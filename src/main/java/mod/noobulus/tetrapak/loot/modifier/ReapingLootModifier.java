package mod.noobulus.tetrapak.loot.modifier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class ReapingLootModifier extends LootModifier {
	public ReapingLootModifier(LootItemCondition[] conditionsIn) {
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