package mod.noobulus.tetrapak;

import mod.noobulus.tetrapak.create.CollapsingEffect;
import mod.noobulus.tetrapak.create.DeforestingEffect;
import mod.noobulus.tetrapak.create.NullifyingEffect;
import mod.noobulus.tetrapak.create.VoidingEffect;
import mod.noobulus.tetrapak.druidcraft.MoonstrikeEffect;
import mod.noobulus.tetrapak.druidcraft.RegrowthEffect;
import mod.noobulus.tetrapak.druidcraft.ScorchingEffect;
import mod.noobulus.tetrapak.util.IClientInit;
import mod.noobulus.tetrapak.util.ILootModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Mods {
	CREATE("create", CollapsingEffect::new, DeforestingEffect::new, NullifyingEffect::new, VoidingEffect::new),
	DRUIDCRAFT("druidcraft", MoonstrikeEffect::new, RegrowthEffect::new, ScorchingEffect::new);

	private final Set<Object> loadedListeners = new HashSet<>();

	@SafeVarargs
	Mods(String modid, Supplier<Object>... eventListeners) {
		if (ModList.get().isLoaded(modid)) {
			Arrays.stream(eventListeners).map(Supplier::get).forEach(loadedListeners::add);
		}
	}

	private static Stream<Object> getLoadedListenersStream() {
		return Arrays.stream(Mods.values())
			.flatMap(Mods::getLoadedListeners);
	}

	public static void registerEventListeners() {
		getLoadedListenersStream()
			.forEach(MinecraftForge.EVENT_BUS::register);
	}

	public static void clientSetup(FMLClientSetupEvent event) {
		getLoadedListenersStream()
			.filter(IClientInit.class::isInstance)
			.map(IClientInit.class::cast)
			.forEach(IClientInit::clientInit);
	}

	public static void registerLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		getLoadedListenersStream()
			.filter(ILootModifier.class::isInstance)
			.map(ILootModifier.class::cast)
			.map(ILootModifier::getModifier)
			.forEach(event.getRegistry()::register);
	}

	public Stream<Object> getLoadedListeners() {
		return loadedListeners.stream();
	}
}
