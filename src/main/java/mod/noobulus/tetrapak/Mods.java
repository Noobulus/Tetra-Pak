package mod.noobulus.tetrapak;

import mod.noobulus.tetrapak.create.NullifyingEffect;
import mod.noobulus.tetrapak.create.VoidingEffect;
import mod.noobulus.tetrapak.create.refined_radiance.CollapsingEffect;
import mod.noobulus.tetrapak.create.refined_radiance.DeforestingEffect;
import mod.noobulus.tetrapak.create.refined_radiance.FloatingEffect;
import mod.noobulus.tetrapak.create.refined_radiance.UnearthingEffect;
import mod.noobulus.tetrapak.druidcraft.MoonsightEffect;
import mod.noobulus.tetrapak.druidcraft.MoonstrikeEffect;
import mod.noobulus.tetrapak.druidcraft.RegrowthEffect;
import mod.noobulus.tetrapak.druidcraft.ScorchingEffect;
import mod.noobulus.tetrapak.quark.CorundumEffect;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ILootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Mods {
	CREATE("create", CollapsingEffect::new, DeforestingEffect::new, UnearthingEffect::new, NullifyingEffect::new, VoidingEffect::new, () -> FloatingEffect.INSTANCE),
	DRUIDCRAFT("druidcraft", MoonstrikeEffect::new, MoonsightEffect::new, RegrowthEffect::new, ScorchingEffect::new),
	QUARK("quark", CorundumEffect::new);

	public final boolean isLoaded;
	private final Set<ITetraEffect> loadedListeners = new HashSet<>();

	@SafeVarargs
	Mods(String modid, Supplier<ITetraEffect>... eventListeners) {
		isLoaded = ModList.get().isLoaded(modid);
		if (isLoaded) {
			Arrays.stream(eventListeners)
				.map(Supplier::get)
				.forEach(loadedListeners::add);
		}
	}

	private static Stream<ITetraEffect> getLoadedListenersStream() {
		return Arrays.stream(Mods.values())
			.flatMap(Mods::getLoadedListeners);
	}

	public static void registerEventListeners() {
		getLoadedListenersStream()
			.forEach(MinecraftForge.EVENT_BUS::register);
		MinecraftForge.EVENT_BUS.register(Mods.class);
	}

	public static void clientSetup(FMLClientSetupEvent event) {
		getLoadedListenersStream()
			.filter(IHoloDescription.class::isInstance)
			.map(IHoloDescription.class::cast)
			.forEach(IHoloDescription::clientInit);
	}

	public static void registerLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		getLoadedListenersStream()
			.filter(ILootModifier.class::isInstance)
			.map(ILootModifier.class::cast)
			.map(ILootModifier::getModifier)
			.forEach(event.getRegistry()::register);
	}

	@SubscribeEvent
	public static void handlebeltTick(LivingEvent.LivingUpdateEvent event) {
		if (!(event.getEntityLiving() instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) event.getEntityLiving();
		ItemStack belt = ToolbeltHelper.findToolbelt(player);

		getLoadedListenersStream()
			.forEach(tetraEffect -> tetraEffect.doBeltTick(player, belt));
	}

	public Stream<ITetraEffect> getLoadedListeners() {
		return loadedListeners.stream();
	}
}
