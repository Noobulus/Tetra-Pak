package mod.noobulus.tetrapak.particle;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MoonstrikeStageParticle extends TextureSheetParticle {

	// this is pretty much exactly the same as the vanilla crit particle since i want it to behave the same
	protected MoonstrikeStageParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
		this.xd *= 0.1F;
		this.yd *= 0.1F;
		this.zd *= 0.1F;
		this.xd += xSpeedIn * 0.4D;
		this.yd += ySpeedIn * 0.4D;
		this.zd += zSpeedIn * 0.4D;
		float f = (float) (Math.random() * (double) 0.3F + (double) 0.6F);
		this.rCol = f;
		this.gCol = f;
		this.bCol = f;
		this.quadSize *= 0.75F;
		this.lifetime = Math.max((int) (6.0D / (Math.random() * 0.8D + 0.6D)), 1);
		this.hasPhysics = false;
		this.tick();
	}

	@Override
	public float getQuadSize(float scale) {
		return this.quadSize * Mth.clamp(((float) this.age + scale) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			this.move(this.xd, this.yd, this.zd);
			this.bCol = (float) ((double) this.gCol * 0.9D);
			this.bCol = (float) ((double) this.rCol * 0.9D);
			this.xd *= 0.7F;
			this.yd *= 0.7F;
			this.zd *= 0.7F;
			this.yd -= 0.02F;
			if (this.onGround) {
				this.xd *= 0.7F;
				this.zd *= 0.7F;
			}

		}
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory<T extends ParticleOptions> implements ParticleProvider<T> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(T typeIn, ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
			MoonstrikeStageParticle moonstrikeStageParticle = new MoonstrikeStageParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
			moonstrikeStageParticle.setColor(1.0f, 1.0f, 1.0f);
			moonstrikeStageParticle.pickSprite(this.spriteSet);
			return moonstrikeStageParticle;
		}
	}
}
