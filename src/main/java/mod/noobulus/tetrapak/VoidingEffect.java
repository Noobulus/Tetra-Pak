package mod.noobulus.tetrapak;

import com.simibubi.create.content.curiosities.tools.DeforesterItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
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
        if ((event.getEntity() instanceof PlayerEntity) || user == null) // accidentally killing somebody and voiding their items is uncool
            return;
        ItemStack heldItem = user.getHeldItemMainhand();
        if (!(heldItem.getItem() instanceof ModularItem))
            return;

        ModularItem heldModularitem = (ModularItem) heldItem.getItem();
        int level = heldModularitem.getEffectLevel(heldItem, voiding);
        if (level > 0) {
            int levelLooting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, heldItem);
            float modifier = 2 + (0.25f * levelLooting); // double exp, then add 25% more of the original exp for each level of looting
            event.setDroppedExperience((int) (event.getDroppedExperience() * modifier));
        }
    }

    @SubscribeEvent
    public static void onBlockDestroyed(BlockEvent.BreakEvent event) {
        ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
        if (!(heldItemMainhand.getItem() instanceof ModularItem))
            return;
        ModularItem heldItem = (ModularItem) heldItemMainhand.getItem();
        int level = heldItem.getEffectLevel(heldItemMainhand, voiding);
        if (level > 0) {
            int levelFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItemMainhand);
            float modifier = 2 + (0.25f * levelFortune);
            float hardness = event.getState().getBlockHardness(event.getWorld(), event.getPos());
            float hardnessExp = 0;
            if (hardness > 3.1) // free exp for mining stone is a little bit much
                hardnessExp = (0.2f * (hardness * (1 + (0.25f * levelFortune)))); // give exp based on broken block hardness, needs tweaking
            event.setExpToDrop((int) ((event.getExpToDrop() * modifier) + hardnessExp));
        }
    }
}