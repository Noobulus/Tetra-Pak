package mod.noobulus.tetrapak.eidolon;

import mod.noobulus.tetrapak.loot.VoidingLootModifier;
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

public class ReapingEffect implements IHoloDescription, ILootModifier<ReapingLootModifier> {
    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (target.isEntityUndead()) {
            int levelLooting = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, event.getAttackingPlayer().getMainHandItem());
            double modifier = 1 + (getEffectEfficiency(lastActive) * (levelLooting + 2));
            ItemEntity drop = new ItemEntity(source.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(),
                    new ItemStack(Registry.SOUL_SHARD.get(), source.world.rand.nextInt(2 + looting)));
            drop.setDefaultPickupDelay();
            event.getDrops().add(drop);
        }
    }
}