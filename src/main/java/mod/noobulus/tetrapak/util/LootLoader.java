package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = BuildConfig.MODID)
public class LootLoader {
	private static final Random rand = new Random();
	private static final int STATISTICAL_TEST = 100; // Values tested to determine min and max
	@Nullable
	private static MinecraftServer server = null;
	private static LootTableManager manager;

	private LootLoader() {
	}

	@SubscribeEvent
	public static void onServerStart(FMLServerStartedEvent event) {
		server = event.getServer();
	}

	@Nullable
	private static MinecraftServer getServer() {
		if (server == null) {
			return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().getSingleplayerServer(), () -> () -> null);
		}
		return server;
	}

	public static LootTableManager getManager() {
		MinecraftServer server = getServer();
		if (server == null) {
			if (manager == null) {
				manager = new LootTableManager(new LootPredicateManager());
				TetraPak.LOGGER.error("Loot tables should never be calculated on the client!");
			}
			return manager;
		}
		return server.getLootTables();
	}

	public static List<LootSlot> crawlTable(LootTable table, LootTableManager manager) {
		List<LootSlot> drops = new ArrayList<>();

		getPools(table).forEach(
			pool -> {
				int min = getMin(pool.getRolls());
				int max = getMax(pool.getRolls()) + getMax(pool.getBonusRolls());
				final float totalWeight = getLootEntries(pool).stream()
					.filter(StandaloneLootEntry.class::isInstance).map(StandaloneLootEntry.class::cast)
					.mapToInt(entry -> entry.weight).sum();
				getLootEntries(pool).stream()
					.filter(ItemLootEntry.class::isInstance).map(ItemLootEntry.class::cast)
					.map(entry -> new LootSlot(entry.item, entry.weight / totalWeight, min, max))
					.forEach(drops::add);

				getLootEntries(pool).stream()
					.filter(TableLootEntry.class::isInstance).map(TableLootEntry.class::cast)
					.map(entry -> crawlTable(manager.get(entry.name), manager)).forEach(drops::addAll);
			}
		);

		drops.removeIf(Objects::isNull);
		return drops;
	}

	public static List<LootPool> getPools(LootTable table) {
		// public net.minecraft.loot.LootTable field_186466_c # pools
		return ObfuscationReflectionHelper.getPrivateValue(LootTable.class, table, "field_186466_c");
	}

	public static List<LootEntry> getLootEntries(LootPool pool) {
		// public net.minecraft.loot.LootPool field_186453_a # lootEntries
		return ObfuscationReflectionHelper.getPrivateValue(LootPool.class, pool, "field_186453_a");
	}

	public static int getMin(IRandomRange randomRange) {
		if (randomRange instanceof ConstantRange) {
			return randomRange.getInt(rand);
		} else if (randomRange instanceof RandomValueRange) {
			return MathHelper.floor(((RandomValueRange) randomRange).getMin());
		} else if (randomRange instanceof BinomialRange) {
			return 0;
		} else {
			// Test a 100 values
			return IntStream.iterate(0, i -> randomRange.getInt(rand)).limit(STATISTICAL_TEST).min().orElse(0);
		}
	}

	public static int getMax(IRandomRange randomRange) {
		if (randomRange instanceof ConstantRange) {
			return randomRange.getInt(rand);
		} else if (randomRange instanceof RandomValueRange) {
			return MathHelper.floor(((RandomValueRange) randomRange).getMax());
		} else if (randomRange instanceof BinomialRange) {
			return ((BinomialRange) randomRange).n;
		} else {
			// Test a 100 values
			return IntStream.iterate(0, i -> randomRange.getInt(rand)).limit(STATISTICAL_TEST).max().orElse(0);
		}
	}

	public static class LootSlot implements Supplier<ITextComponent> {
		public final Item item;
		public final int min;
		public final int max;
		public final float chance;

		public LootSlot(Item item, float chance, int min, int max) {
			this.item = item;
			this.min = min;
			this.max = max;
			this.chance = chance;
		}

		public LootSlot(PacketBuffer buffer) {
			ItemStack item = buffer.readItem();
			this.item = item.getItem();
			min = item.getCount();
			max = buffer.readInt();
			chance = buffer.readFloat();
		}

		public ItemStack asStack() {
			return new ItemStack(item, min);
		}

		public String toString() {
			if (min == max) return min + getDropChance();
			return min + "-" + max + getDropChance();
		}

		private String getDropChance() {
			return chance < 1F ? " (" + formatChance() + "%)" : "";
		}

		private String formatChance() {
			float chance = this.chance * 100;
			if (chance < 10) return String.format("%.1f", chance);
			return String.format("%2d", (int) chance);
		}

		@Override
		public ITextComponent get() {
			return new StringTextComponent(this.toString());
		}

		public void toBuffer(PacketBuffer buffer) {
			buffer.writeItem(new ItemStack(item, min));
			buffer.writeInt(max);
			buffer.writeFloat(chance);
		}
	}
}
