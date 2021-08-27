package mod.noobulus.tetrapak.util;

import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SelfReloadingLazyValue<T> extends RecalculatableLazyValue<T>{
	public SelfReloadingLazyValue(NonNullSupplier<T> supplier) {
		super(supplier);
	}

	@SubscribeEvent
	public void onReload(ReloadEvent event) {
		recalculate();
	}
}
