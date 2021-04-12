package mod.noobulus.tetrapak;

import mod.noobulus.tetrapak.util.DamageBufferer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("tetrapak")
public class TetraPak {
	public TetraPak() {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(DamageBufferer.class);

		Mods.registerEventListeners();

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(GlobalLootModifierSerializer.class, Mods::registerLootModifiers);
		modEventBus.addListener(Mods::clientSetup);
	}
}