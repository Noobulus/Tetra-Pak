package mod.noobulus.tetrapak;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class FieryGlassScorchDropsModifier extends LootModifier {

	public FieryGlassScorchDropsModifier(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	private static ItemStack smelt(ItemStack stack, World world) { // this is just forge example code switched over to my mappings but we won't talk about that
		return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), world)
			.map(FurnaceRecipe::getRecipeOutput)
			.filter(itemStack -> !itemStack.isEmpty())
			.map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
			.orElse(stack);
	}

	@Nonnull
	@Override
	public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		if (context.has(LootParameters.BLOCK_ENTITY)) //don't smelt TE drops
			return generatedLoot;
		Entity killerEntity = context.get(LootParameters.KILLER_ENTITY);
		return applyFor(context.get(LootParameters.THIS_ENTITY) instanceof PlayerEntity || killerEntity == null ?
			context.get(LootParameters.TOOL) :
			killerEntity.getHeldEquipment().iterator().next(), generatedLoot, context.getWorld());
	}

	private List<ItemStack> applyFor(ItemStack tool, List<ItemStack> toSmelt, World world) {
		if (ItemHelper.getEffectLevel(tool, ScorchingEffect.SCORCHING_EFFECT) > 0) {
			return toSmelt.stream().map(stack -> smelt(stack, world)).collect(Collectors.toList());
		}
		return toSmelt;
	}

	// JSON serializer stuff for loot modifiers
	public static class Serializer extends GlobalLootModifierSerializer<FieryGlassScorchDropsModifier> {

		@Override
		public FieryGlassScorchDropsModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
			return new FieryGlassScorchDropsModifier(conditionsIn);
		}

		@Override
		public JsonObject write(FieryGlassScorchDropsModifier instance) {
			return null;
		}
	}
}