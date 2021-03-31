package mod.noobulus.tetrapak;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.entity.*;

import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;


public class VoidingEffect {
    private static final ItemEffect voiding = ItemEffect.get("tetrapak:voiding");

    @SubscribeEvent
    public static void onDeath(LivingDropsEvent event) {
        if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof LivingEntity && !(event.getEntity() instanceof PlayerEntity)) {
            LivingEntity user = (LivingEntity) event.getSource().getTrueSource();
            ItemStack heldItem = user.getHeldItemMainhand();
            if (!(heldItem.getItem() instanceof ModularItem))
                return;
            ModularItem heldModularitem = (ModularItem) heldItem.getItem();
            int level = heldModularitem.getEffectLevel(heldItem, voiding);
            if (level > 0) {
                event.getDrops().clear();
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingExperienceDropEvent event) {
        LivingEntity user = event.getAttackingPlayer();
        if ((event.getEntity() instanceof PlayerEntity) || user == null)
            return;
        ItemStack heldItem = user.getHeldItemMainhand();
        if (!(heldItem.getItem() instanceof ModularItem))
            return;

        ModularItem heldModularitem = (ModularItem) heldItem.getItem();
        int level = heldModularitem.getEffectLevel(heldItem, voiding);
        if (level > 0) {
            int levelLooting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, heldItem);
            float modifier = 2 + (0.25f * levelLooting);
            event.setDroppedExperience((int) (event.getDroppedExperience() * modifier));
        }
    }
}