package mod.noobulus.tetrapak.eidolon;

//import src.main.java.elucent.eidolon.Registry;
import mod.noobulus.tetrapak.loot.ReapingLootModifier;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.*;
import mod.noobulus.tetrapak.util.DamageBufferer;
import net.minecraft.entity.player.PlayerEntity;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ILootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.*;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ReapingEffect implements IHoloDescription, ILootModifier<ReapingLootModifier> {
    @SubscribeEvent
    public void reapingSouls(LivingDropsEvent event) {
        LivingEntity target = event.getEntityLiving();
        DamageSource lastActive = DamageBufferer.getLastActiveDamageSource();
        if (target.getMobType() == CreatureAttribute.UNDEAD) {
            if(event.getSource().getEntity() instanceof PlayerEntity) {
                int levelLooting = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, ((PlayerEntity)event.getSource().getEntity()).getMainHandItem());
                double modifier = 1 + (getEffectEfficiency(lastActive) * (levelLooting + 2));
            }
            //Entity victim = event.getEntity();
            ItemEntity drop = new ItemEntity(ForgeRegistries.ITEMS.getValue(new ResourceLocation("eidolon","soul_shard")));
            //drop.setDefaultPickupDelay();
            event.getDrops().add(drop);
        }
    }



    @Override
    public ItemEffect getEffect() { return ITetraEffect.get("reaping"); }
}