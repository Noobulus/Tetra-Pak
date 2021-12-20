package mod.noobulus.tetrapak.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public abstract class AbstractPredicate<T, R extends AbstractPredicate<T, R>> extends ForgeRegistryEntry<R> {

	@Nullable
	protected abstract Predicate<T> readInternal(JsonObject data) throws JsonSyntaxException;

	@Nullable
	public Predicate<T> read(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull())
			return null;
		JsonObject jsonobject = GsonHelper.convertToJsonObject(element, getKey());
		if (getRegistryName() != null && jsonobject.has(getRegistryName().toString())) {
			JsonElement child = jsonobject.get(getRegistryName().toString());
			if (child == null || child.isJsonNull())
				return null;
			return readInternal(GsonHelper.convertToJsonObject(child, getRegistryName().toString()));
		}
		return null;
	}

	protected abstract String getKey();
}
