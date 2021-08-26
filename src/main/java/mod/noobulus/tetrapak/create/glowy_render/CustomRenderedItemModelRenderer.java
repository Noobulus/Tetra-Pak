package mod.noobulus.tetrapak.create.glowy_render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CustomRenderedItemModelRenderer<M extends BakedModelWrapper<?>> extends ItemStackTileEntityRenderer {

	@Override
	@SuppressWarnings("unchecked")
	public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		M mainModel = ((M) Minecraft.getInstance()
			.getItemRenderer()
			.getModel(stack, null, null));
		PartialItemModelRenderer renderer = PartialItemModelRenderer.of(stack, transformType, ms, buffer, overlay);

		ms.pushPose();
		ms.translate(0.5F, 0.5F, 0.5F);
		render(stack, mainModel, renderer, ms, buffer, light, overlay);
		ms.popPose();
	}

	protected void render(ItemStack stack, M model, PartialItemModelRenderer renderer, MatrixStack ms,
						  IRenderTypeBuffer buffer, int light, int overlay) {

	}
}
