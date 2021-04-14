package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.entities.FragileFallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public enum Entities {
	FRAGILE_FALLING_BLOCK("fragile_falling_block", () -> EntityType.Builder.<FragileFallingBlock>create(FragileFallingBlock::new, EntityClassification.MISC).size(0.98F, 0.98F).maxTrackingRange(10).trackingTickInterval(20));

	public final String id;
	public final RegistryObject<EntityType<? extends Entity>> entityType;

	Entities(String id, Supplier<EntityType.Builder<? extends Entity>> supplier) {
		this.id = id;
		entityType = Registry.ENTITY_TYPES.register(id, () -> supplier.get().build(id));
	}

	public static void register(IEventBus bus) {
		Registry.ENTITY_TYPES.register(bus);
	}

	private static final class Registry {
		private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TetraPak.MODID);
	}
}
