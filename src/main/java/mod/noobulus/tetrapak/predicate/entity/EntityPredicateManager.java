package mod.noobulus.tetrapak.predicate.entity;

import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.predicate.AbstractPredicateManager;
import net.minecraftforge.registries.IForgeRegistry;

public class EntityPredicateManager extends AbstractPredicateManager<AbstractEntityPredicate> {
	public static final EntityPredicateManager INSTANCE = new EntityPredicateManager();

	@Override
	public void registerContents(IForgeRegistry<AbstractEntityPredicate> registry) {
		registry.register(new CreatureAttributePredicate().setRegistryName(TetraPak.asId("creature_attribute")));
	}

	@Override
	protected Class<AbstractEntityPredicate> getPredicateClass() {
		return AbstractEntityPredicate.class;
	}

	@Override
	protected String getRegistryName() {
		return "entitypredicates";
	}
}
