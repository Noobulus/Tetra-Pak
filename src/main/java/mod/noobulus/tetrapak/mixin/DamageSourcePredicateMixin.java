package mod.noobulus.tetrapak.mixin;

import com.google.gson.JsonElement;
import mod.noobulus.tetrapak.predicate.PredicateManagers;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(DamageSourcePredicate.class)
public class DamageSourcePredicateMixin {
	private final List<Predicate<DamageSource>> customPredicates = new ArrayList<>();

	@Inject(at = @At("RETURN"), method = "fromJson", cancellable = true)
	private static void onFromJson(JsonElement element, CallbackInfoReturnable<DamageSourcePredicate> cir) {
		if (PredicateManagers.DAMAGE_SOURCE_PREDICATES.getRegistry() == null)
			return;

		List<Predicate<DamageSource>> predicateList = PredicateManagers.DAMAGE_SOURCE_PREDICATES
			.getRegistry()
			.getValues()
			.stream()
			.map(abstractEntityPredicate -> abstractEntityPredicate.read(element))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		if (predicateList.isEmpty())
			return;

		DamageSourcePredicate predicate = cir.getReturnValue();
		if (predicate == DamageSourcePredicate.ANY) {
			predicate = DamageSourcePredicate.Builder.damageType().build();
			cir.setReturnValue(predicate);
		}

		((DamageSourcePredicateMixin) (Object) predicate).bindPredicateList(predicateList);
	}

	@Inject(at = @At("RETURN"), method = "matches(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/DamageSource;)Z", cancellable = true)
	private void onPredicateTest(ServerLevel level, Vec3 pos, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		if (customPredicates.isEmpty() || !cir.getReturnValueZ())
			return;
		cir.setReturnValue(customPredicates.stream().allMatch(entityPredicate -> entityPredicate.test(damageSource)));
	}

	public void bindPredicateList(List<Predicate<DamageSource>> list) {
		customPredicates.addAll(list);
	}
}
