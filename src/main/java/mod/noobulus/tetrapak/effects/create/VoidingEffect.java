package mod.noobulus.tetrapak.effects.create;

import mod.noobulus.tetrapak.Registry;
import mod.noobulus.tetrapak.effects.base.ExpBoostEffect;
import mod.noobulus.tetrapak.loot.modifier.VoidingLootModifier;
import mod.noobulus.tetrapak.util.DamageBufferer;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ILootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.*;

import javax.annotation.Nullable;
import java.util.function.Function;

public class VoidingEffect implements IHoloDescription, ILootModifier<VoidingLootModifier>, IEventBusListener {

	@SubscribeEvent(priority = EventPriority.LOWEST) // this should always go last
	public void voidingKillsRemoveDrops(LivingDropsEvent event) { // this is dumb, but bosses are built different and don't care about loot modifiers
		Entity entity = event.getEntity();
		if (!shouldVoidingAffect(event.getSource(), entity)) {
			return;
		}
		//event.getDrops().clear(); // good contingency but breaks solidifying
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
	public ItemEffect getEffect() {
		return ITetraEffect.get("voiding");
	}

	@Override
	public Function<LootItemCondition[], VoidingLootModifier> getModifierConstructor() {
		return VoidingLootModifier::new;
	}
}