package mod.noobulus.tetrapak.loot;

import com.google.gson.JsonObject;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Function;

public class GenericModifierSerializer<T extends LootModifier> extends GlobalLootModifierSerializer<T> {
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
