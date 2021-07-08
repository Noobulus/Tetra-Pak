package mod.noobulus.tetrapak;

import mod.noobulus.tetrapak.loot.LootConditions;
import mod.noobulus.tetrapak.networking.Packets;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TetraPak.MODID)
public class TetraPak {
	public static final String MODID = "tetrapak";

	public TetraPak() {
		Mods.registerEventListeners();
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(GlobalLootModifierSerializer.class, Mods::registerLootModifiers);
		modEventBus.addListener(Mods::clientSetup);
		modEventBus.addListener((FMLCommonSetupEvent event) -> Packets.registerPackets());
		LootConditions.register();
	}
}