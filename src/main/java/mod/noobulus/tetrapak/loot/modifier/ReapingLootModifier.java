package mod.noobulus.tetrapak.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class ReapingLootModifier extends LootModifier {
	public ReapingLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	public static final Codec<ReapingLootModifier> CODEC = RecordCodecBuilder.create(instance ->
		codecStart(instance).apply(instance, ReapingLootModifier::new)
	);

	@Nonnull
	@Override
	public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.clear();
		generatedLoot.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("eidolon", "soul_shard")),
			context.getRandom().nextInt(2 + context.getLootingModifier())));
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}