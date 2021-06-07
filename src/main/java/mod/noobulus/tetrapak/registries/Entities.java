package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.entities.FragileFallingBlock;
import mod.noobulus.tetrapak.entities.FragileFallingBlockRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Entities {
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TetraPak.MODID);
	public static final RegistryObject<EntityType<FragileFallingBlock>> FRAGILE_FALLING_BLOCK = register("fragile_falling_block",
		EntityType.Builder.<FragileFallingBlock>of(FragileFallingBlock::new, EntityClassification.MISC)
			.sized(0.98F, 0.98F)
			.clientTrackingRange(10)
			.updateInterval(20));

	private Entities() {
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String id, EntityType.Builder<T> type) {
		return ENTITY_TYPES.register(id, () -> type.build(id));
	}

	public static void register(IEventBus bus) {
		ENTITY_TYPES.register(bus);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(Entities::clientInit));
	}

	@OnlyIn(Dist.CLIENT)
	private static void clientInit(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(FRAGILE_FALLING_BLOCK.get(), FragileFallingBlockRenderer::new);
	}
}
