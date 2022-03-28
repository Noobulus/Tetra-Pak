package mod.noobulus.tetrapak.util.classloading;

import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.core.particles.SimpleParticleType;

// born to code
// classloading is a fuck
public class GetSudsParticle {
    public static SimpleParticleType getSudsParticle() {
        return ModRegistry.SUDS_PARTICLE.get();
    }
}
