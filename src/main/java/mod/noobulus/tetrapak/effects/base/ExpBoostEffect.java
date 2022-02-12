package mod.noobulus.tetrapak.effects.base;

import mod.noobulus.tetrapak.util.DamageBufferer;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

import javax.annotation.Nullable;

public class ExpBoostEffect implements IHoloDescription, IEventBusListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST) // always go off first
    public void boostExpFromKills(LivingExperienceDropEvent event) {
        LivingEntity target = event.getEntityLiving();
        DamageSource lastActive = DamageBufferer.getLastActiveDamageSource();
        if (shouldExpBoostAffect(lastActive, target)) {
            event.setDroppedExperience((int) (event.getDroppedExperience() * (1 + getEffectEfficiency(lastActive)))); // TODO: fix this int cast from making small amounts of exp boost do fuckall
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) // works!
    public void boostExpFromBlocks(BlockEvent.BreakEvent event) {
        ItemStack heldItemMainhand = event.getPlayer().getMainHandItem();
        if (hasEffect(heldItemMainhand)) {
            event.setExpToDrop((int) (event.getExpToDrop() * (1 + getEffectEfficiency(heldItemMainhand)))); // TODO: fix this int cast from making small amounts of exp boost do fuckall
        }
    }

    private boolean shouldExpBoostAffect(@Nullable DamageSource source, Entity target) {
        if (target instanceof Player)
            return false;
        return hasEffect(source);
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public GuiStatBar getStatBar() {
        final IStatGetter voidingGetter = new StatGetterEffectLevel(getEffect(), 1, 0);
        final IStatGetter voidingEffGetter = new StatGetterEffectEfficiency(getEffect(), 1);
        final IStatGetter voidingLootingGetter = new StatGetterEnchantmentLevel(Enchantments.MOB_LOOTING, 1.0D);
        final IStatGetter voidingFortuneGetter = new StatGetterEnchantmentLevel(Enchantments.BLOCK_FORTUNE, 1.0D);
        return new GuiStatBar(0, 0, 59, getStatsPath(),
                0, 1, false, voidingGetter, LabelGetterBasic.integerLabel,
                (player, itemStack) -> I18n.get(getTooltipPath(),
                        1 + (voidingEffGetter.getValue(player, itemStack) * (voidingLootingGetter.getValue(player, itemStack) + 2))
                        , 1 + (voidingEffGetter.getValue(player, itemStack) * (voidingFortuneGetter.getValue(player, itemStack) + 2))));
    }*/

    @Override
    public ItemEffect getEffect() {
        return ITetraEffect.get("expboost");
    }
}
