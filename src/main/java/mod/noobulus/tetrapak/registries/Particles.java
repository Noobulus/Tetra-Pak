package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.particle.MoonstrikeStageParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Particles {
	public static final DeferredRegister<ParticleType<?>> MOONSTRIKE_PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "tetrapak");
	@SuppressWarnings("unused")
	public static final RegistryObject<BasicParticleType>
		MOONSTRIKE_STAGE_0 = MOONSTRIKE_PARTICLES.register("moonstrike_stage_0", () -> new BasicParticleType(true)),
		MOONSTRIKE_STAGE_1 = MOONSTRIKE_PARTICLES.register("moonstrike_stage_1", () -> new BasicParticleType(true)),
		MOONSTRIKE_STAGE_2 = MOONSTRIKE_PARTICLES.register("moonstrike_stage_2", () -> new BasicParticleType(true)),
		MOONSTRIKE_STAGE_3 = MOONSTRIKE_PARTICLES.register("moonstrike_stage_3", () -> new BasicParticleType(true));

	static {
		MOONSTRIKE_PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	private Particles() {
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@OnlyIn(Dist.CLIENT)
	public static void registerParticles(ParticleFactoryRegisterEvent event) {
		ParticleManager manager = Minecraft.getInstance().particles;
		MOONSTRIKE_PARTICLES.getEntries().stream().map(RegistryObject::get).forEach(sprite -> manager.registerFactory(sprite, MoonstrikeStageParticle.Factory::new));
	}
}
