package mod.noobulus.tetrapak.loot.modifier;

import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class ScorchingLootModifier extends LootModifier {
	public ScorchingLootModifier(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Nonnull
	@Override
	public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		return generatedLoot.stream()
			.map(stack -> ItemHelper.smelt(stack, context.getLevel()))
			.collect(Collectors.toList());
	}
}
