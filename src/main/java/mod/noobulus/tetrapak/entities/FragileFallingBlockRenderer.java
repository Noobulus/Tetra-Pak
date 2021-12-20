package mod.noobulus.tetrapak.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class FragileFallingBlockRenderer<T extends FallingBlockEntity> extends EntityRenderer<T> {
	private final FallingBlockRenderer renderer;

	public FragileFallingBlockRenderer(EntityRendererProvider.Context manager) {
		super(manager);
		renderer = new FallingBlockRenderer(manager);
	}

	@Override
	public void render(T entity, float p_225623_2_, float p_225623_3_, PoseStack matrixStack, MultiBufferSource buffer, int p_225623_6_) {
		renderer.render(entity, p_225623_2_, p_225623_3_, matrixStack, buffer, p_225623_6_);
	}

	@Override
	public ResourceLocation getTextureLocation(T e) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
