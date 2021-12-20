package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.loot.modifier.ScorchingLootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ILootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.function.Function;

public class ScorchingEffect implements IHoloDescription, ILootModifier<ScorchingLootModifier> {
	@Override
	public Function<LootItemCondition[], ScorchingLootModifier> getModifierConstructor() {
		return ScorchingLootModifier::new;
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("scorching");
	}
}