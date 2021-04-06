package mod.noobulus.tetrapak;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.LazyValue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.entity.*;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.*;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;


public class VoidingEffect {
    private static final ItemEffect voiding = ItemEffect.get("tetrapak:voiding");
    private static final LazyValue<Method> arrowStackGetter = new LazyValue<>(() -> ObfuscationReflectionHelper.findMethod(AbstractArrowEntity.class, "func_184550_j"));
    private static DamageSource lastActiveDamageSource = null;

    @OnlyIn(Dist.CLIENT)
    public static void clientInit() {
        final IStatGetter voidingGetter = new StatGetterEffectLevel(voiding, 1, 0);
        final GuiStatBar voidingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.voiding",
                0, 1, false, voidingGetter, LabelGetterBasic.integerLabel,
                new TooltipGetterInteger("tetrapak.stats.voiding.tooltip", voidingGetter));

        WorkbenchStatsGui.addBar(voidingBar);
        HoloStatsGui.addBar(voidingBar);
    }

    @Nullable
    private static ItemStack getThrownItemStack(@Nullable Entity e) { // some grimm magic to make javelins work
        if (!(e instanceof AbstractArrowEntity))
            return null;
        Method lookup = arrowStackGetter.getValue();
        lookup.setAccessible(true);
        Object result;
        try {
            result = lookup.invoke(e);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            return null;
        }
        if (!(result instanceof ItemStack))
            return null;
        return (ItemStack) result;
    }

    @SubscribeEvent
    public static void voidingKillsRemoveDrops(LivingDropsEvent event) {
        if (shouldVoidingAffect(event.getSource(), event.getEntity())) {
            event.getDrops().clear();
        }
    }

    @SubscribeEvent
    public static void bufferDamageSourceEvent(LootingLevelEvent event) { // haha event hacks go brrr
        lastActiveDamageSource = event.getDamageSource();
    }

    @SubscribeEvent
    public static void voidingKillsMultiplyExp(LivingExperienceDropEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (shouldVoidingAffect(lastActiveDamageSource, target)) {
            int levelLooting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, event.getAttackingPlayer().getHeldItemMainhand());
            float modifier = 2 + (getVoidingLevel(lastActiveDamageSource, target) * levelLooting); // double exp, then add 25% (configurable!) more of the original exp for each level of looting
            event.setDroppedExperience((int) (event.getDroppedExperience() * modifier));
        }
    }

    @SubscribeEvent
    public static void voidingHardBlocksGivesExp(BlockEvent.BreakEvent event) {
        ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
        if (!(heldItemMainhand.getItem() instanceof ModularItem))
            return;
        ModularItem heldItem = (ModularItem) heldItemMainhand.getItem();
        int level = heldItem.getEffectLevel(heldItemMainhand, voiding);
        if (level > 0) {
            int levelFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItemMainhand);
            float modifier = 2 + ((float)heldItem.getEffectEfficiency(heldItemMainhand, voiding) * levelFortune);
            float hardness = event.getState().getBlockHardness(event.getWorld(), event.getPos());
            float hardnessExp = 0;
            if (hardness > 3.1) // free exp for mining stone is a little bit much
                hardnessExp = (0.2f * (hardness * (1 + (0.25f * levelFortune)))); // give exp based on broken block hardness, needs tweaking
            event.setExpToDrop((int) ((event.getExpToDrop() * modifier) + hardnessExp));
        }
    }

    private static boolean shouldVoidingAffect(@Nullable DamageSource source, Entity target) {
        if (source == null)
            return false;
        if (source.getTrueSource() instanceof LivingEntity && !(target instanceof PlayerEntity)) {
            LivingEntity user = (LivingEntity) source.getTrueSource();
            ItemStack heldItem = user.getHeldItemMainhand();

            if (heldItem.getItem() instanceof ModularItem) {
                ModularItem heldModularitem = (ModularItem) heldItem.getItem();
                int level = heldModularitem.getEffectLevel(heldItem, voiding);
                return level > 0;
            }
            ItemStack thrownItem = getThrownItemStack(source.getImmediateSource());
            if (thrownItem != null && thrownItem.getItem() instanceof ModularItem) {
                ModularItem thrownModularItem = (ModularItem) thrownItem.getItem();
                int level = thrownModularItem.getEffectLevel(thrownItem, voiding);
                return level > 0;
            }
        }
        return false;
    }

    private static float getVoidingLevel(@Nullable DamageSource source, Entity target) {
        if (source == null)
            return 0;
        if (source.getTrueSource() instanceof LivingEntity && !(target instanceof PlayerEntity)) {
            LivingEntity user = (LivingEntity) source.getTrueSource();
            ItemStack heldItem = user.getHeldItemMainhand();

            if (heldItem.getItem() instanceof ModularItem) {
                ModularItem heldModularitem = (ModularItem) heldItem.getItem();
                return (float)heldModularitem.getEffectEfficiency(heldItem, voiding);
            }
            ItemStack thrownItem = getThrownItemStack(source.getImmediateSource());
            if (thrownItem != null && thrownItem.getItem() instanceof ModularItem) {
                ModularItem thrownModularItem = (ModularItem) thrownItem.getItem();
                return (float)thrownModularItem.getEffectEfficiency(thrownItem, voiding);
            }
        }
        return 0;
    }

    private static float getVoidingEfficiency(@Nullable DamageSource source) {
        if (source == null)
            return 0;
        if (source.getTrueSource() instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) source.getTrueSource();
            ItemStack heldItem = user.getHeldItemMainhand();

            if (heldItem.getItem() instanceof ModularItem) {
                ModularItem heldModularitem = (ModularItem) heldItem.getItem();
                return (float)heldModularitem.getEffectEfficiency(heldItem, voiding);
            }
            ItemStack thrownItem = getThrownItemStack(source.getImmediateSource());
            if (thrownItem != null && thrownItem.getItem() instanceof ModularItem) {
                ModularItem thrownModularItem = (ModularItem) thrownItem.getItem();
                return (float)thrownModularItem.getEffectEfficiency(thrownItem, voiding);
            }
        }
        return 0;
    }
}