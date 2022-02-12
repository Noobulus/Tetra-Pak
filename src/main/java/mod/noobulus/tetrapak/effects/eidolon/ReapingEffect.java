package mod.noobulus.tetrapak.effects.eidolon;

import mod.noobulus.tetrapak.loot.modifier.ReapingLootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ILootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.function.Function;

public class ReapingEffect implements IHoloDescription, ILootModifier<ReapingLootModifier> {
	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("reaping");
	}

	@Override
	public Function<LootItemCondition[], ReapingLootModifier> getModifierConstructor() {
		return ReapingLootModifier::new;
	}
}