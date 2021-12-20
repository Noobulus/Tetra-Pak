package mod.noobulus.tetrapak.predicate.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class CreatureAttributePredicate extends AbstractEntityPredicate {
	@Nullable
	private static MobType fromString(String attribute) {
		return switch (attribute) {
			case "undefined" -> MobType.UNDEFINED;
			case "undead" -> MobType.UNDEAD;
			case "arthropod" -> MobType.ARTHROPOD;
			case "illager" -> MobType.ILLAGER;
			case "water" -> MobType.WATER;
			default -> null;
		};
	}

	@Override
	protected Predicate<Entity> readInternal(JsonObject jsonobject) throws JsonSyntaxException {
		if (!jsonobject.has("attribute"))
			return null;
		MobType mobType = fromString(GsonHelper.getAsString(jsonobject, "attribute"));
		return entity -> entity instanceof LivingEntity livingEntity && livingEntity.getMobType().equals(mobType);
	}
}
