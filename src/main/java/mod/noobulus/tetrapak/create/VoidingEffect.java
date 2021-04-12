package mod.noobulus.tetrapak.create;

import mod.noobulus.tetrapak.util.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.*;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import javax.annotation.Nullable;

public class VoidingEffect implements IClientInit, ILootModifier<VoidingLootModifier> {
	public static final ItemEffect VOIDING_EFFECT = EffectHelper.get("voiding");

	@SubscribeEvent
	public void voidingKillsRemoveDrops(LivingDropsEvent event) {
		if (shouldVoidingAffect(event.getSource(), event.getEntity())) {
			event.getDrops().clear();
		}
	}

	@SubscribeEvent
	public void voidingKillsMultiplyExp(LivingExperienceDropEvent event) {
		LivingEntity target = event.getEntityLiving();
		if (shouldVoidingAffect(DamageBufferer.getLastActiveDamageSource(), target)) {
			int levelLooting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, event.getAttackingPlayer().getHeldItemMainhand());
			float modifier = 1 + (EffectHelper.getEffectEfficiency(DamageBufferer.getLastActiveDamageSource(), VOIDING_EFFECT) * (levelLooting + 2));
			event.setDroppedExperience((int) (event.getDroppedExperience() * modifier));
		}
	}

	@SubscribeEvent
	public void voidingHardBlocksGivesExp(BlockEvent.BreakEvent event) {
		ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
		if (ItemHelper.getEffectLevel(heldItemMainhand, VOIDING_EFFECT) > 0) {
			int levelFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItemMainhand);
			float efficiency = EffectHelper.getEffectEfficiency(DamageBufferer.getLastActiveDamageSource(), VOIDING_EFFECT);
			float modifier = 1 + efficiency * (levelFortune + 2);
			float hardness = event.getState().getBlockHardness(event.getWorld(), event.getPos());
			float hardnessExp = 0;
			if (hardness > 3.1) // free exp for mining stone is a little bit much
				hardnessExp = (0.1f * (hardness * (1 + efficiency * levelFortune))); // give exp based on broken block hardness, needs tweaking
			event.setExpToDrop((int) ((event.getExpToDrop() * modifier) + hardnessExp));
		}
	}

	private boolean shouldVoidingAffect(@Nullable DamageSource source, Entity target) {
		if (source == null)
			return false;
		if (source.getTrueSource() instanceof LivingEntity && !(target instanceof PlayerEntity)) {
			LivingEntity user = (LivingEntity) source.getTrueSource();
			return ItemHelper.getEffectLevel(user.getHeldItemMainhand(), VOIDING_EFFECT) > 0
				|| ItemHelper.getEffectLevel(ItemHelper.getThrownItemStack(source.getImmediateSource()), VOIDING_EFFECT) > 0;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientInit() {
		final IStatGetter voidingGetter = new StatGetterEffectLevel(VOIDING_EFFECT, 1, 0);
		final IStatGetter voidingEffGetter = new StatGetterEffectEfficiency(VOIDING_EFFECT, 1);
		final IStatGetter voidingLootingGetter = new StatGetterEnchantmentLevel(Enchantments.LOOTING, 1.0D);
		final IStatGetter voidingFortuneGetter = new StatGetterEnchantmentLevel(Enchantments.FORTUNE, 1.0D);
		final GuiStatBar voidingBar = new GuiStatBar(0, 0, 59, EffectHelper.getStatsPath(VOIDING_EFFECT),
			0, 1, false, voidingGetter, LabelGetterBasic.integerLabel,
			(player, itemStack) -> I18n.format(EffectHelper.getTooltipPath(VOIDING_EFFECT),
				1 + (voidingEffGetter.getValue(player, itemStack) * (voidingLootingGetter.getValue(player, itemStack) + 2))
				, 1 + (voidingEffGetter.getValue(player, itemStack) * (voidingFortuneGetter.getValue(player, itemStack) + 2))));

		WorkbenchStatsGui.addBar(voidingBar);
		HoloStatsGui.addBar(voidingBar);
	}

	@Override
	public GlobalLootModifierSerializer<VoidingLootModifier> getModifier() {
		return new VoidingLootModifier.Serializer().setRegistryName(new ResourceLocation(VOIDING_EFFECT.getKey()));
	}
}