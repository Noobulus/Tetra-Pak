package mod.noobulus.tetrapak.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HasBlockEntity implements ILootCondition {
	@Override
	public LootConditionType getType() {
		return LootConditions.HAS_BLOCK_ENTITY.type;
	}

	@Override
	public boolean test(LootContext context) {
		return context.hasParam(LootParameters.BLOCK_ENTITY);
	}

	public static class Serializer implements ILootSerializer<HasBlockEntity> {
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
