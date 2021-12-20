package mod.noobulus.tetrapak.mixin;

import mod.noobulus.tetrapak.Mods;
import mod.noobulus.tetrapak.create.refined_radiance.FloatingEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartContainer.class)
public class ContainerMinecartEntityMixin {
	@Inject(at = @At("HEAD"), method = "destroy")
	private void onKillMinecartPre(DamageSource damageSource, CallbackInfo ci) {
		if (!Mods.CREATE.isLoaded)
			return;
		FloatingEffect.INSTANCE.checkFloatiness(damageSource);
	}

	@Inject(at = @At("RETURN"), method = "destroy")
	private void onKillMinecartPost(DamageSource damageSource, CallbackInfo ci) {
		FloatingEffect.INSTANCE.resetFloatiness();
	}
}
