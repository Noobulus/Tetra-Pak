package mod.noobulus.tetrapak.registries;

import com.mojang.serialization.Codec;
import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.loot.modifier.ReapingLootModifier;
import mod.noobulus.tetrapak.loot.modifier.ScorchingLootModifier;
import mod.noobulus.tetrapak.loot.modifier.VoidingLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GlobalLootModifiers {
	// Deferred registry of loot modifiers
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BuildConfig.MODID);
	@SuppressWarnings("unused")
	public static final RegistryObject<Codec<? extends IGlobalLootModifier>>
			REAPING_MODIFIER = LOOT_MODIFIERS.register("reaping", () -> ReapingLootModifier.CODEC),
			SCORCHING_MODIFIER = LOOT_MODIFIERS.register("scorching", () -> ScorchingLootModifier.CODEC),
			VOIDING_MODIFIER = LOOT_MODIFIERS.register("voiding", () -> VoidingLootModifier.CODEC);

	// prevent instantiation
	private GlobalLootModifiers() {
	}

	// register loot modifiers to event bus
	static  {
		LOOT_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
