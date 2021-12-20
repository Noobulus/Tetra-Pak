package mod.noobulus.tetrapak.loot.modifier;

import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class ScorchingLootModifier extends LootModifier {
	public ScorchingLootModifier(LootItemCondition[] conditionsIn) {
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
