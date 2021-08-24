package mod.noobulus.tetrapak.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public interface IEventBusListener {
	default void register() {
		MinecraftForge.EVENT_BUS.register(this);
		registerModListeners(FMLJavaModLoadingContext.get().getModEventBus());
	}

	default void registerModListeners(IEventBus bus) {
	}
}
