package mod.noobulus.tetrapak.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

public class VoidingLootModifier extends LootModifier {
	public VoidingLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	public static final Codec<VoidingLootModifier> CODEC = RecordCodecBuilder.create(instance ->
		codecStart(instance).apply(instance, VoidingLootModifier::new)
	);

	@Nonnull
	@Override
	public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.clear();
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return null;
	}
}
