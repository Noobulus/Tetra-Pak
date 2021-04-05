package mod.noobulus.tetrapak;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

import javax.annotation.Nonnull;
import java.util.List;

public class ShadowSteelVoidingLootModifier extends LootModifier {
    private static final ItemEffect voiding = ItemEffect.get("tetrapak:voiding");

    public ShadowSteelVoidingLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (context.has(LootParameters.BLOCK_ENTITY)) // because deleting a shulker full of items from one screw-up is unfun
            return generatedLoot;
        ItemStack heldItem = context.get(LootParameters.TOOL);
        assert heldItem != null; // NPE-B-GONE
        ModularItem heldModularitem = (ModularItem) heldItem.getItem();
        int level = heldModularitem.getEffectLevel(heldItem, voiding);
        if (level > 0) {
            generatedLoot.clear();
        }
        return generatedLoot;
    }

    // JSON serializer stuff for loot modifiers
    public static class Serializer extends GlobalLootModifierSerializer<ShadowSteelVoidingLootModifier> {

        @Override
        public ShadowSteelVoidingLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new ShadowSteelVoidingLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(ShadowSteelVoidingLootModifier instance) {
            return null;
        }
    }
}