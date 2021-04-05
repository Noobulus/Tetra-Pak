package mod.noobulus.tetrapak;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.*;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import java.util.Objects;

public class MoonstrikeEffect {
    private static final ItemEffect moonstrike = ItemEffect.get("tetrapak:moonstrike");

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
        DamageSource source = event.getSource();
        if (!(source.getTrueSource() instanceof PlayerEntity))
            return;
        PlayerEntity player = (PlayerEntity)source.getTrueSource();
        ItemStack heldItemMainhand = player.getHeldItemMainhand();
        if (!(heldItemMainhand.getItem() instanceof ModularItem))
            return;
        ModularItem heldItem = (ModularItem) heldItemMainhand.getItem();
        int level = heldItem.getEffectLevel(heldItemMainhand, moonstrike);
        if (level > 0) {
            IWorld moonPhaseWorld = Objects.requireNonNull(event.getSource().getImmediateSource()).getEntityWorld();
            float efficiency = (float)heldItem.getEffectEfficiency(heldItemMainhand, moonstrike);
            event.setAmount(event.getAmount() * getMoonFactor(moonPhaseWorld, efficiency));
        }
    }

    private static float getMoonFactor(IWorld world, float efficiency) {
        int moonPhase = world.getMoonPhase();
        return (1 + ((efficiency/10) * (float)Math.abs(moonPhase - 4) / 40));
    }
}
