package mod.noobulus.tetrapak.loot;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public enum LootConditions {
	HAS_BLOCK_ENTITY(TetraPak.asId("has_block_entity"), new HasBlockEntity.Serializer());

	public final LootItemConditionType type;

	LootConditions(ResourceLocation id, Serializer<? extends LootItemCondition> serializer) {
		type = LootItemConditions.register(id.toString(), serializer);
	}

	@SuppressWarnings("EmptyMethod")
	public static void register() {
		// classloading
	}
}
