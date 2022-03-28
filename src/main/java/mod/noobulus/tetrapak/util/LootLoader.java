package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.mixin.LootPoolAccessorMixin;
import mod.noobulus.tetrapak.mixin.LootTableAccessorMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = BuildConfig.MODID)
public class LootLoader {
	private static final int STATISTICAL_TEST = 100; // Values tested to determine min and max
	@Nullable
	private static MinecraftServer server = null;
	private static LootTables manager;

	private LootLoader() {
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartedEvent event) {
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
		LootContext dummyContext = new LootContext.Builder(getServer().overworld()).create(new LootContextParamSet.Builder().build());
		List<LootSlot> drops = new ArrayList<>();

		getPools(table).forEach(
			pool -> {
				int min = getMin(pool.getRolls(), dummyContext);
				int max = getMax(pool.getRolls(), dummyContext) + getMax(pool.getBonusRolls(), dummyContext);
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
		return ((LootTableAccessorMixin) (Object) table).getPools();
	}

	public static List<LootPoolEntryContainer> getLootEntries(LootPool pool) {
		return Arrays.asList(((LootPoolAccessorMixin) (Object) pool).getEntries());
	}

	public static int getMin(NumberProvider randomRange, LootContext dummyContext) {
		if (randomRange instanceof ConstantValue) {
			return randomRange.getInt(dummyContext);
		} else if (randomRange instanceof UniformGenerator uniformGenerator) {
			return Mth.floor(uniformGenerator.min.getFloat(dummyContext));
		} else if (randomRange instanceof BinomialDistributionGenerator) {
			return 0;
		} else {
			// Test a 100 values
			return IntStream.iterate(0, i -> randomRange.getInt(dummyContext)).limit(STATISTICAL_TEST).min().orElse(0);
		}
	}

	public static int getMax(NumberProvider randomRange, LootContext dummyContext) {
		if (randomRange instanceof ConstantValue) {
			return randomRange.getInt(dummyContext);
		} else if (randomRange instanceof UniformGenerator uniformGenerator) {
			return Mth.floor(uniformGenerator.max.getFloat(dummyContext));
		} else if (randomRange instanceof BinomialDistributionGenerator binomialDistributionGenerator) {
			return binomialDistributionGenerator.n.getInt(dummyContext);
		} else {
			// Test a 100 values
			return IntStream.iterate(0, i -> randomRange.getInt(dummyContext)).limit(STATISTICAL_TEST).max().orElse(0);
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
