package mod.noobulus.tetrapak.mixin;

import mod.noobulus.tetrapak.create.glowy_render.FancyRenderManager;
import mod.noobulus.tetrapak.create.glowy_render.WrappedCustomModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.client.model.ModularOverrideList;

@Mixin(value = ModularOverrideList.class, remap = false)
public class ModularOverrideListMixin {
	@Inject(at = @At("RETURN"), method = "resolve", cancellable = true)
	public void onGetOverrideModel(IBakedModel originalModel, ItemStack stack, ClientWorld world, LivingEntity entity, CallbackInfoReturnable<IBakedModel> cir) {
		if (FancyRenderManager.MODULAR_DOUBLE.equals(stack.getItem().getRegistryName())) {
			cir.setReturnValue(new WrappedCustomModel<>(cir.getReturnValue()));
		}
	}
}
