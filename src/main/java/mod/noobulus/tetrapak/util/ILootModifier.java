package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.loot.GenericModifierSerializer;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Function;

public interface ILootModifier<T extends LootModifier> extends ITetraEffect {
	default GlobalLootModifierSerializer<T> getModifier() {
		return new GenericModifierSerializer<>(getModifierConstructor()).setRegistryName(new ResourceLocation(getEffect().getKey()));
	}

	Function<ILootCondition[], T> getModifierConstructor();
}
