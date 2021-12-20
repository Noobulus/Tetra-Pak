package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
