package mod.noobulus.tetrapak.quark;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static net.minecraft.world.level.material.MaterialColor.*;

public final class CorundumMap {
	public static final Map<MaterialColor, Integer> COLOR_MAP = new HashMap<>();
	public static final Map<Integer, ResourceLocation> CRYSTAL_MAP = new HashMap<>();
	public static final Map<Integer, String> NAME_MAP = new HashMap<>();
	private static final int RED = 1;
	private static final int ORANGE = 2;
	private static final int YELLOW = 3;
	private static final int GREEN = 4;
	private static final int BLUE = 5;
	private static final int INDIGO = 6;
	private static final int VIOLET = 7;
	private static final int WHITE = 8;
	private static final int BLACK = 9;

	static {
		COLOR_MAP.put(NONE, BLACK);
		COLOR_MAP.put(GRASS, GREEN);
		COLOR_MAP.put(SAND, YELLOW);
		COLOR_MAP.put(WOOL, WHITE);
		COLOR_MAP.put(FIRE, RED);
		COLOR_MAP.put(ICE, INDIGO);
		COLOR_MAP.put(METAL, WHITE);
		COLOR_MAP.put(PLANT, GREEN);
		COLOR_MAP.put(SNOW, WHITE);
		COLOR_MAP.put(CLAY, WHITE);
		COLOR_MAP.put(DIRT, ORANGE);
		COLOR_MAP.put(STONE, BLACK);
		COLOR_MAP.put(WATER, BLUE);
		COLOR_MAP.put(WOOD, ORANGE);
		COLOR_MAP.put(QUARTZ, WHITE);
		COLOR_MAP.put(COLOR_ORANGE, ORANGE);
		COLOR_MAP.put(COLOR_MAGENTA, INDIGO);
		COLOR_MAP.put(COLOR_LIGHT_BLUE, BLUE);
		COLOR_MAP.put(COLOR_YELLOW, YELLOW);
		COLOR_MAP.put(COLOR_LIGHT_GREEN, GREEN);
		COLOR_MAP.put(COLOR_PINK, VIOLET);
		COLOR_MAP.put(COLOR_GRAY, BLACK);
		COLOR_MAP.put(COLOR_LIGHT_GRAY, WHITE);
		COLOR_MAP.put(COLOR_CYAN, BLUE);
		COLOR_MAP.put(COLOR_PURPLE, INDIGO);
		COLOR_MAP.put(COLOR_BLUE, BLUE);
		COLOR_MAP.put(COLOR_BROWN, ORANGE);
		COLOR_MAP.put(COLOR_GREEN, GREEN);
		COLOR_MAP.put(COLOR_RED, RED);
		COLOR_MAP.put(COLOR_BLACK, BLACK);
		COLOR_MAP.put(GOLD, YELLOW);
		COLOR_MAP.put(DIAMOND, BLUE);
		COLOR_MAP.put(LAPIS, BLUE);
		COLOR_MAP.put(EMERALD, GREEN);
		COLOR_MAP.put(PODZOL, ORANGE);
		COLOR_MAP.put(NETHER, RED);
		COLOR_MAP.put(TERRACOTTA_WHITE, WHITE);
		COLOR_MAP.put(TERRACOTTA_ORANGE, ORANGE);
		COLOR_MAP.put(TERRACOTTA_MAGENTA, INDIGO);
		COLOR_MAP.put(TERRACOTTA_LIGHT_BLUE, BLUE);
		COLOR_MAP.put(TERRACOTTA_YELLOW, YELLOW);
		COLOR_MAP.put(TERRACOTTA_LIGHT_GREEN, GREEN);
		COLOR_MAP.put(TERRACOTTA_PINK, VIOLET);
		COLOR_MAP.put(TERRACOTTA_GRAY, BLACK);
		COLOR_MAP.put(TERRACOTTA_LIGHT_GRAY, WHITE);
		COLOR_MAP.put(TERRACOTTA_CYAN, BLUE);
		COLOR_MAP.put(TERRACOTTA_PURPLE, INDIGO);
		COLOR_MAP.put(TERRACOTTA_BLUE, BLUE);
		COLOR_MAP.put(TERRACOTTA_BROWN, ORANGE);
		COLOR_MAP.put(TERRACOTTA_GREEN, GREEN);
		COLOR_MAP.put(TERRACOTTA_RED, RED);
		COLOR_MAP.put(TERRACOTTA_BLACK, BLACK);
		COLOR_MAP.put(CRIMSON_NYLIUM, RED);
		COLOR_MAP.put(CRIMSON_STEM, RED);
		COLOR_MAP.put(CRIMSON_HYPHAE, RED);
		COLOR_MAP.put(WARPED_NYLIUM, BLUE);
		COLOR_MAP.put(WARPED_STEM, BLUE);
		COLOR_MAP.put(WARPED_HYPHAE, BLUE);
		COLOR_MAP.put(WARPED_WART_BLOCK, BLUE);

		CRYSTAL_MAP.put(RED, asQuarkCrystal("red"));
		CRYSTAL_MAP.put(ORANGE, asQuarkCrystal("orange"));
		CRYSTAL_MAP.put(YELLOW, asQuarkCrystal("yellow"));
		CRYSTAL_MAP.put(GREEN, asQuarkCrystal("green"));
		CRYSTAL_MAP.put(BLUE, asQuarkCrystal("blue"));
		CRYSTAL_MAP.put(INDIGO, asQuarkCrystal("indigo"));
		CRYSTAL_MAP.put(VIOLET, asQuarkCrystal("violet"));
		CRYSTAL_MAP.put(WHITE, asQuarkCrystal("white"));
		CRYSTAL_MAP.put(BLACK, asQuarkCrystal("black"));


		NAME_MAP.put(RED, "red");
		NAME_MAP.put(ORANGE, "orange");
		NAME_MAP.put(YELLOW, "yellow");
		NAME_MAP.put(GREEN, "green");
		NAME_MAP.put(BLUE, "blue");
		NAME_MAP.put(INDIGO, "indigo");
		NAME_MAP.put(VIOLET, "violet");
		NAME_MAP.put(WHITE, "white");
		NAME_MAP.put(BLACK, "black");
	}


	private CorundumMap() {
	}

	private static ResourceLocation asQuarkCrystal(String color) {
		return new ResourceLocation("quark", color + "_crystal_cluster");
	}

	public static void checkMappings() {
		TetraPak.LOGGER.info("Checking corundum color mappings...");
		Arrays.stream(MATERIAL_COLORS)
			.filter(((Predicate<MaterialColor>) COLOR_MAP::containsKey).negate())
			.filter(Objects::nonNull)
			.forEach(materialColor -> TetraPak.LOGGER.error("Material color {} of id {} and colour {} was not mapped to a corundum variant",
				materialColor, materialColor.id, materialColor.col));
	}
}
