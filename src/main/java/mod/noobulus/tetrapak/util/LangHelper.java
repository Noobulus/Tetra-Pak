package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.util.text.TranslationTextComponent;

public class LangHelper {
	public static String translate(String key) {
		return new TranslationTextComponent(BuildConfig.MODID + "." + key).getString();
	}
}
