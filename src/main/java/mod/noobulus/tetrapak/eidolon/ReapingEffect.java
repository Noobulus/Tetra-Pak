package mod.noobulus.tetrapak.eidolon;

import mod.noobulus.tetrapak.loot.ReapingLootModifier;
import mod.noobulus.tetrapak.util.DamageBufferer;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ILootModifier;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.function.Function;

public class ReapingEffect implements IHoloDescription, ILootModifier<ReapingLootModifier> {
    @SubscribeEvent
    public void reapingSouls(LivingDropsEvent event) {
        LivingEntity target = event.getEntityLiving();
        DamageSource lastActive = DamageBufferer.getLastActiveDamageSource();
        if (target.getMobType() == CreatureAttribute.UNDEAD) {
            if(event.getSource().getEntity() instanceof PlayerEntity) {
                int levelLooting = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, ((PlayerEntity)event.getSource().getEntity()).getMainHandItem());
                double modifier = 2 + (getEffectEfficiency(lastActive) * (levelLooting));
                ItemEntity drop = new ItemEntity(target.level, target.getX(), target.getY(), target.getZ(),
                        new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("eidolon","soul_shard")), ((int) Math.round(modifier))));
                event.getDrops().add(drop);
            }
        }
    }



    @Override
    public ItemEffect getEffect() { return ITetraEffect.get("reaping"); }

    @Override
    public Function<ILootCondition[], ReapingLootModifier> getModifierConstructor() {
        return ReapingLootModifier::new;
    }
}