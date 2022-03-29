package mod.noobulus.tetrapak.mixin;

import mod.noobulus.tetrapak.Mods;
import mod.noobulus.tetrapak.util.classloading.GetSudsParticle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;
import se.mickelus.tetra.items.modular.impl.toolbelt.booster.UtilBooster;

@Mixin(UtilBooster.class)
public class UtilBoosterMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"), method = "Lse/mickelus/tetra/items/modular/impl/toolbelt/booster/UtilBooster;boostPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/nbt/CompoundTag;I)V", cancellable = true)
	private static void boostPlayer(Player player, CompoundTag tag, int level, CallbackInfo ci) {
		if (Mods.SUPPLEMENTARIES.isLoaded && hasBubbler(player)) {
			((ServerLevel)player.level).sendParticles(GetSudsParticle.getSudsParticle(), player.getX() - 0.2D + Math.random() * 0.4D, player.getY() + Math.random() * 0.2D, player.getZ() - 0.2D + Math.random() * 0.4D, 8, 0.0D, -0.3D, 0.0D, 0.1D);
			ci.cancel(); // don't add regular smoke particles
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), method = "Lse/mickelus/tetra/items/modular/impl/toolbelt/booster/UtilBooster;boostHorizontal(Lnet/minecraft/world/entity/player/Player;)V", cancellable = true)
	private static void boostHorizontal(Player player, CallbackInfo ci) {
		if (Mods.SUPPLEMENTARIES.isLoaded && hasBubbler(player)) {
			Vec3 direction = getAbsoluteMotion(-player.xxa, -player.zza, player.getYRot());
			player.getCommandSenderWorld().addParticle(GetSudsParticle.getSudsParticle(), player.getX(), player.getY() + (double)player.getBbHeight() * 0.4D, player.getZ(), Math.random() * (0.2D * direction.x + 0.07D) - 0.05D, Math.random() * 0.1D - 0.05D, Math.random() * (0.2D * direction.z + 0.07D) - 0.05D);
			ci.cancel(); // don't add regular smoke particles
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"), method = "Lse/mickelus/tetra/items/modular/impl/toolbelt/booster/UtilBooster;boostPlayerCharged(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/nbt/CompoundTag;I)V", cancellable = true)
	private static void boostPlayerCharged(Player player, CompoundTag tag, int level, CallbackInfo ci) {
		if (Mods.SUPPLEMENTARIES.isLoaded && hasBubbler(player)) {
			((ServerLevel)player.level).sendParticles(GetSudsParticle.getSudsParticle(), player.getX() - 0.2D + Math.random() * 0.4D, player.getY() + Math.random() * 0.2D, player.getZ() - 0.2D + Math.random() * 0.4D, 8, 0.0D, -0.3D, 0.0D, 0.1D);
			ci.cancel(); // don't add regular smoke particles
		}
	}

	private static Vec3 getAbsoluteMotion(float strafe, float forward, float facing) {
		float sin = Mth.sin(facing * 0.017453292F);
		float cos = Mth.cos(facing * 0.017453292F);
		return new Vec3((double)(strafe * cos - forward * sin), 0.0D, (double)(forward * cos + strafe * sin));
	}

	private static boolean hasBubbler(Player player) {
		ItemStack itemStack = ToolbeltHelper.findToolbelt(player);
		return getBubblerLevel(itemStack) > 0;
	}

	private static int getBubblerLevel(ItemStack itemStack) {
		if (!itemStack.isEmpty() && itemStack.getItem() instanceof IModularItem) {
			IModularItem item = (IModularItem)itemStack.getItem();
			int lvl = item.getEffectLevel(itemStack, ItemEffect.get("tetrapak:bubbling"));
			return lvl;
		} else {
			return 0;
		}
	}
}
