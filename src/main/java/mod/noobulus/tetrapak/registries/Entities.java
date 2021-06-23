package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.entities.FragileFallingBlock;
import mod.noobulus.tetrapak.entities.FragileFallingBlockRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = TetraPak.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Entities {
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TetraPak.MODID);
	public static final RegistryObject<EntityType<FragileFallingBlock>> FRAGILE_FALLING_BLOCK = register("fragile_falling_block",
		EntityType.Builder.<FragileFallingBlock>create(FragileFallingBlock::new, EntityClassification.MISC)
			.size(0.98F, 0.98F)
			.maxTrackingRange(10)
			.trackingTickInterval(20));

	static {
		ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	private Entities() {
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String id, EntityType.Builder<T> type) {
		return ENTITY_TYPES.register(id, () -> type.build(id));
	}


	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(FRAGILE_FALLING_BLOCK.get(), FragileFallingBlockRenderer::new);
	}
}
