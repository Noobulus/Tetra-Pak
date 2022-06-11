package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundEvents {
	// deferd registry for sounds
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BuildConfig.MODID);
	public static final RegistryObject<SoundEvent>
			PLAYING_WITH_POWER = addSound("playing_with_power");

	// register to event bus
	static {
		SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	// private constructor to prevent instantiation
	private SoundEvents() {
	}

	private static RegistryObject<SoundEvent> addSound(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(BuildConfig.MODID, name)));
	}
}
