package mod.noobulus.tetrapak.loot;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.ResourceLocation;

public enum LootConditions {
	HAS_BLOCK_ENTITY(TetraPak.asId("has_block_entity"), new HasBlockEntity.Serializer());

	public final LootConditionType type;

	LootConditions(ResourceLocation id, ILootSerializer<? extends ILootCondition> serializer) {
		type = LootConditionManager.register(id.toString(), serializer);
	}

	@SuppressWarnings("EmptyMethod")
	public static void register() {
		// classloading
	}
}
