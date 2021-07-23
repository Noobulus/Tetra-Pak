package mod.noobulus.tetrapak.jei;

import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DynamicJeiCompat {
	CREATE("create", () -> AutoSalvageCategory::new);

	public final boolean isLoaded;
	private final Set<CompatJeiRecipe<?>> loadedCategories = new HashSet<>();

	@SafeVarargs
	DynamicJeiCompat(String modid, Supplier<Supplier<CompatJeiRecipe<?>>>... recipes) {
		isLoaded = ModList.get().isLoaded(modid);
		if (isLoaded) {
			Arrays.stream(recipes)
				.map(Supplier::get)
				.map(Supplier::get)
				.forEach(loadedCategories::add);
		}
	}

	public static Set<CompatJeiRecipe<?>> getAllLoadedCategories() {
		return Arrays.stream(values())
			.filter(DynamicJeiCompat::isLoaded)
			.flatMap(DynamicJeiCompat::getLoadedCategories)
			.collect(Collectors.toSet());
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	private Stream<CompatJeiRecipe<?>> getLoadedCategories() {
		return loadedCategories.stream();
	}
}
