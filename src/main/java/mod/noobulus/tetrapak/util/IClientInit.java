package mod.noobulus.tetrapak.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IClientInit {
	@OnlyIn(Dist.CLIENT)
	void clientInit();
}
