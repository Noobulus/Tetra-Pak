package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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

import net.minecraft.world.level.storage.loot.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = BuildConfig.MODID)
public class LootLoader {
	private static final Random rand = new Random();
	private static final int STATISTICAL_TEST = 100; // Values tested to determine min and max
	@Nullable
	private static MinecraftServer server = null;
	private static LootTables manager;

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

	public static LootTables getManager() {
		MinecraftServer server = getServer();
		if (server == null) {
			if (manager == null) {
				manager = new LootTables(new PredicateManager());
				TetraPak.LOGGER.error("Loot tables should never be calculated on the client!");
			}
			return manager;
		}
		return server.getLootTables();
	}

	public static List<LootSlot> crawlTable(LootTable table, LootTables manager) {
		List<LootSlot> drops = new ArrayList<>();

		getPools(table).forEach(
			pool -> {
				int min = getMin(pool.getRolls());
				int max = getMax(pool.getRolls()) + getMax(pool.getBonusRolls());
				final float totalWeight = getLootEntries(pool).stream()
					.filter(LootPoolSingletonContainer.class::isInstance).map(LootPoolSingletonContainer.class::cast)
					.mapToInt(entry -> entry.weight).sum();
				getLootEntries(pool).stream()
					.filter(LootItem.class::isInstance).map(LootItem.class::cast)
					.map(entry -> new LootSlot(entry.item, entry.weight / totalWeight, min, max))
					.forEach(drops::add);

				getLootEntries(pool).stream()
					.filter(LootTableReference.class::isInstance).map(LootTableReference.class::cast)
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

	public static List<LootPoolEntryContainer> getLootEntries(LootPool pool) {
		// public net.minecraft.loot.LootPool field_186453_a # lootEntries
		return ObfuscationReflectionHelper.getPrivateValue(LootPool.class, pool, "field_186453_a");
	}

	public static int getMin(RandomIntGenerator randomRange) {
		if (randomRange instanceof ConstantIntValue) {
			return randomRange.getInt(rand);
		} else if (randomRange instanceof RandomValueBounds) {
			return Mth.floor(((RandomValueBounds) randomRange).getMin());
		} else if (randomRange instanceof BinomialDistributionGenerator) {
			return 0;
		} else {
			// Test a 100 values
			return IntStream.iterate(0, i -> randomRange.getInt(rand)).limit(STATISTICAL_TEST).min().orElse(0);
		}
	}

	public static int getMax(RandomIntGenerator randomRange) {
		if (randomRange instanceof ConstantIntValue) {
			return randomRange.getInt(rand);
		} else if (randomRange instanceof RandomValueBounds) {
			return Mth.floor(((RandomValueBounds) randomRange).getMax());
		} else if (randomRange instanceof BinomialDistributionGenerator) {
			return ((BinomialDistributionGenerator) randomRange).n;
		} else {
			// Test a 100 values
			return IntStream.iterate(0, i -> randomRange.getInt(rand)).limit(STATISTICAL_TEST).max().orElse(0);
		}
	}

	public static class LootSlot implements Supplier<Component> {
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

		public LootSlot(FriendlyByteBuf buffer) {
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
		public Component get() {
			return new TextComponent(this.toString());
		}

		public void toBuffer(FriendlyByteBuf buffer) {
			buffer.writeItem(new ItemStack(item, min));
			buffer.writeInt(max);
			buffer.writeFloat(chance);
		}
	}
}
