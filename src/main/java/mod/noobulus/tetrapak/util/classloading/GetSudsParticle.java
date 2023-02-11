package mod.noobulus.tetrapak.util.classloading;

import net.mehvahdjukaar.supplementaries.reg.ModParticles;
import net.minecraft.core.particles.SimpleParticleType;

// born to code
// classloading is a fuck
public class GetSudsParticle {
    public static SimpleParticleType getSudsParticle() {
        return ModParticles.BUBBLE_BLOCK_PARTICLE.get();
    }
}
