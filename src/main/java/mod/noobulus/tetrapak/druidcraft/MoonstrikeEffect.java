package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.registries.Particles;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IPercentageHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelTimeAccess;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;
import se.mickelus.tetra.items.modular.ModularItem;

public class MoonstrikeEffect implements IPercentageHoloDescription, IEventBusListener {

	private static float getMoonFactor(LevelTimeAccess world, double efficiency) {
		return (float) (1 + (efficiency * world.getMoonBrightness() / 100.d));
	}

	private static void spawnMoonParticles(Level world, Vec3 pos) {
		if (world instanceof ServerLevel && world.getMoonBrightness() != 0) {
			((ServerLevel) world).sendParticles(getParticleType(world), pos.x() + 0.5D, pos.y() + 0.5D, pos.z() + 0.5D, 12, (world.random.nextDouble() * 2.0D - 1.0D) * 0.3D, 0.3D + world.random.nextDouble() * 0.3D, (world.random.nextDouble() * 2.0D - 1.0D) * 0.3D, 0.3D);
		}
	}

	private static SimpleParticleType getParticleType(LevelTimeAccess world) {
		float moonSize = world.getMoonBrightness();
		return (moonSize > .5 ?
			(moonSize > .75 ? Particles.MOONSTRIKE_STAGE_3 : Particles.MOONSTRIKE_STAGE_2) :
			(moonSize > .25 ? Particles.MOONSTRIKE_STAGE_1 : Particles.MOONSTRIKE_STAGE_0)).get();
	}

	@SubscribeEvent
	public void moonstrikeToolsBreakBlocksFaster(PlayerEvent.BreakSpeed event) {
		ItemStack heldItemMainhand = event.getPlayer().getMainHandItem();
		if (!(heldItemMainhand.getItem() instanceof ModularItem))
			return;
		ModularItem item = (ModularItem) heldItemMainhand.getItem();
		if (hasEffect(heldItemMainhand)) {
			Level moonPhaseWorld = event.getPlayer().getCommandSenderWorld();
			spawnMoonParticles(moonPhaseWorld, Vec3.atLowerCornerOf(event.getPos()));
			float efficiency = (float) item.getEffectEfficiency(heldItemMainhand, getEffect());
			event.setNewSpeed(event.getOriginalSpeed() * getMoonFactor(moonPhaseWorld, efficiency));
		}
	}

	@SubscribeEvent
	public void moonstrikeCausesBonusDamage(LivingHurtEvent event) {
		if (hasEffect(event.getSource())) {
			Entity source = event.getSource().getDirectEntity();
			if (source == null)
				return;
			Level moonPhaseWorld = source.getCommandSenderWorld();
			double efficiency = getEffectEfficiency(event.getSource());
			if (moonPhaseWorld.getMoonBrightness() != 0)
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().particleEngine.createTrackingEmitter(event.getEntity(), getParticleType(moonPhaseWorld)));
			event.setAmount(event.getAmount() * getMoonFactor(moonPhaseWorld, efficiency));
		}
	}

	@Override
	public ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
		return (player, itemStack) -> I18n.get(getTooltipPath(),
			statGetter.getValue(player, itemStack), statGetter.getValue(player, itemStack));
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("moonstrike");
	}
}
