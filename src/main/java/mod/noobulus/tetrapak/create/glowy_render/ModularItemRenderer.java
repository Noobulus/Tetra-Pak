package mod.noobulus.tetrapak.create.glowy_render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ModularItemRenderer extends ItemStackTileEntityRenderer {
	private static final int MAX_LIGHT = 0xF000F0;

	@Override
	public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		IVertexBuilder ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffer, RenderType.cutout(), true, stack.hasFoil());
		PartialItemModelRenderer renderer = PartialItemModelRenderer.of(stack, transformType, ms, buffer, overlay);
		IBakedModel mainModel = Minecraft.getInstance()
			.getItemRenderer()
			.getModel(stack, null, null);

		ms.pushPose();

		Minecraft.getInstance().getItemRenderer().renderModelLists(mainModel, stack, light, overlay, ms, ivertexbuilder);

		ms.translate(0.5, 0.5, 0.5);
		renderer.renderGlowing(FancyRenderManager.GLOWY_BITS.get("axe_glow"), MAX_LIGHT);

		ms.popPose();
	}
}
