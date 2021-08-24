package mod.noobulus.tetrapak.create.refined_radiance;

import mod.noobulus.tetrapak.networking.EntityFloatParticlePacket;
import mod.noobulus.tetrapak.networking.Packets;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import se.mickelus.tetra.effect.ItemEffect;

public class FloatingEffect implements IHoloDescription, IEventBusListener {
	public static final FloatingEffect INSTANCE = new FloatingEffect();
	private boolean makeItemsFloat = false;

	private static void makeFloaty(Entity e) {
		e.setNoGravity(true);
		e.setDeltaMovement(e.getDeltaMovement().scale(0.3));
		e.getPersistentData().putBoolean("DoFloatyParticles", true);
	}

	public static float getIdleParticleChance(ItemEntity entity) {
		return MathHelper.clamp(entity.getItem()
			.getCount() - 10, 5, 100) / 64f;
	}

	public static void onItemEntityTick(ItemEntity entity) {
		World world = entity.getCommandSenderWorld();
		if (entity.level == null || entity.level.isClientSide)
			return;
		if (!entity.getPersistentData().getBoolean("DoFloatyParticles"))
			return;
		if (world.random.nextFloat() > getIdleParticleChance(entity))
			return;

		Packets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new EntityFloatParticlePacket(entity));
	}

	public void checkFloatiness(ItemStack tool) {
		makeItemsFloat = hasEffect(tool);
	}

	public void checkFloatiness(DamageSource source) {
		makeItemsFloat = hasEffect(source);
	}

	@SubscribeEvent
	public void floatingKillsLevitateDrops(LivingDropsEvent event) {
		if (hasEffect(event.getSource()))
			event.getDrops().forEach(FloatingEffect::makeFloaty);
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!(event.getEntity() instanceof ItemEntity))
			return;

		if (makeItemsFloat || hasEffect(((ItemEntity) event.getEntity()).getItem()))
			makeFloaty(event.getEntity());
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("floating");
	}

	public void resetFloatiness() {
		makeItemsFloat = false;
	}
}
