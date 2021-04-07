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
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FieryGlassScorchEntityDropsModifier extends LootModifier {
    private static final ItemEffect scorching = ItemEffect.get("tetrapak:scorching");

    public FieryGlassScorchEntityDropsModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (context.get(LootParameters.THIS_ENTITY) instanceof PlayerEntity) //don't smelt player drops
            return generatedLoot;
        Entity killerEntity = context.get(LootParameters.KILLER_ENTITY);
        if (killerEntity == null) // because require non-null throws NPEs
            return generatedLoot;
        ItemStack heldItem = killerEntity.getHeldEquipment().iterator().next();
        if (!(heldItem.getItem() instanceof ModularItem))
                return generatedLoot;
        ModularItem heldModularitem = (ModularItem) heldItem.getItem();
        int level = heldModularitem.getEffectLevel(heldItem, scorching);
        if (level > 0) {
            ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
            generatedLoot.forEach((stack) -> ret.add(smelt(stack, context)));
            return ret;
        }
        return generatedLoot;
    }

    private static ItemStack smelt(ItemStack stack, LootContext context) { // this is just forge example code switched over to my mappings but we won't talk about that
        return context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld())
                .map(FurnaceRecipe::getRecipeOutput)
                .filter(itemStack -> !itemStack.isEmpty())
                .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
                .orElse(stack);
    }

    // JSON serializer stuff for loot modifiers
    public static class Serializer extends GlobalLootModifierSerializer<FieryGlassScorchEntityDropsModifier> {

        @Override
        public FieryGlassScorchEntityDropsModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new FieryGlassScorchEntityDropsModifier(conditionsIn);
        }

        @Override
        public JsonObject write(FieryGlassScorchEntityDropsModifier instance) {
            return null;
        }
    }
}