package mod.noobulus.tetrapak;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.DoubleValue MATCHING_CRYSTAL_FACTOR;
	public static final ForgeConfigSpec.DoubleValue NON_MATCHING_CRYSTAL_FACTOR;
	public static final ForgeConfigSpec.IntValue MAX_RADIANT_BLOCKS;


	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		// Quark
		commonBuilder.comment("Quark compatibility settings").push("quark");
		MATCHING_CRYSTAL_FACTOR = commonBuilder.comment("Matching Crystal color mining speed factor")
			.defineInRange("matching_crystal_factor", 1.5f, 0f, 10);
		NON_MATCHING_CRYSTAL_FACTOR = commonBuilder.comment("Non-matching Crystal color mining speed factor")
			.defineInRange("non_matching_crystal_factor", 0.5f, 0f, 10);
		commonBuilder.pop();

		// Create
		commonBuilder.comment("Create compatibility settings").push("create");
		MAX_RADIANT_BLOCKS = commonBuilder.comment("Max block count for Refined Radiance mass harvesting")
			.defineInRange("max_radiant_blocks", 512, 0, 4096);
		commonBuilder.pop();

		// Druidcraft
		// ...

		COMMON_CONFIG = commonBuilder.build();
	}

	private Config() {
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		configData.load();
		spec.setConfig(configData);
	}
}
