package mod.noobulus.tetrapak.util;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

public interface ILootModifier<T extends LootModifier> {
	GlobalLootModifierSerializer<T> getModifier();
}
