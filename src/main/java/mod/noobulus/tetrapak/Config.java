package mod.noobulus.tetrapak;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.IntValue MAX_RADIANT_BLOCKS;


	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

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
