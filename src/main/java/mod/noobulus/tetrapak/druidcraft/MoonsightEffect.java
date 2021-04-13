package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.IHoloDescription;
import mod.noobulus.tetrapak.util.ITetraEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

public class MoonsightEffect implements IHoloDescription {

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("moonsight");
	}

	@SubscribeEvent
	public void grantMoonlightVision(LivingEvent.LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.world;

		if (world.isRemote || !world.isNight() || world.getGameTime() % 80L != 0L || !world.isSkyVisible(entity.getBlockPos()) || world.getMoonSize() == 0 || !hasBeltEffect(entity))
			return;

		entity.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 300, 1, true, false));
	}
}
