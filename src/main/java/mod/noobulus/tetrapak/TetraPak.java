package mod.noobulus.tetrapak;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("tetrapak")
public class TetraPak {
    public TetraPak() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(DeforestingEffect.class);
        MinecraftForge.EVENT_BUS.register(VoidingEffect.class);
        MinecraftForge.EVENT_BUS.register(NullifyingEffect.class);
        MinecraftForge.EVENT_BUS.register(MoonstrikeEffect.class);

        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        modEventBus.addGenericListener(GlobalLootModifierSerializer.class, TetraPak::registerLootModifiers);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    // loot modifier tomfoolery
    public static void registerLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        GlobalLootModifierSerializer<ShadowSteelVoidingModifier> voiding = new ShadowSteelVoidingModifier.Serializer();
        voiding.setRegistryName(new ResourceLocation("tetrapak", "voiding"));
        event.getRegistry().register(voiding);
    }

    @SubscribeEvent
    public void setup(FMLClientSetupEvent event) {
        VoidingEffect.clientInit();
        DeforestingEffect.clientInit();
        NullifyingEffect.clientInit();
        MoonstrikeEffect.clientInit();
    }
}