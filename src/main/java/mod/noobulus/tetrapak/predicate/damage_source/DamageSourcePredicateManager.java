package mod.noobulus.tetrapak.predicate.damage_source;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.TetraPak;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = BuildConfig.MODID)
public class DamageSourcePredicateManager {
	public static IForgeRegistry<AbstractDamageSourcePredicate> REGISTRY = null;

	private DamageSourcePredicateManager() {
	}

	@SubscribeEvent
	public static void onNewRegistry(RegistryEvent.NewRegistry event) {
		REGISTRY = new RegistryBuilder<AbstractDamageSourcePredicate>()
			.setType(AbstractDamageSourcePredicate.class)
			.setName(TetraPak.asId("damagesourcepredicates"))
			.create();
	}

	@SubscribeEvent
	public static void onRegisterEntityPredicates(RegistryEvent.Register<AbstractDamageSourcePredicate> event) {
		IForgeRegistry<AbstractDamageSourcePredicate> registry = event.getRegistry();
		registry.register(new TestWeaponPredicate().setRegistryName(TetraPak.asId("weapon")));
	}
}