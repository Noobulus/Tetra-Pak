package mod.noobulus.tetrapak.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import mod.noobulus.tetrapak.registries.LootConditions;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HasBlockEntity implements LootItemCondition {
	@Override
	public LootItemConditionType getType() {
		return LootConditions.HAS_BLOCK_ENTITY.get();
	}

	@Override
	public boolean test(LootContext context) {
		return context.hasParam(LootContextParams.BLOCK_ENTITY);
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<HasBlockEntity> {
		@Override
		public void serialize(JsonObject json, HasBlockEntity condition, JsonSerializationContext context) {
			// No extra data required
		}

		@Override
		public HasBlockEntity deserialize(JsonObject json, JsonDeserializationContext context) {
			return new HasBlockEntity();
		}
	}
}
