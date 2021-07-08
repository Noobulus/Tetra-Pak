package mod.noobulus.tetrapak.mixin;

import mod.noobulus.tetrapak.Mods;
import mod.noobulus.tetrapak.create.refined_radiance.FloatingEffect;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInteractionManager.class)
public class PlayerInteractionManagerMixin {
	@Shadow
	public ServerPlayerEntity player;

	@Inject(at = @At("HEAD"), method = "removeBlock", remap = false)
	private void onRemoveBlockPre(BlockPos pos, boolean canHarvest, CallbackInfoReturnable<Boolean> cir) {
		if (!Mods.CREATE.isLoaded || player == null)
			return;
		FloatingEffect.INSTANCE.checkFloatiness(player.getHeldItemMainhand());
	}

	@Inject(at = @At("RETURN"), method = "removeBlock", remap = false)
	private void onRemoveBlockPost(BlockPos pos, boolean canHarvest, CallbackInfoReturnable<Boolean> cir) {
		FloatingEffect.INSTANCE.resetFloatiness();
	}
}
