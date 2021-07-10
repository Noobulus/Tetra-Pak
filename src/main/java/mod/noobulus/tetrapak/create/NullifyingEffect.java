package mod.noobulus.tetrapak.create;

import com.simibubi.create.foundation.utility.VecHelper;
import mod.noobulus.tetrapak.util.tetra_definitions.IPercentageHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.UUID;

public class NullifyingEffect implements IPercentageHoloDescription {
	public static final AttributeModifier beltGravityModifier = new AttributeModifier(UUID.fromString("678c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.25 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final AttributeModifier beltDoubleGravityModifier = new AttributeModifier(UUID.fromString("778c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.125 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final AttributeModifier beltGravityModifierSlowfall = new AttributeModifier(UUID.fromString("878c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.35 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

	private static void updateEffect(boolean active, ModifiableAttributeInstance attributeInstance, AttributeModifier modifier) {
		if (active) {
			if (!attributeInstance.hasModifier(modifier))
				attributeInstance.addTransientModifier(modifier);
		} else if (attributeInstance.hasModifier(modifier)) {
			attributeInstance.removeModifier(modifier);
		}
	}

	@Override
	public void doBeltTick(PlayerEntity player, int nullifierLevel) {
		ModifiableAttributeInstance gravityAttribute = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
		if (nullifierLevel < 0 || gravityAttribute == null)
			return;

		boolean slowfall = player.hasEffect(Effects.SLOW_FALLING);
		updateEffect(nullifierLevel == 1 && !slowfall, gravityAttribute, beltGravityModifier);
		updateEffect(nullifierLevel == 2 && !slowfall, gravityAttribute, beltDoubleGravityModifier);
		updateEffect(nullifierLevel > 0 && slowfall, gravityAttribute, beltGravityModifierSlowfall);
		if (nullifierLevel > 0 && player.getDeltaMovement().y() < 0) // extra check for fall speed to make crits work correctly
			player.fallDistance = 1;
		if (nullifierLevel > 0 && player.getDeltaMovement().y() >= 0)
			player.fallDistance = 0;

		// spawn end rod particles at player's feet to show that refined radiance-y effects are happening
		if (player.level.isClientSide) {
			Vector3d pos = player.position();
			Vector3d basemotion;
			if (player.level.random.nextFloat() < nullifierLevel / 2f) {
				basemotion = VecHelper.offsetRandomly(pos, player.level.random, 0.5F);
				player.level.addParticle(ParticleTypes.END_ROD, basemotion.x, pos.y, basemotion.z, 0.0D, -0.10000000149011612D, 0.0D);
			}
		}
	}

	@SubscribeEvent
	public void onFarmlandTrampled(BlockEvent.FarmlandTrampleEvent event) {
		if (hasBeltEffect(event.getEntity()))
			event.setCanceled(true);
	}

	@Override
	public double getStatMultiplier() {
		return 12.5;
	}

	@Override
	public double getStatBase() {
		return 62.5;
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("nullifying");
	}
}
