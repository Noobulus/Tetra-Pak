package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.EffectHelper;
import mod.noobulus.tetrapak.util.IHoloDescription;
import mod.noobulus.tetrapak.util.ILootModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import se.mickelus.tetra.effect.ItemEffect;

public class ScorchingEffect implements IHoloDescription, ILootModifier<ScorchingLootModifier> {

	@Override
	@OnlyIn(Dist.CLIENT)
	public GlobalLootModifierSerializer<ScorchingLootModifier> getModifier() {
		return new ScorchingLootModifier.Serializer().setRegistryName(new ResourceLocation(getEffect().getKey()));
	}

	@Override
	public ItemEffect getEffect() {
		return EffectHelper.get("scorching");
	}
}