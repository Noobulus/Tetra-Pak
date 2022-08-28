package mod.noobulus.tetrapak.effects.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.curiosities.ExperienceNuggetItem;
import mod.noobulus.tetrapak.util.DamageBufferer;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.MathHelper;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

import javax.annotation.Nullable;

public class SolidifyingEffect implements IHoloDescription, IEventBusListener {

	@SubscribeEvent(priority = EventPriority.LOWEST) // pop off after everything else
	public void solidifyKillExp(LivingExperienceDropEvent event) {
		LivingEntity target = event.getEntityLiving();
		DamageSource lastActive = DamageBufferer.getLastActiveDamageSource();
		if (shouldSolidifyingAffect(lastActive, target)) {
            int chunks = MathHelper.doubleToIntWithChance((event.getDroppedExperience()/3d));
			target.level.addFreshEntity(new ItemEntity(target.level, target.position().x, target.position().y, target.position().z,
					new ItemStack(AllItems.EXP_NUGGET.get(), chunks))); // maybe just doing this ungoofs apotheosis?
			event.setDroppedExperience(0); // don't drop any regular experience
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void solidifyBlockExp(BlockEvent.BreakEvent event) {
		ItemStack heldItemMainhand = event.getPlayer().getMainHandItem();
		if (hasEffect(heldItemMainhand)) {
			BlockPos pos = event.getPos();
			Level level = event.getPlayer().getLevel();
			int chunks = MathHelper.doubleToIntWithChance((event.getExpToDrop()/3d));
			level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d,
					new ItemStack(AllItems.EXP_NUGGET.get(), chunks)));
			event.setExpToDrop(0);
		}
	}

    private boolean shouldSolidifyingAffect(@Nullable DamageSource source, Entity target) {
        if (target instanceof Player)
            return false;
        return hasEffect(source);
    }

    @Override
    public ItemEffect getEffect() {
        return ITetraEffect.get("solidifying");
    }

}