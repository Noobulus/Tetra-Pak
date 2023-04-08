package mod.noobulus.tetrapak.effects.eidolon;

import com.mojang.authlib.GameProfile;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.ITooltipGetter;

public class CleavingEffect implements IHoloDescription, IEventBusListener {
	@SubscribeEvent
	public void onDeath(LivingDropsEvent event) {
		if (hasEffect(event.getSource())) {
			LivingEntity targetEntity = event.getEntity();
			int looting = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, ((LivingEntity) event.getSource().getDirectEntity()).getMainHandItem());
			ItemStack head = ItemStack.EMPTY;
			if (targetEntity instanceof WitherSkeleton) head = new ItemStack(Items.WITHER_SKELETON_SKULL);
			else if (targetEntity instanceof Skeleton) head = new ItemStack(Items.SKELETON_SKULL);
			else if (targetEntity instanceof Zombie) head = new ItemStack(Items.ZOMBIE_HEAD);
			else if (targetEntity instanceof Creeper) head = new ItemStack(Items.CREEPER_HEAD);
			else if (targetEntity instanceof EnderDragon) head = new ItemStack(Items.DRAGON_HEAD);
			else if (targetEntity instanceof Player) {
				head = new ItemStack(Items.PLAYER_HEAD);
				GameProfile gameprofile = ((Player) targetEntity).getGameProfile();
				head.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameprofile));
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
