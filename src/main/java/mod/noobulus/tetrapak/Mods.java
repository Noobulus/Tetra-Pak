package mod.noobulus.tetrapak;

import mod.noobulus.tetrapak.effects.base.ExpBoostEffect;
import mod.noobulus.tetrapak.effects.base.RegrowthEffect;
import mod.noobulus.tetrapak.effects.base.ScorchingEffect;
import mod.noobulus.tetrapak.effects.create.NullifyingEffect;
import mod.noobulus.tetrapak.effects.create.SolidifyingEffect;
import mod.noobulus.tetrapak.effects.create.StandardTetraPakAttributes;
import mod.noobulus.tetrapak.effects.create.VoidingEffect;
import mod.noobulus.tetrapak.effects.create.refined_radiance.CollapsingEffect;
import mod.noobulus.tetrapak.effects.create.refined_radiance.DeforestingEffect;
import mod.noobulus.tetrapak.effects.create.refined_radiance.FloatingEffect;
import mod.noobulus.tetrapak.effects.create.refined_radiance.UnearthingEffect;
import mod.noobulus.tetrapak.effects.druidcraft.MoonsightEffect;
import mod.noobulus.tetrapak.effects.druidcraft.MoonstrikeEffect;
import mod.noobulus.tetrapak.effects.eidolon.CleavingEffect;
import mod.noobulus.tetrapak.effects.eidolon.ReapingEffect;
import mod.noobulus.tetrapak.effects.quark.CorundumEffect;
import mod.noobulus.tetrapak.effects.supplementaries.BubblingEffect;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Mods {
	TETRA("tetra", () -> RegrowthEffect::new, () -> ScorchingEffect::new, () -> ExpBoostEffect::new), // useful effects outside of specific compat
	CREATE("create", () -> CollapsingEffect::new, () -> DeforestingEffect::new, () -> UnearthingEffect::new, () -> NullifyingEffect::new, () -> VoidingEffect::new, () -> SolidifyingEffect::new, () -> () -> FloatingEffect.INSTANCE, () -> StandardTetraPakAttributes::register),
	DRUIDCRAFT("druidcraft", () -> MoonstrikeEffect::new, () -> MoonsightEffect::new),
	QUARK("quark", () -> CorundumEffect::new),
	EIDOLON("eidolon", () -> ReapingEffect::new, () -> CleavingEffect::new),
	SUPPLEMENTARIES("supplementaries", () -> BubblingEffect::new);

	public final boolean isLoaded;
	private final Set<Object> loadedListeners = new HashSet<>();

	@SafeVarargs
	Mods(String modid, Supplier<Supplier<Object>>... eventListeners) {
		isLoaded = ModList.get().isLoaded(modid);
		if (isLoaded) {
			Arrays.stream(eventListeners)
				.map(Supplier::get)
				.map(Supplier::get)
				.filter(Objects::nonNull)
				.forEach(loadedListeners::add);
		}
	}

	private static Stream<Object> getLoadedListenersStream() {
		return Arrays.stream(Mods.values())
			.flatMap(mods -> mods.loadedListeners.stream());
	}

	public static void registerEventListeners() {
		getLoadedListenersStream()
			.filter(IEventBusListener.class::isInstance)
			.map(IEventBusListener.class::cast)
			.forEach(IEventBusListener::register);
		MinecraftForge.EVENT_BUS.register(Mods.class);
	}

	public static void clientSetup(FMLClientSetupEvent event) {
		getLoadedListenersStream()
			.filter(IHoloDescription.class::isInstance)
			.map(IHoloDescription.class::cast)
			.forEach(IHoloDescription::clientInit);
	}

	@SubscribeEvent
	public static void handlebeltTick(LivingEvent.LivingUpdateEvent event) {
		if (!(event.getEntityLiving() instanceof Player player))
			return;
		ItemStack belt = ToolbeltHelper.findToolbelt(player);

		getLoadedListenersStream()
			.filter(ITetraEffect.class::isInstance)
			.map(ITetraEffect.class::cast)
			.forEach(tetraEffect -> tetraEffect.doBeltTick(player, belt));
	}

}
