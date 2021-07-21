package mod.noobulus.tetrapak.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.JSONUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AndLootCondition implements ILootCondition {
	private final ILootCondition[] terms;
	private final Predicate<LootContext> composedPredicate;

	public AndLootCondition(ILootCondition[] iLootConditions) {
		this.terms = iLootConditions;
		this.composedPredicate = LootConditionManager.andConditions(iLootConditions);
	}

	@Override
	public LootConditionType getType() {
		return LootConditions.AND.type;
	}

	@Override
	public boolean test(LootContext context) {
		return this.composedPredicate.test(context);
	}

	@Override
	public void validate(ValidationTracker validationTracker) {
		ILootCondition.super.validate(validationTracker);

		for(int i = 0; i < this.terms.length; ++i) {
			this.terms[i].validate(validationTracker.forChild(".term[" + i + "]"));
		}
	}

	public static class Serializer implements ILootSerializer<AndLootCondition> {
		public void serialize(JsonObject jsonObject, AndLootCondition andLootCondition, JsonSerializationContext context) {
			jsonObject.add("terms", context.serialize(andLootCondition.terms));
		}

		public AndLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext context) {
			ILootCondition[] ailootcondition = JSONUtils.getAsObject(jsonObject, "terms", context, ILootCondition[].class);
			return new AndLootCondition(ailootcondition);
		}
	}
}
