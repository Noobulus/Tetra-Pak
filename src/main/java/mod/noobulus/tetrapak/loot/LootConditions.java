package mod.noobulus.tetrapak.loot;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;

public enum LootConditions {
	SHOULD_EFFECT_AFFECT(TetraPak.MODID + ":should_effect_affect", new ShouldEffectAffect.Serializer());

	public final LootConditionType type;

	LootConditions(String id, ILootSerializer<? extends ILootCondition> serializer) {
		type = LootConditionManager.register(id, serializer);
	}

	public static void register() {
		// classloading
	}
}
