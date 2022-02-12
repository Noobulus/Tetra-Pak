package mod.noobulus.tetrapak.effects.create;

import mod.noobulus.tetrapak.Registry;
import mod.noobulus.tetrapak.effects.base.ExpBoostEffect;
import mod.noobulus.tetrapak.loot.modifier.VoidingLootModifier;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ILootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.*;

import javax.annotation.Nullable;
import java.util.function.Function;

public class VoidingEffect implements IHoloDescription, ILootModifier<VoidingLootModifier>, IEventBusListener {

	/*@SubscribeEvent(priority = EventPriority.LOWEST) // pop off after everything else
	public void voidingKillsMultiplyExp(LivingExperienceDropEvent event) {
		LivingEntity target = event.getEntityLiving();
		DamageSource lastActive = DamageBufferer.getLastActiveDamageSource();
		if (shouldVoidingAffect(lastActive, target)) {
			int levelLooting = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, event.getAttackingPlayer().getMainHandItem());
			double modifier = 1 + (getEffectEfficiency(lastActive) * (levelLooting + 2));
			event.setDroppedExperience((int) (event.getDroppedExperience() * modifier));
			//event.getEntity().captureDrops().
		}
	}

	@SubscribeEvent
	public void voidingBlocksMultipliesExp(BlockEvent.BreakEvent event) {
		ItemStack heldItemMainhand = event.getPlayer().getMainHandItem();
		if (hasEffect(heldItemMainhand)) {
			int levelFortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, heldItemMainhand);
			double efficiency = getEffectEfficiency(heldItemMainhand);
			double modifier = 1 + efficiency * (levelFortune + 2);
			event.setExpToDrop((int) (event.getExpToDrop() * modifier));
		}
	}*/ // abstracted to exp boost

	@SubscribeEvent(priority = EventPriority.LOWEST) // this should always go last
	public void voidingKillsRemoveDrops(LivingDropsEvent event) { // this is dumb, but bosses are built different and don't care about loot modifiers
		Entity entity = event.getEntity();
		if (!shouldVoidingAffect(event.getSource(), entity)) {
			return;
		}
		event.getDrops().clear(); // just to be sure dangit
		if (entity instanceof WitherBoss) { // funny music disc easter egg
			ItemEntity drop = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Registry.PLAYING_WITH_POWER_DISC.get(), 1));
			drop.setDefaultPickUpDelay(); // i can tell this is janky because it doesn't work without this line
			event.getDrops().add(drop);
		}
	}

	private boolean shouldVoidingAffect(@Nullable DamageSource source, Entity target) {
		if (target instanceof Player)
			return false;
		return hasEffect(source);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiStatBar getStatBar() {
		final IStatGetter voidingGetter = new StatGetterEffectLevel(getEffect(), 1, 0);
		final IStatGetter expBoostGetter = new StatGetterEffectEfficiency(ItemEffect.get("tetrapak:expboost"), 1);
		//final IStatGetter voidingLootingGetter = new StatGetterEnchantmentLevel(Enchantments.MOB_LOOTING, 1.0D);
		//final IStatGetter voidingFortuneGetter = new StatGetterEnchantmentLevel(Enchantments.BLOCK_FORTUNE, 1.0D);
		return new GuiStatBar(0, 0, 59, getStatsPath(),
			0, 1, false, voidingGetter, LabelGetterBasic.integerLabel,
			(player, itemStack) -> I18n.get(getTooltipPath(),
				1 + (expBoostGetter.getValue(player, itemStack))
				, 1 + (expBoostGetter.getValue(player, itemStack))));
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("voiding");
	}

	@Override
	public Function<LootItemCondition[], VoidingLootModifier> getModifierConstructor() {
		return VoidingLootModifier::new;
	}
}