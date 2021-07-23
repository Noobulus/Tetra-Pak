package mod.noobulus.tetrapak.jei;

import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JeiTooltipEntry<T> implements ITooltipCallback<T> {
	private final Map<T, ? extends Supplier<? extends ITextComponent>> toolTipMap;

	public JeiTooltipEntry(Map<T, ? extends Supplier<? extends ITextComponent>> toolTipMap) {
		this.toolTipMap = toolTipMap;
	}

	@Override
	public void onTooltip(int i, boolean b, T t, List<ITextComponent> list) {
		if (toolTipMap.containsKey(t))
			list.add(toolTipMap.get(t).get());
	}
}
