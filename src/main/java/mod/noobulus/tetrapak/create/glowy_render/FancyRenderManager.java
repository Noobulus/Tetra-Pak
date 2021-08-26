package mod.noobulus.tetrapak.create.glowy_render;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.util.IEventBusListener;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.tetra.TetraMod;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FancyRenderManager implements IEventBusListener {
	public static final ResourceLocation MODULAR_DOUBLE = new ResourceLocation(TetraMod.MOD_ID, "modular_double");
	public static final Map<String, IBakedModel> GLOWY_BITS = new HashMap<>();
	private static final String[] GLOWY_BIT_KEYS = new String[]{"axe_glow", "adze_glow"};

	@OnlyIn(Dist.CLIENT)
	public void onModelRegistry(ModelRegistryEvent event) {
		Item modularDouble = ForgeRegistries.ITEMS.getValue(MODULAR_DOUBLE);
		if (modularDouble == null)
			return;
		ItemStackTileEntityRenderer renderer = new ModularItemRenderer();
		try {
			Field ister = Item.class.getDeclaredField("ister");
			ister.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(ister, ister.getModifiers() & ~Modifier.FINAL);

			ister.set(modularDouble, (Supplier<? extends ItemStackTileEntityRenderer>) () -> renderer);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			TetraPak.LOGGER.error("Error registering custom renderer: Can not invoke ister field: {}", e.getMessage());
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void onModelBake(ModelBakeEvent event) {
		GLOWY_BITS.clear();
		ModelLoader loader = event.getModelLoader();
		Arrays.stream(GLOWY_BIT_KEYS).forEach(k ->
			GLOWY_BITS.put(k, loader.bake(new ResourceLocation(BuildConfig.MODID, "item/glowing/" + k), ModelRotation.X0_Y0)));
	}

	@Override
	public void registerModListeners(IEventBus bus) {
		IEventBusListener.super.registerModListeners(bus);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			bus.addListener(this::onModelRegistry);
			bus.addListener(this::onModelBake);
		});
	}
}
