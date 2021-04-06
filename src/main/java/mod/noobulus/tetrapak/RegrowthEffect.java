package mod.noobulus.tetrapak;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import static java.lang.Math.round;

public class RegrowthEffect {
    private static final ItemEffect regrowth = ItemEffect.get("tetrapak:regrowth");

    @OnlyIn(Dist.CLIENT)
    public static void clientInit() {
        final IStatGetter regrowthGetter = new StatGetterEffectEfficiency(regrowth, 0.05);
        final IStatGetter regrowthTotalGetter = new StatGetterEffectEfficiency(regrowth, 0.08333);
        final GuiStatBar regrowthBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.regrowth",
                0, 60, false, regrowthGetter, LabelGetterBasic.integerLabel,
                (player, itemStack) -> I18n.format("tetrapak.stats.regrowth.tooltip",
                regrowthGetter.getValue(player, itemStack), round((float)regrowthTotalGetter.getValue(player, itemStack))));

        WorkbenchStatsGui.addBar(regrowthBar);
        HoloStatsGui.addBar(regrowthBar);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        World world = entity.getEntityWorld();
        if (!(entity instanceof PlayerEntity))
            return;

        for (EquipmentSlotType slot : new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND}) {
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (stack.getItem() instanceof ModularItem) {
                ModularItem item = (ModularItem) stack.getItem();
                int level = item.getEffectLevel(stack, regrowth);
                if (!stack.isEmpty() && stack.isDamaged() && level > 0 && world.getGameTime() % item.getEffectEfficiency(stack, regrowth) == 0) {
                    stack.setDamage(stack.getDamage() - (stack.getMaxDamage()/100) - 1);
                }
            }
        }
    }
}
