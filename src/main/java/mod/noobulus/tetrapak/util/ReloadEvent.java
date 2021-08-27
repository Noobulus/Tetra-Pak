package mod.noobulus.tetrapak.util;

import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// bc the vanilla reload listener system sucks
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReloadEvent extends Event {
	@SubscribeEvent
	public static void registerReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new ReloadListener<Void>() {
			@Override
			protected Void prepare(@Nonnull IResourceManager iResourceManager, @Nonnull IProfiler iProfiler) {
				return null;
			}

			@Override
			protected void apply(@Nullable Void unused, @Nonnull IResourceManager iResourceManager, @Nonnull IProfiler iProfiler) {
				MinecraftForge.EVENT_BUS.post(new ReloadEvent());
			}
		});
	}
}
