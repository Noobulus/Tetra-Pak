package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import se.mickelus.tetra.effect.ItemEffect;

public class MoonsightEffect implements IHoloDescription {

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("moonsight");
	}

	@Override
	public void doBeltTick(Player player, int effectLevel) {
		Level world = player.level;

		if (effectLevel <= 0 || world.isClientSide || !world.isNight() || world.getGameTime() % 80L != 0L || !world.canSeeSky(player.blockPosition()) || world.getMoonBrightness() == 0)
			return;

		player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
	}
}
