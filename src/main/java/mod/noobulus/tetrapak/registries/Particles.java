package mod.noobulus.tetrapak.registries;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Particles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "tetrapak");

    public static final RegistryObject<BasicParticleType> MOONSTRIKE_STAGE_0 = PARTICLES.register("moonstrike_stage_0", () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> MOONSTRIKE_STAGE_1 = PARTICLES.register("moonstrike_stage_1", () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> MOONSTRIKE_STAGE_2 = PARTICLES.register("moonstrike_stage_2", () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> MOONSTRIKE_STAGE_3 = PARTICLES.register("moonstrike_stage_3", () -> new BasicParticleType(true));


}
