package mod.noobulus.tetrapak.loot;

import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.client.renderer.model.multipart.AndCondition;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;

public enum LootConditions {
	AND(BuildConfig.MODID + "and", new AndLootCondition.Serializer()),
	SHOULD_EFFECT_AFFECT(BuildConfig.MODID + ":should_effect_affect", new ShouldEffectAffect.Serializer());

	public final LootConditionType type;

	LootConditions(String id, ILootSerializer<? extends ILootCondition> serializer) {
		type = LootConditionManager.register(id, serializer);
	}

	@SuppressWarnings("EmptyMethod")
	public static void register() {
		// classloading
	}
}
