package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.IHoloDescription;
import mod.noobulus.tetrapak.util.ITetraEffect;
import mod.noobulus.tetrapak.util.ItemHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.items.modular.ModularItem;

import javax.annotation.Nullable;

public class MoonstrikeEffect implements IHoloDescription {

	private static float getMoonFactor(IWorld world, float efficiency) {
		return 1 + (efficiency * world.getMoonSize() / 100.f);
	}

	@SubscribeEvent
	public void moonstrikeToolsBreakBlocksFaster(PlayerEvent.BreakSpeed event) {
		ItemStack heldItemMainhand = event.getPlayer().getHeldItemMainhand();
		if (!(heldItemMainhand.getItem() instanceof ModularItem))
			return;
		ModularItem item = (ModularItem) heldItemMainhand.getItem();
		if (hasEffect(heldItemMainhand)) {
			IWorld moonPhaseWorld = event.getPlayer().getEntityWorld();
			float efficiency = (float) item.getEffectEfficiency(heldItemMainhand, getEffect());
			event.setNewSpeed(event.getOriginalSpeed() * getMoonFactor(moonPhaseWorld, efficiency));
		}
	}

	@SubscribeEvent
	public void moonstrikeCausesBonusDamage(LivingHurtEvent event) {
		if (shouldMoonstrikeAffect(event.getSource())) {
			Entity source = event.getSource().getImmediateSource();
			if (source == null)
				return;
			IWorld moonPhaseWorld = source.getEntityWorld();
			float efficiency = getEffectEfficiency(event.getSource());
			event.setAmount(event.getAmount() * getMoonFactor(moonPhaseWorld, efficiency));
		}
	}

	private boolean shouldMoonstrikeAffect(@Nullable DamageSource source) {
		if (source == null)
			return false;
		if (source.getTrueSource() instanceof LivingEntity) {
			LivingEntity user = (LivingEntity) source.getTrueSource();

			return hasEffect(user.getHeldItemMainhand()) ||
				hasEffect(ItemHelper.getThrownItemStack(source.getImmediateSource()));
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiStatBar getStatBar() {
		final IStatGetter moonstrikeGetter = new StatGetterEffectEfficiency(getEffect(), 1);
		return new GuiStatBar(0, 0, 59, getStatsPath(),
			0D, 100D, false, moonstrikeGetter, LabelGetterBasic.percentageLabelDecimal,
			(player, itemStack) -> I18n.format(getTooltipPath(),
				moonstrikeGetter.getValue(player, itemStack), moonstrikeGetter.getValue(player, itemStack)));
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("moonstrike");
	}
}
