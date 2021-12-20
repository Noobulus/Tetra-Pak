package mod.noobulus.tetrapak.mixin;

import mod.noobulus.tetrapak.Mods;
import mod.noobulus.tetrapak.create.refined_radiance.FloatingEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class PlayerInteractionManagerMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(at = @At("HEAD"), method = "removeBlock", remap = false)
	private void onRemoveBlockPre(BlockPos pos, boolean canHarvest, CallbackInfoReturnable<Boolean> cir) {
		if (!Mods.CREATE.isLoaded || player == null)
			return;
		FloatingEffect.INSTANCE.checkFloatiness(player.getMainHandItem());
	}

	@Inject(at = @At("RETURN"), method = "removeBlock", remap = false)
	private void onRemoveBlockPost(BlockPos pos, boolean canHarvest, CallbackInfoReturnable<Boolean> cir) {
		FloatingEffect.INSTANCE.resetFloatiness();
	}
}
