package mod.noobulus.tetrapak.jei;

import mezz.jei.api.gui.ingredient.ITooltipCallback;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JeiTooltipEntry<T> implements ITooltipCallback<T> {
	private final Map<T, ? extends Supplier<? extends Component>> toolTipMap;

	public JeiTooltipEntry(Map<T, ? extends Supplier<? extends Component>> toolTipMap) {
		this.toolTipMap = toolTipMap;
	}

	@Override
	public void onTooltip(int i, boolean b, T t, List<Component> list) {
		if (toolTipMap.containsKey(t))
			list.add(toolTipMap.get(t).get());
	}
}
