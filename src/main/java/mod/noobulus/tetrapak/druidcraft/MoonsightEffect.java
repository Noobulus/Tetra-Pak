package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.IHoloDescription;
import mod.noobulus.tetrapak.util.ITetraEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import se.mickelus.tetra.effect.ItemEffect;

public class MoonsightEffect implements IHoloDescription {

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("moonsight");
	}

	@Override
	public void doBeltTick(PlayerEntity player, int effectLevel) {
		World world = player.world;

		if (effectLevel <= 0 || world.isRemote || !world.isNight() || world.getGameTime() % 80L != 0L || !world.isSkyVisible(player.getBlockPos()) || world.getMoonSize() == 0)
			return;

		player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 300, 0, true, false));
	}
}
