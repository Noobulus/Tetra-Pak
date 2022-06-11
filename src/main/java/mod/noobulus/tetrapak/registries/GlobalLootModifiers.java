package mod.noobulus.tetrapak.registries;

import com.google.gson.JsonObject;
import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.loot.modifier.ReapingLootModifier;
import mod.noobulus.tetrapak.loot.modifier.ScorchingLootModifier;
import mod.noobulus.tetrapak.loot.modifier.VoidingLootModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;


@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GlobalLootModifiers {
	// Deferred registry of loot modifiers
	public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, BuildConfig.MODID);
	@SuppressWarnings("unused")
	public static final RegistryObject<GlobalLootModifierSerializer<?>>
			REAPING_MODIFIER = LOOT_MODIFIERS.register("reaping", () -> new GenericModifierSerializer<>(ReapingLootModifier::new)),
			SCORCHING_MODIFIER = LOOT_MODIFIERS.register("scorching", () -> new GenericModifierSerializer<>(ScorchingLootModifier::new)),
			VOIDING_MODIFIER = LOOT_MODIFIERS.register("voiding", () -> new GenericModifierSerializer<>(VoidingLootModifier::new));


	// prevent instantiation
	private GlobalLootModifiers() {
	}

	// register loot modifiers to event bus
	static  {
		LOOT_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public static class GenericModifierSerializer<T extends LootModifier> extends GlobalLootModifierSerializer<T> {
		private final Function<LootItemCondition[], T> lootModifier;

		public GenericModifierSerializer(Function<LootItemCondition[], T> lootModifier) {
			this.lootModifier = lootModifier;
		}

		@Override
		public T read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
			return lootModifier.apply(ailootcondition);
		}

		@Override
		public JsonObject write(T instance) {
			return new JsonObject();
		}
	}
}
