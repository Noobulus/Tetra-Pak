package mod.noobulus.tetrapak.druidcraft;

import com.google.gson.JsonObject;
import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
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
		if (context.has(LootParameters.BLOCK_ENTITY)) //don't smelt TE drops
			return generatedLoot;
		Entity killerEntity = context.get(LootParameters.KILLER_ENTITY);
		return applyFor(context.get(LootParameters.THIS_ENTITY) instanceof PlayerEntity || killerEntity == null ?
			context.get(LootParameters.TOOL) :
			killerEntity.getHeldEquipment().iterator().next(), generatedLoot, context.getWorld());
	}

	private List<ItemStack> applyFor(ItemStack tool, List<ItemStack> toSmelt, World world) {
		if (ItemHelper.getEffectLevel(tool, ScorchingEffect.SCORCHING_EFFECT) > 0) {
			return toSmelt.stream().map(stack -> ItemHelper.smelt(stack, world)).collect(Collectors.toList());
		}
		return toSmelt;
	}

	// JSON serializer stuff for loot modifiers
	public static class Serializer extends GlobalLootModifierSerializer<ScorchingLootModifier> {

		@Override
		public ScorchingLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
			return new ScorchingLootModifier(conditionsIn);
		}

		@Override
		public JsonObject write(ScorchingLootModifier instance) {
			return null;
		}
	}
}
