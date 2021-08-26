package mod.noobulus.tetrapak.create.glowy_render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ModularItemRenderer extends ItemStackTileEntityRenderer {
	@Override
	public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		IBakedModel mainModel = Minecraft.getInstance()
			.getItemRenderer()
			.getModel(stack, null, null);

		RenderType rendertype = RenderTypeLookup.getRenderType(stack, true);
		IVertexBuilder ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
		Minecraft.getInstance().getItemRenderer().renderModelLists(mainModel, stack, light, overlay, ms, ivertexbuilder);
	}
}
