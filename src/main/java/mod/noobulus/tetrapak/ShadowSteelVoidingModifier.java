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

public class ShadowSteelVoidingModifier extends LootModifier {
    private static final ItemEffect voiding = ItemEffect.get("tetrapak:voiding");

    public ShadowSteelVoidingModifier(ILootCondition[] conditionsIn) {
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
    public static class Serializer extends GlobalLootModifierSerializer<ShadowSteelVoidingModifier> {

        @Override
        public ShadowSteelVoidingModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new ShadowSteelVoidingModifier(conditionsIn);
        }

        @Override
        public JsonObject write(ShadowSteelVoidingModifier instance) {
            return null;
        }
    }
}