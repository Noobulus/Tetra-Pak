package mod.noobulus.tetrapak.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class FragileFallingBlockRenderer<T extends FallingBlockEntity> extends EntityRenderer<T> {
	private final FallingBlockRenderer renderer;

	public FragileFallingBlockRenderer(EntityRendererManager manager) {
		super(manager);
		renderer = new FallingBlockRenderer(manager);
	}

	@Override
	public void render(T entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225623_6_) {
		renderer.render(entity, p_225623_2_, p_225623_3_, matrixStack, buffer, p_225623_6_);
	}

	@Override
	public ResourceLocation getEntityTexture(T e) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}
