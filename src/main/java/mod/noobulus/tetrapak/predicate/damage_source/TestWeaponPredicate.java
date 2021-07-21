package mod.noobulus.tetrapak.predicate.damage_source;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TestWeaponPredicate extends AbstractDamageSourcePredicate {
	@Nullable
	@Override
	protected Predicate<DamageSource> readInternal(JsonObject data) throws JsonSyntaxException {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(data);
		return damageSource -> itemPredicate.matches(ItemHelper.getItemOfDamgeSource(damageSource));
	}
}
