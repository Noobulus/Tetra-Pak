package mod.noobulus.tetrapak.create;

import mod.noobulus.tetrapak.loot.VoidingLootModifier;
import mod.noobulus.tetrapak.util.*;
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
		if (shouldVoidingAffect(DamageBufferer.getLastActiveDamageSource(), target)) {
			int levelLooting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, event.getAttackingPlayer().getHeldItemMainhand());
			float modifier = 1 + (getEffectEfficiency() * (levelLooting + 2));
			event.setDroppedExperience((int) (event.getDroppedExperience() * modifier));
		}
	}

	@SubscribeEvent
	public void voidingHardBlocksGivesExp(BlockEvent.BreakEvent event) {
		ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
		if (hasEffect(heldItemMainhand)) {
			int levelFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItemMainhand);
			float efficiency = getEffectEfficiency(DamageBufferer.getLastActiveDamageSource());
			float modifier = 1 + efficiency * (levelFortune + 2);
			float hardness = event.getState().getBlockHardness(event.getWorld(), event.getPos());
			float hardnessExp = 0;
			if (hardness > 3.1) // free exp for mining stone is a little bit much
				hardnessExp = (0.1f * (hardness * (1 + efficiency * levelFortune))); // give exp based on broken block hardness, needs tweaking
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
		if (source == null)
			return false;
		if (source.getTrueSource() instanceof LivingEntity && !(target instanceof PlayerEntity)) {
			LivingEntity user = (LivingEntity) source.getTrueSource();
			return hasEffect(user.getHeldItemMainhand())
				|| hasEffect(ItemHelper.getThrownItemStack(source.getImmediateSource()));
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiStatBar getStatBar() {
		final IStatGetter voidingGetter = new StatGetterEffectLevel(getEffect(), 1, 0);
		final IStatGetter voidingEffGetter = new StatGetterEffectEfficiency(getEffect(), 1);
		final IStatGetter voidingLootingGetter = new StatGetterEnchantmentLevel(Enchantments.LOOTING, 1.0D);
		final IStatGetter voidingFortuneGetter = new StatGetterEnchantmentLevel(Enchantments.FORTUNE, 1.0D);
		return new GuiStatBar(0, 0, 59, getStatsPath(),
			0, 1, false, voidingGetter, LabelGetterBasic.integerLabel,
			(player, itemStack) -> I18n.format(getTooltipPath(),
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