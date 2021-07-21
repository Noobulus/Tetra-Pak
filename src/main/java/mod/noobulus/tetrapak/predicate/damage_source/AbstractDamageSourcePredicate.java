package mod.noobulus.tetrapak.predicate.damage_source;

import mod.noobulus.tetrapak.predicate.AbstractPredicate;
import net.minecraft.util.DamageSource;

public abstract class AbstractDamageSourcePredicate extends AbstractPredicate<DamageSource, AbstractDamageSourcePredicate> {
	@Override
	protected String getKey() {
		return "damage type";
	}
}
