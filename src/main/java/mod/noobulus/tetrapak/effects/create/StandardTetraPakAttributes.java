package mod.noobulus.tetrapak.effects.create;

import com.simibubi.create.content.logistics.item.filter.ItemAttribute;
import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.IModularItem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum StandardTetraPakAttributes implements ItemAttribute {
	DUMMY(s -> false),
	honeable(s -> Optional.ofNullable(s.getTag())
		.map(tag -> tag.contains("honing_available"))
		.orElse(false)),
	broken(s -> s.getItem() instanceof IModularItem && ((IModularItem) s.getItem()).isBroken(s));

	private final Predicate<ItemStack> test;

	StandardTetraPakAttributes(Predicate<ItemStack> test) {
		this.test = test;
	}

	public static Object register() {
		ItemAttribute.register(DUMMY);
		return null;
	}

	@Override
	public boolean appliesTo(ItemStack itemStack) {
		return test.test(itemStack);
	}

	@Override
	public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {
		return Arrays.stream(StandardTetraPakAttributes.values())
			.filter(standardAttribute -> standardAttribute.appliesTo(itemStack))
			.collect(Collectors.toList());
	}

	@Override
	public String getTranslationKey() {
		return BuildConfig.MODID + "." + this.name();
	}


	@Override
	public String getNBTKey() {
		return "tetrapak_standard_trait";
	}

	@Override
	public void writeNBT(CompoundTag nbt) {
		nbt.putBoolean(this.name(), true);
	}

	@Override
	public ItemAttribute readNBT(CompoundTag nbt) {
		for (StandardTetraPakAttributes trait : values())
			if (nbt.contains(trait.name()))
				return trait;
		return null;
	}
}
