package mod.noobulus.tetrapak.predicate;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;

public abstract class AbstractPredicateManager<T extends AbstractPredicate<?, T>> {
	protected IForgeRegistry<T> registry = null;

	@SubscribeEvent
	public void onNewRegistry(RegistryEvent.NewRegistry event) {
		registry = new RegistryBuilder<T>()
			.setType(getPredicateClass())
			.setName(TetraPak.asId(getRegistryName()))
			.create();
	}

	@SubscribeEvent
	public void onRegisterPredicates(RegistryEvent.Register<T> event) {
		registerContents(event.getRegistry());
	}

	public void registerContents(IForgeRegistry<T> registry) {

	}

	protected abstract Class<T> getPredicateClass();

	protected abstract String getRegistryName();

	@Nullable
	public IForgeRegistry<T> getRegistry() {
		return registry;
	}

	public void register(IEventBus modEventBus) {
		modEventBus.addGenericListener(getPredicateClass(), this::onRegisterPredicates);
		modEventBus.addListener(this::onNewRegistry);
	}
}
