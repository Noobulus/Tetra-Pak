package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
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
		World world = player.level;

		if (effectLevel <= 0 || world.isClientSide || !world.isNight() || world.getGameTime() % 80L != 0L || !world.canSeeSky(player.blockPosition()) || world.getMoonBrightness() == 0)
			return;

		player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 400, 0, true, false));
	}
}
