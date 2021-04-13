package mod.noobulus.tetrapak.loot;

import com.google.gson.JsonObject;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Function;

public class GenericModifierSerializer<T extends LootModifier> extends GlobalLootModifierSerializer<T> {
	private final Function<ILootCondition[], T> lootModifier;

	public GenericModifierSerializer(Function<ILootCondition[], T> lootModifier) {
		this.lootModifier = lootModifier;
	}

	@Override
	public T read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
		return lootModifier.apply(ailootcondition);
	}

	@Override
	public JsonObject write(T instance) {
		return new JsonObject();
	}
}
