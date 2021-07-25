package mod.noobulus.tetrapak.util;

import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;

public class RecalculatableLazyValue<T> {
	private NonNullSupplier<T> supplier;
	@Nullable
	private T value;

	public RecalculatableLazyValue(NonNullSupplier<T> supplier) {
		this.supplier = supplier;
	}

	public T get() {
		if (value == null)
			value = supplier.get();
		return value;
	}

	public void recalculate() {
		value = null;
	}

	public void updateSupplier(NonNullSupplier<T> supplier) {
		this.supplier = supplier;
		recalculate();
	}
}
