package mod.noobulus.tetrapak.util;


import mod.noobulus.tetrapak.particle.MoonstrikeStage0Particle;
import mod.noobulus.tetrapak.particle.MoonstrikeStage1Particle;
import mod.noobulus.tetrapak.particle.MoonstrikeStage2Particle;
import mod.noobulus.tetrapak.particle.MoonstrikeStage3Particle;
import mod.noobulus.tetrapak.registries.Particles;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "tetrapak", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleHelper {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(Particles.MOONSTRIKE_STAGE_0.get(), MoonstrikeStage0Particle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(Particles.MOONSTRIKE_STAGE_1.get(), MoonstrikeStage1Particle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(Particles.MOONSTRIKE_STAGE_2.get(), MoonstrikeStage2Particle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(Particles.MOONSTRIKE_STAGE_3.get(), MoonstrikeStage3Particle.Factory::new);
    }
}
