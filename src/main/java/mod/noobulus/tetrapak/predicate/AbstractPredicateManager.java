package mod.noobulus.tetrapak.predicate;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractPredicateManager<T extends AbstractPredicate<?, T>> {
	private final Class<T> registryClass;
	private final String registryName;
	protected IForgeRegistry<T> registry = null;

	protected AbstractPredicateManager(Class<T> registryClass, String registryName) {
		this.registryClass = registryClass;
		this.registryName = registryName;
	}

	@SubscribeEvent
	public void onNewRegistry(RegistryEvent.NewRegistry event) {
		registry = new RegistryBuilder<T>()
			.setType(registryClass)
			.setName(TetraPak.asId(registryName))
			.create();
	}

	@SubscribeEvent
	public void onRegisterPredicates(RegistryEvent.Register<T> event) {
		registerContents(event.getRegistry());
	}

	public void registerContents(IForgeRegistry<T> registry) {

	}

	@Nullable
	public IForgeRegistry<T> getRegistry() {
		return registry;
	}

	public AbstractPredicateManager<T> register(IEventBus modEventBus) {
		modEventBus.addGenericListener(registryClass, this::onRegisterPredicates);
		modEventBus.addListener(this::onNewRegistry);
		return this;
	}

	public static class Builder<T extends AbstractPredicate<?, T>> {
		private final Map<ResourceLocation, Supplier<T>> registryObjects;
		private final Class<T> registryClass;
		private final String registryName;

		public Builder(Class<T> registryClass, String registryName) {
			this.registryClass = registryClass;
			this.registryName = registryName;
			registryObjects = new HashMap<>();
		}

		public Builder<T> queue(String id, Supplier<T> supplier) {
			return queue(TetraPak.asId(id), supplier);
		}

		public Builder<T> queue(ResourceLocation id, Supplier<T> supplier) {
			registryObjects.put(id, supplier);
			return this;
		}

		public AbstractPredicateManager<T> build() {
			return new AbstractPredicateManager<T>(registryClass, registryName) {
				@Override
				public void registerContents(IForgeRegistry<T> registry) {
					registryObjects.forEach((name, supplier) -> registry.register(supplier.get().setRegistryName(name)));
				}
			}.register(FMLJavaModLoadingContext.get().getModEventBus());
		}
	}
}
