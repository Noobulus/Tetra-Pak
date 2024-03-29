package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.particle.MoonstrikeStageParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Particles {
	public static final DeferredRegister<ParticleType<?>> MOONSTRIKE_PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BuildConfig.MODID);
	@SuppressWarnings("unused")
	public static final RegistryObject<SimpleParticleType>
			MOONSTRIKE_STAGE_0 = MOONSTRIKE_PARTICLES.register("moonstrike_stage_0", () -> new SimpleParticleType(true)),
		MOONSTRIKE_STAGE_1 = MOONSTRIKE_PARTICLES.register("moonstrike_stage_1", () -> new SimpleParticleType(true)),
		MOONSTRIKE_STAGE_2 = MOONSTRIKE_PARTICLES.register("moonstrike_stage_2", () -> new SimpleParticleType(true)),
		MOONSTRIKE_STAGE_3 = MOONSTRIKE_PARTICLES.register("moonstrike_stage_3", () -> new SimpleParticleType(true));

	static {
		MOONSTRIKE_PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	private Particles() {
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@OnlyIn(Dist.CLIENT)
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		ParticleEngine manager = Minecraft.getInstance().particleEngine;
		MOONSTRIKE_PARTICLES.getEntries().stream().map(RegistryObject::get).forEach(sprite -> manager.register(sprite, MoonstrikeStageParticle.Factory::new));
	}
}
