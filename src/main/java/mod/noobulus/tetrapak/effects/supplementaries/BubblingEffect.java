package mod.noobulus.tetrapak.effects.supplementaries;

import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;;
import se.mickelus.tetra.effect.ItemEffect;

public class BubblingEffect implements ITetraEffect {
	// shell effect for tetra's sake

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("bubbling");
	}
}
