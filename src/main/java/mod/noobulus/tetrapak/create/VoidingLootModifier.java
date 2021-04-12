package mod.noobulus.tetrapak.create;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class VoidingLootModifier extends LootModifier {
	public VoidingLootModifier(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Nonnull
	@Override
	public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.clear();
		return generatedLoot;
	}

	// JSON serializer stuff for loot modifiers
	public static class Serializer extends GlobalLootModifierSerializer<VoidingLootModifier> {

		@Override
		public VoidingLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
			return new VoidingLootModifier(conditionsIn);
		}

		@Override
		public JsonObject write(VoidingLootModifier instance) {
			return null;
		}
	}
}
