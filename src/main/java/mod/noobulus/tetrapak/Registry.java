package mod.noobulus.tetrapak;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Registry {

    public static final Logger LOGGER = LogManager.getLogger(BuildConfig.MODID);

    // most of this is just shamelessly yoinked from eidolon but hey, at least it's expandable!
    static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BuildConfig.MODID);
    static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BuildConfig.MODID);
    static Map<String, Item> ITEM_MAP = new HashMap<>(); // sigma strat #1358: new hashmap for one item

    static Item.Properties itemProps() {
        return new Item.Properties();
    }

    static RegistryObject<SoundEvent> addSound(String name) {
        SoundEvent event = new SoundEvent(new ResourceLocation(BuildConfig.MODID, name));
        return SOUND_EVENTS.register(name, () -> event);
    }

    static RegistryObject<Item> addItem(String name, Item item) {
        ITEM_MAP.put(name, item);
        return ITEMS.register(name, () -> item);
    }

    public static RegistryObject<SoundEvent>
            PLAYING_WITH_POWER = addSound("playing_with_power");


    public static RegistryObject<Item>
            PLAYING_WITH_POWER_DISC = addItem("music_disc_playing_with_power", new RecordItem(2, () -> PLAYING_WITH_POWER.get(),
            itemProps().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE)));

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
