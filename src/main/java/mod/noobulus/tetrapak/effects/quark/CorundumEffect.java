package mod.noobulus.tetrapak.effects.quark;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.Config;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.getter.*;

public class CorundumEffect implements IHoloDescription, IEventBusListener {
	public CorundumEffect() {
		CorundumMap.checkMappings();
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("rainbow");
	}

	@SubscribeEvent
	public void corundumToolsBreakBlocksFaster(PlayerEvent.BreakSpeed event) {
		ItemStack heldItemMainhand = event.getPlayer().getMainHandItem();
		int effectLevel = getEffectLevel(heldItemMainhand);
		if (effectLevel <= 0)
			return;
		float efficiency = (float) getEffectEfficiency(heldItemMainhand) / 100F;
		BlockPos pos = event.getPos();
		BlockGetter world = event.getEntityLiving().level;
		boolean matches = CorundumMap.COLOR_MAP.get(world.getBlockState(event.getPos()).getMapColor(world, pos)) == effectLevel;
		event.setNewSpeed(event.getOriginalSpeed() * (matches ? 1 + efficiency : 1));
		//event.setNewSpeed((float) (event.getOriginalSpeed() * (matches ? Config.MATCHING_CRYSTAL_FACTOR.get() : Config.NON_MATCHING_CRYSTAL_FACTOR.get())));
	}

	@Override
	public ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
		return (player, itemStack) -> I18n.get(getTooltipPath(),
			new TranslatableComponent(BuildConfig.MODID + "." + CorundumMap.NAME_MAP.get(
				(int) statGetter.getValue(player, itemStack))).getString(),
				new StatGetterEffectEfficiency(getEffect(), 1).getValue(player, itemStack));
	}

	@Override
	public ILabelGetter getStatLabel() { // todo: work out how to make this capitalize the color in the tooltip
		return new LabelGetterBasic("%s", "%s") {
			@Override
			public String getLabel(double value, double diffValue, boolean flipped) {
				return new TranslatableComponent(BuildConfig.MODID + "." + CorundumMap.NAME_MAP.get((int) diffValue)).getString();
			}
		};
	}
}
