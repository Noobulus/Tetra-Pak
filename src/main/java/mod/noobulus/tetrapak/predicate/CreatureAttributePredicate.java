package mod.noobulus.tetrapak.predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;

import java.util.function.Predicate;

public class CreatureAttributePredicate extends AbstractEntityPredicate {
	@Override
	protected Predicate<Entity> readInternal(JsonObject jsonobject) throws JsonSyntaxException {
		if (!jsonobject.has("attribute"))
			return entity -> true;
		String attribute = JSONUtils.getAsString(jsonobject, "attribute");
		switch (attribute) {
			case "undefined":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(CreatureAttribute.UNDEFINED);
			case "undead":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(CreatureAttribute.UNDEAD);
			case "arthropod":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(CreatureAttribute.ARTHROPOD);
			case "illager":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(CreatureAttribute.ILLAGER);
			case "water":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(CreatureAttribute.WATER);
			default:
				return entity -> true;
		}
	}
}
