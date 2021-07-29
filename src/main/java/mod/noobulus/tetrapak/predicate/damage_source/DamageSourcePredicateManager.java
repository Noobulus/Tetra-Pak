package mod.noobulus.tetrapak.predicate.damage_source;

import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.predicate.AbstractPredicateManager;
import net.minecraftforge.registries.IForgeRegistry;

public class DamageSourcePredicateManager extends AbstractPredicateManager<AbstractDamageSourcePredicate> {
	public static final DamageSourcePredicateManager INSTANCE = new DamageSourcePredicateManager();

	@Override
	public void registerContents(IForgeRegistry<AbstractDamageSourcePredicate> registry) {
		registry.register(new TestWeaponPredicate().setRegistryName(TetraPak.asId("weapon")));
	}

	@Override
	protected Class<AbstractDamageSourcePredicate> getPredicateClass() {
		return AbstractDamageSourcePredicate.class;
	}

	@Override
	protected String getRegistryName() {
		return "damagesourcepredicates";
	}
}