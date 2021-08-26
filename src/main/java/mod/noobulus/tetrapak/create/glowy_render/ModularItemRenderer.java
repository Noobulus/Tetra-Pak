package mod.noobulus.tetrapak.create.glowy_render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ModularItemRenderer extends CustomRenderedItemModelRenderer<WrappedCustomModel<?>> {
	@Override
	protected void render(ItemStack stack, WrappedCustomModel<?> model, PartialItemModelRenderer renderer, MatrixStack ms,
						  IRenderTypeBuffer buffer, int light, int overlay) {
		System.out.println("render");
	}
}
