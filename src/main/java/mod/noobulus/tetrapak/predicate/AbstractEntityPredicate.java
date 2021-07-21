package mod.noobulus.tetrapak.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public abstract class AbstractEntityPredicate extends ForgeRegistryEntry<AbstractEntityPredicate> {
	public static final Predicate<Entity> ANY = entity -> true;

	protected abstract Predicate<Entity> readInternal(JsonObject data) throws JsonSyntaxException;

	public Predicate<Entity> read(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull())
			return ANY;
		JsonObject jsonobject = JSONUtils.convertToJsonObject(element, "entity");
		if (getRegistryName() != null && jsonobject.has(getRegistryName().toString())) {
			JsonElement child = jsonobject.get(getRegistryName().toString());
			if (child == null || child.isJsonNull())
				return ANY;
			return readInternal(JSONUtils.convertToJsonObject(child, getRegistryName().toString()));
		}
		return ANY;
	}
}
