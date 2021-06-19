package mod.noobulus.tetrapak.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MoonstrikeStage0Particle extends SpriteTexturedParticle {

    // this is pretty much exactly the same as the vanilla crit particle since i want it to behave the same
    protected MoonstrikeStage0Particle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.motionX *= (double)0.1F;
        this.motionY *= (double)0.1F;
        this.motionZ *= (double)0.1F;
        this.motionX += xSpeedIn * 0.4D;
        this.motionY += ySpeedIn * 0.4D;
        this.motionZ += zSpeedIn * 0.4D;
        float f = (float)(Math.random() * (double)0.3F + (double)0.6F);
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleScale *= 0.75F;
        this.maxAge = Math.max((int)(6.0D / (Math.random() * 0.8D + 0.6D)), 1);
        this.canCollide = false;
        this.tick();
    }

    public float getScale(float p_217561_1_) {
        return this.particleScale * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.move(this.motionX, this.motionY, this.motionZ);
            this.particleBlue = (float)((double)this.particleGreen * 0.9D);
            this.particleBlue = (float)((double)this.particleRed * 0.9D);
            this.motionX *= (double)0.7F;
            this.motionY *= (double)0.7F;
            this.motionZ *= (double)0.7F;
            this.motionY -= (double)0.02F;
            if (this.onGround) {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }

        }
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
            MoonstrikeStage0Particle moonstrikeStage0Particle = new MoonstrikeStage0Particle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            moonstrikeStage0Particle.setColor(1.0f, 1.0f, 1.0f);
            moonstrikeStage0Particle.selectSpriteRandomly(this.spriteSet);
            return moonstrikeStage0Particle;
        }
    }
}
