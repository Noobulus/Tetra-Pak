package mod.noobulus.tetrapak;

import mod.noobulus.tetrapak.loot.LootConditions;
import mod.noobulus.tetrapak.registries.Entities;
import mod.noobulus.tetrapak.registries.Particles;
import mod.noobulus.tetrapak.util.DamageBufferer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TetraPak.MODID)
public class TetraPak {
	public static final String MODID = "tetrapak";

	public TetraPak() {
		MinecraftForge.EVENT_BUS.register(DamageBufferer.class);

		Mods.registerEventListeners();

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(GlobalLootModifierSerializer.class, Mods::registerLootModifiers);
		modEventBus.addListener(Mods::clientSetup);
		Entities.register(modEventBus);
		Particles.PARTICLES.register(modEventBus);

		LootConditions.register();
	}
}