package mod.noobulus.tetrapak.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

public class ScorchingLootModifier extends LootModifier {
	public ScorchingLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	public static final Codec<ScorchingLootModifier> CODEC = RecordCodecBuilder.create(instance ->
		codecStart(instance).apply(instance, ScorchingLootModifier::new)
	);

	@Nonnull
	@Override
	public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		return new ObjectArrayList<>(generatedLoot.stream()
				.map(stack -> ItemHelper.smelt(stack, context.getLevel()))
				.toList()
		);
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
