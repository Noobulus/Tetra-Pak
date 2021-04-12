package mod.noobulus.tetrapak.util;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

public class DamageBufferer {
	private static DamageSource lastActiveDamageSource = null;

	private DamageBufferer() {
	}

	@SubscribeEvent
	public static void bufferDamageSourceEvent(LootingLevelEvent event) { // haha event hacks go brrr once more
		lastActiveDamageSource = event.getDamageSource();
	}

	@Nullable
	public static DamageSource getLastActiveDamageSource() {
		return lastActiveDamageSource;
	}
}
