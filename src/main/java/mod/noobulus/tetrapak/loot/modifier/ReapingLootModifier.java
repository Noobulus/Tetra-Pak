package mod.noobulus.tetrapak.loot.modifier;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class ReapingLootModifier extends LootModifier {
	public ReapingLootModifier(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Nonnull
	@Override
	public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.clear();
		generatedLoot.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("eidolon", "soul_shard")),
			context.getRandom().nextInt(2 + context.getLootingModifier())));
		return generatedLoot;
	}
}