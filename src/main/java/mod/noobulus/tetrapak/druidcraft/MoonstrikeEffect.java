package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.registries.Particles;
import mod.noobulus.tetrapak.util.ItemHelper;
import mod.noobulus.tetrapak.util.tetra_definitions.IPercentageHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IDayTimeReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.ITooltipGetter;
import se.mickelus.tetra.items.modular.ModularItem;

import javax.annotation.Nullable;

public class MoonstrikeEffect implements IPercentageHoloDescription {

	private static float getMoonFactor(IDayTimeReader world, float efficiency) {
		return 1 + (efficiency * world.getMoonSize() / 100.f);
	}

	private static void spawnMoonParticles(World world, Vector3d pos) {
		if (world instanceof ServerWorld && world.getMoonSize() != 0) {
			((ServerWorld) world).spawnParticle(getParticleType(world), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 12, (world.rand.nextDouble() * 2.0D - 1.0D) * 0.3D, 0.3D + world.rand.nextDouble() * 0.3D, (world.rand.nextDouble() * 2.0D - 1.0D) * 0.3D, 0.3D);
		}
	}

	private static BasicParticleType getParticleType(IDayTimeReader world) {
		float moonSize = world.getMoonSize();
		return (moonSize > .5 ?
			(moonSize > .75 ? Particles.MOONSTRIKE_STAGE_3 : Particles.MOONSTRIKE_STAGE_2) :
			(moonSize > .25 ? Particles.MOONSTRIKE_STAGE_1 : Particles.MOONSTRIKE_STAGE_0)).get();
	}

	@SubscribeEvent
	public void moonstrikeToolsBreakBlocksFaster(PlayerEvent.BreakSpeed event) {
		ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
		if (!(heldItemMainhand.getItem() instanceof ModularItem))
			return;
		ModularItem item = (ModularItem) heldItemMainhand.getItem();
		if (hasEffect(heldItemMainhand)) {
			World moonPhaseWorld = event.getPlayer().getEntityWorld();
			spawnMoonParticles(moonPhaseWorld, Vector3d.of(event.getPos()));
			float efficiency = (float) item.getEffectEfficiency(heldItemMainhand, getEffect());
			event.setNewSpeed(event.getOriginalSpeed() * getMoonFactor(moonPhaseWorld, efficiency));
		}
	}

	@SubscribeEvent
	public void moonstrikeCausesBonusDamage(LivingHurtEvent event) {
		if (shouldMoonstrikeAffect(event.getSource())) {
			Entity source = event.getSource().getImmediateSource();
			if (source == null)
				return;
			World moonPhaseWorld = source.getEntityWorld();
			float efficiency = getEffectEfficiency(event.getSource());
			if (moonPhaseWorld.getMoonSize() != 0)
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().particles.addParticleEmitter(event.getEntity(), getParticleType(moonPhaseWorld)));
			event.setAmount(event.getAmount() * getMoonFactor(moonPhaseWorld, efficiency));
		}
	}

	private boolean shouldMoonstrikeAffect(@Nullable DamageSource source) {
		if (source == null)
			return false;
		if (source.getTrueSource() instanceof LivingEntity) {
			LivingEntity user = (LivingEntity) source.getTrueSource();

			return hasEffect(user.getHeldItemMainhand()) ||
				hasEffect(ItemHelper.getThrownItemStack(source.getImmediateSource()));
		}
		return false;
	}

	@Override
	public ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
		return (player, itemStack) -> I18n.format(getTooltipPath(),
			statGetter.getValue(player, itemStack), statGetter.getValue(player, itemStack));
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("moonstrike");
	}
}
