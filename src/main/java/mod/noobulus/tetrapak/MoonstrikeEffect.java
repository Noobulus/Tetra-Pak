package mod.noobulus.tetrapak;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.LazyValue;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.*;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class MoonstrikeEffect {
    private static final ItemEffect moonstrike = ItemEffect.get("tetrapak:moonstrike");
    private static final LazyValue<Method> arrowStackGetter = new LazyValue<>(() -> ObfuscationReflectionHelper.findMethod(AbstractArrowEntity.class, "func_184550_j"));
    private static DamageSource lastActiveDamageSource = null;

    @OnlyIn(Dist.CLIENT)
    public static void clientInit() {
        final IStatGetter moonstrikeGetter = new StatGetterEffectEfficiency(moonstrike, 1);
        final GuiStatBar moonstrikeBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.moonstrike",
                0D, 100D, false, moonstrikeGetter, LabelGetterBasic.percentageLabelDecimal,
                (player, itemStack) -> I18n.format("tetrapak.stats.moonstrike.tooltip",
                moonstrikeGetter.getValue(player, itemStack), moonstrikeGetter.getValue(player, itemStack)));

        WorkbenchStatsGui.addBar(moonstrikeBar);
        HoloStatsGui.addBar(moonstrikeBar);
    }

    @Nullable
    private static ItemStack getThrownItemStack(@Nullable Entity e) { // i should make this a helper method but for now doing it twice is fine
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
    public static void bufferDamageSourceEvent(LootingLevelEvent event) { // haha event hacks go brrr once more
        lastActiveDamageSource = event.getDamageSource();
    }

    @SubscribeEvent
    public static void moonstrikeToolsBreakBlocksFaster(PlayerEvent.BreakSpeed event) {
        ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
        if (!(heldItemMainhand.getItem() instanceof ModularItem))
            return;
        ModularItem item = (ModularItem) heldItemMainhand.getItem();
        int level = item.getEffectLevel(heldItemMainhand, moonstrike);
        if (level > 0) {
            IWorld moonPhaseWorld = Objects.requireNonNull(event.getPlayer().getEntityWorld());
            float efficiency = (float)item.getEffectEfficiency(heldItemMainhand, moonstrike);
            event.setNewSpeed(event.getOriginalSpeed() * getMoonFactor(moonPhaseWorld, efficiency));
        }
    }

    @SubscribeEvent
    public static void moonstrikeCausesBonusDamage(LivingHurtEvent event) {
    /*  DamageSource source = event.getSource();
        PlayerEntity player = (PlayerEntity)source.getTrueSource();
        ItemStack heldItemMainhand = player.getHeldItemMainhand();
        ModularItem heldItem = (ModularItem) heldItemMainhand.getItem(); */
        if (shouldMoonstrikeAffect(lastActiveDamageSource)) {
            IWorld moonPhaseWorld = Objects.requireNonNull(event.getSource().getImmediateSource()).getEntityWorld();
            float efficiency = getMoonstrikeEfficiency(lastActiveDamageSource);
            event.setAmount(event.getAmount() * getMoonFactor(moonPhaseWorld, efficiency));
        }
    }

    private static float getMoonFactor(IWorld world, float efficiency) {
        int moonPhase = world.getMoonPhase();
        return (1 + ((efficiency/10) * (float)Math.abs(moonPhase - 4) / 40));
    }

    private static boolean shouldMoonstrikeAffect(@Nullable DamageSource source) {
        if (source == null)
            return false;
        if (source.getTrueSource() instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) source.getTrueSource();
            ItemStack heldItem = user.getHeldItemMainhand();

            if (heldItem.getItem() instanceof ModularItem) {
                ModularItem heldModularitem = (ModularItem) heldItem.getItem();
                int level = heldModularitem.getEffectLevel(heldItem, moonstrike);
                return level > 0;
            }
            ItemStack thrownItem = getThrownItemStack(source.getImmediateSource());
            if (thrownItem != null && thrownItem.getItem() instanceof ModularItem) {
                ModularItem thrownModularItem = (ModularItem) thrownItem.getItem();
                int level = thrownModularItem.getEffectLevel(thrownItem, moonstrike);
                return level > 0;
            }
        }
        return false;
    }

    private static float getMoonstrikeEfficiency(@Nullable DamageSource source) {
        if (source == null)
            return 0;
        if (source.getTrueSource() instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) source.getTrueSource();
            ItemStack heldItem = user.getHeldItemMainhand();

            if (heldItem.getItem() instanceof ModularItem) {
                ModularItem heldModularitem = (ModularItem) heldItem.getItem();
                return (float)heldModularitem.getEffectEfficiency(heldItem, moonstrike);
            }
            ItemStack thrownItem = getThrownItemStack(source.getImmediateSource());
            if (thrownItem != null && thrownItem.getItem() instanceof ModularItem) {
                ModularItem thrownModularItem = (ModularItem) thrownItem.getItem();
                return (float)thrownModularItem.getEffectEfficiency(thrownItem, moonstrike);
            }
        }
        return 0;
    }
}
