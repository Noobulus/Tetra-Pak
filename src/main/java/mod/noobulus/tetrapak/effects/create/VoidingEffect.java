package mod.noobulus.tetrapak.effects.create;

import mod.noobulus.tetrapak.registries.Items;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

import javax.annotation.Nullable;

public class VoidingEffect implements IHoloDescription, IEventBusListener {

	@SubscribeEvent(priority = EventPriority.LOWEST) // this should always go last
	public void voidingKillsRemoveDrops(LivingDropsEvent event) { // this is dumb, but bosses are built different and don't care about loot modifiers
		Entity entity = event.getEntity();
		if (!shouldVoidingAffect(event.getSource(), entity)) {
			return;
		}
		//event.getDrops().clear(); // good contingency but breaks solidifying
		if (entity instanceof WitherBoss) { // funny music disc easter egg
			ItemEntity drop = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.PLAYING_WITH_POWER_DISC.get(), 1));
			drop.setDefaultPickUpDelay(); // i can tell this is janky because it doesn't work without this line
			event.getDrops().add(drop);
		}
	}

	private boolean shouldVoidingAffect(@Nullable DamageSource source, Entity target) {
		if (target instanceof Player)
			return false;
		return hasEffect(source);
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("voiding");
	}
}