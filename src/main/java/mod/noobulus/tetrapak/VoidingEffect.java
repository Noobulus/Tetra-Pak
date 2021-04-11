package mod.noobulus.tetrapak;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.*;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import javax.annotation.Nullable;


public class VoidingEffect {
	private static final ItemEffect voiding = ItemEffect.get("tetrapak:voiding");
	private static DamageSource lastActiveDamageSource = null;

	@OnlyIn(Dist.CLIENT)
	public static void clientInit() {
		final IStatGetter voidingGetter = new StatGetterEffectLevel(voiding, 1, 0);
		final IStatGetter voidingEffGetter = new StatGetterEffectEfficiency(voiding, 1);
		final IStatGetter voidingLootingGetter = new StatGetterEnchantmentLevel(Enchantments.LOOTING, 1.0D);
		final IStatGetter voidingFortuneGetter = new StatGetterEnchantmentLevel(Enchantments.FORTUNE, 1.0D);
		final GuiStatBar voidingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.voiding",
			0, 1, false, voidingGetter, LabelGetterBasic.integerLabel,
			(player, itemStack) -> I18n.format("tetrapak.stats.voiding.tooltip",
				1 + (voidingEffGetter.getValue(player, itemStack) * (voidingLootingGetter.getValue(player, itemStack) + 2))
				, 1 + (voidingEffGetter.getValue(player, itemStack) * (voidingFortuneGetter.getValue(player, itemStack) + 2))));

		WorkbenchStatsGui.addBar(voidingBar);
		HoloStatsGui.addBar(voidingBar);
	}

	@SubscribeEvent
	public static void voidingKillsRemoveDrops(LivingDropsEvent event) {
		if (shouldVoidingAffect(event.getSource(), event.getEntity())) {
			event.getDrops().clear();
		}
	}

	@SubscribeEvent
	public static void bufferDamageSourceEvent(LootingLevelEvent event) { // haha event hacks go brrr
		lastActiveDamageSource = event.getDamageSource();
	}

	@SubscribeEvent
	public static void voidingKillsMultiplyExp(LivingExperienceDropEvent event) {
		LivingEntity target = event.getEntityLiving();
		if (shouldVoidingAffect(lastActiveDamageSource, target)) {
			int levelLooting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, event.getAttackingPlayer().getHeldItemMainhand());
			float modifier = 1 + (getVoidingEfficiency(lastActiveDamageSource) * (levelLooting + 2));
			event.setDroppedExperience((int) (event.getDroppedExperience() * modifier));
		}
	}

	@SubscribeEvent
	public static void voidingHardBlocksGivesExp(BlockEvent.BreakEvent event) {
		ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
		if (ItemHelper.getEffectLevel(heldItemMainhand, voiding) > 0) {
			int levelFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItemMainhand);
			float modifier = 1 + (getVoidingEfficiency(lastActiveDamageSource) * (levelFortune + 2));
			float hardness = event.getState().getBlockHardness(event.getWorld(), event.getPos());
			float hardnessExp = 0;
			if (hardness > 3.1) // free exp for mining stone is a little bit much
				hardnessExp = (0.1f * (hardness * (1 + (getVoidingEfficiency(lastActiveDamageSource) * levelFortune)))); // give exp based on broken block hardness, needs tweaking
			event.setExpToDrop((int) ((event.getExpToDrop() * modifier) + hardnessExp));
		}
	}

	private static boolean shouldVoidingAffect(@Nullable DamageSource source, Entity target) {
		if (source == null)
			return false;
		if (source.getTrueSource() instanceof LivingEntity && !(target instanceof PlayerEntity)) {
			LivingEntity user = (LivingEntity) source.getTrueSource();
			return ItemHelper.getEffectLevel(user.getHeldItemMainhand(), voiding) > 0
				|| ItemHelper.getEffectLevel(ItemHelper.getThrownItemStack(source.getImmediateSource()), voiding) > 0;
		}
		return false;
	}

	private static float getVoidingEfficiency(@Nullable DamageSource source) {
		if (source == null)
			return 0;
		if (source.getTrueSource() instanceof LivingEntity) {
			LivingEntity user = (LivingEntity) source.getTrueSource();
			ItemStack heldItem = user.getHeldItemMainhand();

			if (heldItem.getItem() instanceof ModularItem) {
				ModularItem heldModularitem = (ModularItem) heldItem.getItem();
				return (float) heldModularitem.getEffectEfficiency(heldItem, voiding);
			}
			ItemStack thrownItem = ItemHelper.getThrownItemStack(source.getImmediateSource());
			if (thrownItem != null && thrownItem.getItem() instanceof ModularItem) {
				ModularItem thrownModularItem = (ModularItem) thrownItem.getItem();
				return (float) thrownModularItem.getEffectEfficiency(thrownItem, voiding);
			}
		}
		return 0;
	}
}