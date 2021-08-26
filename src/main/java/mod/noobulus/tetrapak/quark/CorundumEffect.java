package mod.noobulus.tetrapak.quark;

import mod.noobulus.tetrapak.Config;
import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.ITooltipGetter;

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
		int effectLevel = getEffectLevel(event.getPlayer().getMainHandItem());
		if (effectLevel <= 0)
			return;
		BlockPos pos = event.getPos();
		IBlockReader world = event.getEntityLiving().level;
		boolean matches = CorundumMap.COLOR_MAP.get(world.getBlockState(event.getPos()).getMapColor(world, pos)) == effectLevel;
		event.setNewSpeed((float) (event.getOriginalSpeed() * (matches ? Config.MATCHING_CRYSTAL_FACTOR.get() : Config.NON_MATCHING_CRYSTAL_FACTOR.get())));
	}

	@Override
	public ITooltipGetter getStatTooltipGetter(IStatGetter statGetter) {
		return (player, itemStack) -> I18n.get(getTooltipPath(),
				CorundumMap.NAME_MAP.get((int) statGetter.getValue(player, itemStack)), Config.MATCHING_CRYSTAL_FACTOR.get());
	}
}
