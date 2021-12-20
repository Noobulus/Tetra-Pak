package mod.noobulus.tetrapak.util.tetra_definitions;

import mod.noobulus.tetrapak.loot.GenericModifierSerializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Function;

public interface ILootModifier<T extends LootModifier> extends ITetraEffect {
	default GlobalLootModifierSerializer<T> getModifier() {
		return new GenericModifierSerializer<>(getModifierConstructor()).setRegistryName(new ResourceLocation(getEffect().getKey()));
	}

	Function<LootItemCondition[], T> getModifierConstructor();
}
