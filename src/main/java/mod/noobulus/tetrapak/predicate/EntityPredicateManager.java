package mod.noobulus.tetrapak.predicate;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.TetraPak;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = BuildConfig.MODID)
public class EntityPredicateManager {
	public static IForgeRegistry<AbstractEntityPredicate> REGISTRY = null;
	private EntityPredicateManager() {
	}

	@SubscribeEvent
	public static void onNewRegistry(RegistryEvent.NewRegistry event) {
		REGISTRY = new RegistryBuilder<AbstractEntityPredicate>()
			.setType(AbstractEntityPredicate.class)
			.setName(TetraPak.asId("entitypredicates"))
			.create();
	}

	@SubscribeEvent
	public static void onRegisterEntityPredicates(RegistryEvent.Register<AbstractEntityPredicate> event) {
		IForgeRegistry<AbstractEntityPredicate> registry = event.getRegistry();
		registry.register(new CreatureAttributePredicate().setRegistryName(TetraPak.asId("creature_attribute")));
	}
}
