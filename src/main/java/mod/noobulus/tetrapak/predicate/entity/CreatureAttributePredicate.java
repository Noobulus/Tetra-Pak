package mod.noobulus.tetrapak.predicate.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.GsonHelper;

import java.util.function.Predicate;

public class CreatureAttributePredicate extends AbstractEntityPredicate {
	@Override
	protected Predicate<Entity> readInternal(JsonObject jsonobject) throws JsonSyntaxException {
		if (!jsonobject.has("attribute"))
			return null;
		String attribute = GsonHelper.getAsString(jsonobject, "attribute");
		switch (attribute) {
			case "undefined":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(MobType.UNDEFINED);
			case "undead":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(MobType.UNDEAD);
			case "arthropod":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(MobType.ARTHROPOD);
			case "illager":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(MobType.ILLAGER);
			case "water":
				return entity -> entity instanceof LivingEntity && ((LivingEntity) entity).getMobType().equals(MobType.WATER);
			default:
				return null;
		}
	}
}
