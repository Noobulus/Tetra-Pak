package mod.noobulus.tetrapak.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HasBlockEntity implements LootItemCondition {
	@Override
	public LootItemConditionType getType() {
		return LootConditions.HAS_BLOCK_ENTITY.type;
	}

	@Override
	public boolean test(LootContext context) {
		return context.hasParam(LootContextParams.BLOCK_ENTITY);
	}

	public static class Serializer implements Serializer<HasBlockEntity> {
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
