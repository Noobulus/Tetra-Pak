package mod.noobulus.tetrapak.jei;

import net.minecraft.util.text.StringTextComponent;

public interface IToolTip {
	default StringTextComponent getEntry() {
		return new StringTextComponent(this.toString());
	}
}
