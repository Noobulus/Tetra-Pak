package mod.noobulus.tetrapak.effects.create;

import com.simibubi.create.foundation.utility.VecHelper;
import mod.noobulus.tetrapak.Mods;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.classloading.GetSudsParticle;
import mod.noobulus.tetrapak.util.tetra_definitions.IPercentageHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;

import java.util.UUID;

public class NullifyingEffect implements IPercentageHoloDescription, IEventBusListener {
	public static final AttributeModifier beltGravityModifier = new AttributeModifier(UUID.fromString("678c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.25 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final AttributeModifier beltDoubleGravityModifier = new AttributeModifier(UUID.fromString("778c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.125 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final AttributeModifier beltGravityModifierSlowfall = new AttributeModifier(UUID.fromString("878c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.35 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	private static SimpleParticleType sudsParticle = Mods.SUPPLEMENTARIES.isLoaded ? GetSudsParticle.getSudsParticle() : null;

	private static void updateEffect(boolean active, AttributeInstance attributeInstance, AttributeModifier modifier) {
		if (active) {
			if (!attributeInstance.hasModifier(modifier))
				attributeInstance.addTransientModifier(modifier);
		} else if (attributeInstance.hasModifier(modifier)) {
			attributeInstance.removeModifier(modifier);
		}
	}

	@Override
	public void doBeltTick(Player player, int nullifierLevel) {
		AttributeInstance gravityAttribute = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
		if (nullifierLevel < 0 || gravityAttribute == null)
			return;

		boolean slowfall = player.hasEffect(MobEffects.SLOW_FALLING);
		updateEffect(nullifierLevel == 1 && !slowfall, gravityAttribute, beltGravityModifier);
		updateEffect(nullifierLevel == 2 && !slowfall, gravityAttribute, beltDoubleGravityModifier);
		updateEffect(nullifierLevel > 0 && slowfall, gravityAttribute, beltGravityModifierSlowfall);
		if (nullifierLevel > 0 && player.getDeltaMovement().y() < 0) // extra check for fall speed to make crits work correctly
			player.fallDistance = 1;
		if (nullifierLevel > 0 && player.getDeltaMovement().y() >= 0)
			player.fallDistance = 0;

		// spawn end rod particles at player's feet to show that refined radiance-y effects are happening
		if (player.level.isClientSide) {
			Vec3 pos = player.position();
			Vec3 basemotion;
			if (player.level.random.nextFloat() < nullifierLevel / 2f) {
				basemotion = VecHelper.offsetRandomly(pos, player.level.random, 0.5F);
				if (Mods.SUPPLEMENTARIES.isLoaded && hasBubbler(player)) { // funny bubble effect
					player.level.addParticle(sudsParticle, basemotion.x, pos.y, basemotion.z, 0.0D, -0.10000000149011612D, 0.0D);
				} else {
					player.level.addParticle(ParticleTypes.END_ROD, basemotion.x, pos.y, basemotion.z, 0.0D, -0.10000000149011612D, 0.0D);
				}
			}
		}
	}

	private static boolean hasBubbler(Player player) {
		ItemStack itemStack = ToolbeltHelper.findToolbelt(player);
		return getBubblerLevel(itemStack) > 0;
	}

	private static int getBubblerLevel(ItemStack itemStack) {
		if (!itemStack.isEmpty() && itemStack.getItem() instanceof IModularItem) {
			IModularItem item = (IModularItem)itemStack.getItem();
			int lvl = item.getEffectLevel(itemStack, ItemEffect.get("tetrapak:bubbling"));
			return lvl;
		} else {
			return 0;
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
