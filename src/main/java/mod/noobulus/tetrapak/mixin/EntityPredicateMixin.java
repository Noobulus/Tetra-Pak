package mod.noobulus.tetrapak.mixin;

/*import com.google.gson.JsonElement;
import mod.noobulus.tetrapak.predicate.PredicateManagers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
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
public abstract class EntityPredicateMixin {
	private final List<Predicate<Entity>> customPredicates = new ArrayList<>();

	@Inject(at = @At("RETURN"), method = "fromJson", cancellable = true)
	private static void onFromJson(JsonElement element, CallbackInfoReturnable<EntityPredicate> cir) {
		if (PredicateManagers.ENTITY_PREDICATES.getRegistry() == null)
			return;

		List<Predicate<Entity>> predicateList = PredicateManagers.ENTITY_PREDICATES
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
			predicate = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, PlayerPredicate.ANY, FishingHookPredicate.ANY, LighthingBoltPredicate.ANY, null, null);
			cir.setReturnValue(predicate);
		}

		((EntityPredicateMixin) (Object) predicate).bindPredicateList(predicateList);
	}

	@Inject(at = @At("RETURN"), method = "matches(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/Entity;)Z", cancellable = true)
	private void onPredicateTest(ServerLevel level, Vec3 vector3d, Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (customPredicates.isEmpty() || !cir.getReturnValueZ())
			return;
		cir.setReturnValue(customPredicates.stream().allMatch(entityPredicate -> entityPredicate.test(entity)));
	}

	public void bindPredicateList(List<Predicate<Entity>> list) {
		customPredicates.addAll(list);
	}
}
*/