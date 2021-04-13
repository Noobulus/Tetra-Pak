package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.entities.FragileFallingBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class Entities {
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TetraPak.MODID);
	public static final RegistryObject<EntityType<FragileFallingBlock>> FRAGILE_FALLING_BLOCK = ENTITY_TYPES.register("fragile_falling_block",
		() -> EntityType.Builder.<FragileFallingBlock>create(FragileFallingBlock::new, EntityClassification.MISC).size(0.98F, 0.98F).maxTrackingRange(10).trackingTickInterval(20).disableSerialization().build(null));

	private Entities() {
	}

	public static void register(IEventBus bus) {
		ENTITY_TYPES.register(bus);
	}
}
