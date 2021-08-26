package mod.noobulus.tetrapak.create.glowy_render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraftforge.client.model.BakedModelWrapper;

public class WrappedCustomModel<T extends IBakedModel> extends BakedModelWrapper<T> {
	public WrappedCustomModel(T originalModel) {
		super(originalModel);
	}

	@Override
	public boolean isCustomRenderer() {
		return true;
	}

	@Override
	public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
		return new WrappedCustomModel<>(super.handlePerspective(cameraTransformType, mat));
	}
}
