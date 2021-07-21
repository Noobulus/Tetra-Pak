package mod.noobulus.tetrapak.create;

import mod.noobulus.tetrapak.loot.modifier.VoidingLootModifier;
import mod.noobulus.tetrapak.util.DamageBufferer;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ILootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.*;

import javax.annotation.Nullable;
import java.util.function.Function;

public class VoidingEffect implements IHoloDescription, ILootModifier<VoidingLootModifier> {

	@SubscribeEvent
	public void voidingKillsMultiplyExp(LivingExperienceDropEvent event) {
		LivingEntity target = event.getEntityLiving();
		DamageSource lastActive = DamageBufferer.getLastActiveDamageSource();
		if (shouldVoidingAffect(lastActive, target)) {
			int levelLooting = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, event.getAttackingPlayer().getMainHandItem());
			double modifier = 1 + (getEffectEfficiency(lastActive) * (levelLooting + 2));
			event.setDroppedExperience((int) (event.getDroppedExperience() * modifier));
		}
	}

	@SubscribeEvent
	public void voidingHardBlocksGivesExp(BlockEvent.BreakEvent event) {
		ItemStack heldItemMainhand = event.getPlayer().getMainHandItem();
		if (hasEffect(heldItemMainhand)) {
			int levelFortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, heldItemMainhand);
			double efficiency = getEffectEfficiency(heldItemMainhand);
			double modifier = 1 + efficiency * (levelFortune + 2);
			float hardness = event.getState().getDestroySpeed(event.getWorld(), event.getPos());
			double hardnessExp = 0;
			if (hardness > 3.1) // free exp for mining stone is a little bit much
				hardnessExp = (0.1f * (hardness * (1 + efficiency))); // give exp based on broken block hardness, does not scale with fortune
			event.setExpToDrop((int) ((event.getExpToDrop() * modifier) + hardnessExp));
		}
	}

	@SubscribeEvent
	public void voidingKillsRemoveDrops(LivingDropsEvent event) { // this is dumb, but bosses are built different and don't care about loot modifiers
		if (!shouldVoidingAffect(event.getSource(), event.getEntity())) {
			return;
		}
		event.getDrops().clear();
	}

	private boolean shouldVoidingAffect(@Nullable DamageSource source, Entity target) {
		if (target instanceof PlayerEntity)
			return false;
		return hasEffect(source);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiStatBar getStatBar() {
		final IStatGetter voidingGetter = new StatGetterEffectLevel(getEffect(), 1, 0);
		final IStatGetter voidingEffGetter = new StatGetterEffectEfficiency(getEffect(), 1);
		final IStatGetter voidingLootingGetter = new StatGetterEnchantmentLevel(Enchantments.MOB_LOOTING, 1.0D);
		final IStatGetter voidingFortuneGetter = new StatGetterEnchantmentLevel(Enchantments.BLOCK_FORTUNE, 1.0D);
		return new GuiStatBar(0, 0, 59, getStatsPath(),
			0, 1, false, voidingGetter, LabelGetterBasic.integerLabel,
			(player, itemStack) -> I18n.get(getTooltipPath(),
				1 + (voidingEffGetter.getValue(player, itemStack) * (voidingLootingGetter.getValue(player, itemStack) + 2))
				, 1 + (voidingEffGetter.getValue(player, itemStack) * (voidingFortuneGetter.getValue(player, itemStack) + 2))));
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("voiding");
	}

	@Override
	public Function<ILootCondition[], VoidingLootModifier> getModifierConstructor() {
		return VoidingLootModifier::new;
	}
}