package mod.noobulus.tetrapak.registries;

import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static mod.noobulus.tetrapak.registries.SoundEvents.PLAYING_WITH_POWER;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Items {
	// Deferred registry for items
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BuildConfig.MODID);

	public static RegistryObject<Item>
			PLAYING_WITH_POWER_DISC = ITEMS.register("music_disc_playing_with_power", () -> new RecordItem(2, PLAYING_WITH_POWER,
			new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE)));

	// register to event bus
	static {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
