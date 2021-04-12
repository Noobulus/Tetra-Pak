package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.IClientInit;
import mod.noobulus.tetrapak.util.ILootModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.statbar.getter.TooltipGetterInteger;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class ScorchingEffect implements IClientInit, ILootModifier<ScorchingLootModifier> {
	public static final ItemEffect SCORCHING_EFFECT = ItemEffect.get("tetrapak:scorching");

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientInit() {
		final IStatGetter scorchingGetter = new StatGetterEffectLevel(SCORCHING_EFFECT, 1, 0);
		final GuiStatBar scorchingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.scorching",
			0, 1, false, scorchingGetter, LabelGetterBasic.integerLabel,
			new TooltipGetterInteger("tetrapak.stats.scorching.tooltip", scorchingGetter));

		WorkbenchStatsGui.addBar(scorchingBar);
		HoloStatsGui.addBar(scorchingBar);
	}

	@Override
	public GlobalLootModifierSerializer<ScorchingLootModifier> getModifier() {
		return new ScorchingLootModifier.Serializer().setRegistryName(new ResourceLocation("tetrapak", "scorchingitems"));
	}
}