package mod.noobulus.tetrapak.mixin;

import com.google.gson.JsonElement;
import mod.noobulus.tetrapak.predicate.entity.EntityPredicateManager;
import net.minecraft.advancements.criterion.*;
import net.minecraft.entity.Entity;
import net.minecraft.loot.FishingPredicate;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(EntityPredicate.class)
public class EntityPredicateMixin {
	private final List<Predicate<Entity>> customPredicates = new ArrayList<>();

	@Inject(at = @At("RETURN"), method = "fromJson", cancellable = true)
	private static void onFromJson(JsonElement element, CallbackInfoReturnable<EntityPredicate> cir) {
		if (EntityPredicateManager.INSTANCE.getRegistry() == null)
			return;

		List<Predicate<Entity>> predicateList = EntityPredicateManager.INSTANCE
			.getRegistry()
			.getValues()
			.stream()
			.map(abstractEntityPredicate -> abstractEntityPredicate.read(element))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		if (predicateList.isEmpty())
			return;

		EntityPredicate predicate = cir.getReturnValue();
		if (predicate == EntityPredicate.ANY) {
			predicate = new EntityPredicate(EntityTypePredicate.ANY,
				DistancePredicate.ANY,
				LocationPredicate.ANY,
				MobEffectsPredicate.ANY,
				NBTPredicate.ANY,
				EntityFlagsPredicate.ANY,
				EntityEquipmentPredicate.ANY,
				PlayerPredicate.ANY,
				FishingPredicate.ANY,
				null,
				null);
			cir.setReturnValue(predicate);
		}

		((EntityPredicateMixin) (Object) predicate).bindPredicateList(predicateList);
	}

	@Inject(at = @At("RETURN"), method = "matches(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/entity/Entity;)Z", cancellable = true)
	private void onPredicateTest(ServerWorld level, Vector3d vector3d, Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (customPredicates.isEmpty() || !cir.getReturnValueZ())
			return;
		cir.setReturnValue(customPredicates.stream().allMatch(entityPredicate -> entityPredicate.test(entity)));
	}

	public void bindPredicateList(List<Predicate<Entity>> list) {
		customPredicates.addAll(list);
	}
}
