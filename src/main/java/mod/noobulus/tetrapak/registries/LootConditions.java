package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.loot.HasBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootConditions {
	public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registry.LOOT_CONDITION_TYPE.key(), BuildConfig.MODID);
	public static final RegistryObject<LootItemConditionType> HAS_BLOCK_ENTITY = LOOT_CONDITION_TYPES.register("has_block_entity", () -> new LootItemConditionType(new HasBlockEntity.Serializer()));

	// register to event bus
	static {
		LOOT_CONDITION_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	// private constructor, prevent instantiation
	private LootConditions() {
	}
}
