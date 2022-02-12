package mod.noobulus.tetrapak.effects.base;

import mod.noobulus.tetrapak.util.DamageBufferer;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IPercentageHoloDescription;
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

public class ExpBoostEffect implements IPercentageHoloDescription, IEventBusListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST) // always go off first
    public void boostExpFromKills(LivingExperienceDropEvent event) {
        LivingEntity target = event.getEntityLiving();
        DamageSource lastActive = DamageBufferer.getLastActiveDamageSource();
        if (shouldExpBoostAffect(lastActive, target)) {
            event.setDroppedExperience(doubleToIntWithChance(event.getDroppedExperience() * (1 + (getEffectEfficiency(lastActive)) / 100)));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) // works!
    public void boostExpFromBlocks(BlockEvent.BreakEvent event) {
        ItemStack heldItemMainhand = event.getPlayer().getMainHandItem();
        if (hasEffect(heldItemMainhand)) {
            event.setExpToDrop(doubleToIntWithChance(event.getExpToDrop() * (1 + (getEffectEfficiency(heldItemMainhand)) / 100)));
        }
    }

    private boolean shouldExpBoostAffect(@Nullable DamageSource source, Entity target) {
        if (target instanceof Player)
            return false;
        return hasEffect(source);
    }

    private int doubleToIntWithChance(Double num) {
        int res = num.intValue();
        return res + (num - res <= Math.random() ? 1 : 0); // seems like a good way to not truncate decimal exp
    }

    @Override
    public ItemEffect getEffect() {
        return ITetraEffect.get("expboost");
    }
}
