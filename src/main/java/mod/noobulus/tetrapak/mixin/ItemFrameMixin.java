package mod.noobulus.tetrapak.mixin;

import mod.noobulus.tetrapak.Mods;
import mod.noobulus.tetrapak.create.refined_radiance.FloatingEffect;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntity.class)
public class ItemFrameMixin {
	@Inject(at = @At("HEAD"), method = "attackEntityFrom")
	private void onSpawnDropsPre(DamageSource damageSource, float p_70097_2_, CallbackInfoReturnable<Boolean> cir) {
		if (!Mods.CREATE.isLoaded)
			return;
		FloatingEffect.INSTANCE.checkFloatiness(damageSource);
	}

	@Inject(at = @At("RETURN"), method = "attackEntityFrom")
	private void onSpawnDropsEnd(DamageSource damageSource, float p_70097_2_, CallbackInfoReturnable<Boolean> cir) {
		FloatingEffect.INSTANCE.resetFloatiness();
	}
}
