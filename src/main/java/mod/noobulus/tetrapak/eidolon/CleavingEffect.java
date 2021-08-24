package mod.noobulus.tetrapak.eidolon;

import com.mojang.authlib.GameProfile;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.ITooltipGetter;

public class CleavingEffect implements IHoloDescription {
    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        if (hasEffect(event.getSource())) {
            LivingEntity targetEntity = event.getEntityLiving();
            int looting = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, ((LivingEntity) event.getSource().getDirectEntity()).getMainHandItem());
            ItemStack head = ItemStack.EMPTY;
            if (targetEntity instanceof WitherSkeletonEntity) head = new ItemStack(Items.WITHER_SKELETON_SKULL);
            else if (targetEntity instanceof SkeletonEntity) head = new ItemStack(Items.SKELETON_SKULL);
            else if (targetEntity instanceof ZombieEntity) head = new ItemStack(Items.ZOMBIE_HEAD);
            else if (targetEntity instanceof CreeperEntity) head = new ItemStack(Items.CREEPER_HEAD);
            else if (targetEntity instanceof EnderDragonEntity) head = new ItemStack(Items.DRAGON_HEAD);
            else if (targetEntity instanceof PlayerEntity) {
                head = new ItemStack(Items.PLAYER_HEAD);
                GameProfile gameprofile = ((PlayerEntity)targetEntity).getGameProfile();
                head.getOrCreateTag().put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
            }
            if (!head.isEmpty()) {
                boolean doDrop = false;
                if (targetEntity.level.random.nextInt(20) == 0) doDrop = true;
                else for (int i = 0; i < looting; i++) {
                    if (targetEntity.level.random.nextInt(40) == 0) {
                        doDrop = true;
                        break;
                    }
                }
                if (doDrop) {
                    ItemEntity drop = new ItemEntity(targetEntity.level, targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), head);
                    drop.setDefaultPickUpDelay();
                    event.getDrops().add(drop);
                }
            }
        }
    }

    @Override
    public ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
        return (player, itemStack) -> I18n.get(getTooltipPath(),
                statGetter.getValue(player, itemStack), statGetter.getValue(player, itemStack));
    }

    @Override
    public ItemEffect getEffect() {
        return ITetraEffect.get("cleaving");
    }
}
