package mod.noobulus.tetrapak.predicate.entity;

import mod.noobulus.tetrapak.predicate.AbstractPredicate;
import net.minecraft.world.entity.Entity;

public abstract class AbstractEntityPredicate extends AbstractPredicate<Entity, AbstractEntityPredicate> {
	@Override
	protected String getKey() {
		return "entity";
	}
}
