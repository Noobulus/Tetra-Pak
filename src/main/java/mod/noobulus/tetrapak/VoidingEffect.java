package mod.noobulus.tetrapak;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.entity.*;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;


public class VoidingEffect {
    private static final ItemEffect voiding = ItemEffect.get("tetrapak:voiding");
    private static final LazyValue<Method> arrowStackGetter = new LazyValue<>(() -> ObfuscationReflectionHelper.findMethod(AbstractArrowEntity.class, "func_184550_j"));

    @Nullable
    private static ItemStack getThrownItemStack(@Nullable Entity e) { // grimmauld magic
        if (!(e instanceof AbstractArrowEntity))
            return null;
        Method lookup = ObfuscationReflectionHelper.findMethod(AbstractArrowEntity.class, "func_184550_j");
        lookup.setAccessible(true);
        Object result;
        try {
            result = lookup.invoke(e);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            return null;
        }
        if (!(result instanceof ItemStack))
            return null;
        return ((ItemStack) result);
    }

    @SubscribeEvent
    public static void onDeath(LivingDropsEvent event) {
        if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof LivingEntity && !(event.getEntity() instanceof PlayerEntity)) {
            LivingEntity user = (LivingEntity) event.getSource().getTrueSource();
            ItemStack heldItem = user.getHeldItemMainhand();
            ItemStack thrownItem = getThrownItemStack(event.getSource().getImmediateSource());
            if (heldItem.getItem() instanceof ModularItem) {
                ModularItem heldModularitem = (ModularItem) heldItem.getItem();
                int level = heldModularitem.getEffectLevel(heldItem, voiding);
                if (level > 0) {
                    event.getDrops().clear();
                }
                return;
            }
            if (thrownItem.getItem() instanceof ModularItem) {
                ModularItem thrownModularitem = (ModularItem) thrownItem.getItem();
                int level = thrownModularitem.getEffectLevel(thrownItem, voiding);
                if (level > 0) {
                    event.getDrops().clear();
                }
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