package mod.noobulus.tetrapak.druidcraft;

import com.google.gson.JsonObject;
import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
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
		return generatedLoot.stream().map(stack -> ItemHelper.smelt(stack, context.getWorld())).collect(Collectors.toList());
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
