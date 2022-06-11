package mod.noobulus.tetrapak.effects.eidolon;

import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import se.mickelus.tetra.effect.ItemEffect;

public class ReapingEffect implements IHoloDescription {
	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("reaping");
	}
}