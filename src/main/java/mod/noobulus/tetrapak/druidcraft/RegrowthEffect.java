package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.items.modular.ModularItem;

import static java.lang.Math.round;

public class RegrowthEffect implements IHoloDescription, IEventBusListener {

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		Level world = entity.getCommandSenderWorld();
		if (!(entity instanceof Player))
			return;

		for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
			ItemStack stack = entity.getItemBySlot(slot);
			if (!(stack.getItem() instanceof ModularItem))
				continue;
			ModularItem item = (ModularItem) stack.getItem();
			if (stack.isDamaged() && hasEffect(stack) && world.getGameTime() % item.getEffectEfficiency(stack, getEffect()) == 0) {
				stack.setDamageValue(stack.getDamageValue() - (stack.getMaxDamage() / 100) - 1);
			}
		}

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiStatBar getStatBar() {
		final IStatGetter regrowthGetter = new StatGetterEffectEfficiency(getEffect(), 0.05);
		final IStatGetter regrowthTotalGetter = new StatGetterEffectEfficiency(getEffect(), 0.08333);
		return new GuiStatBar(0, 0, 59, getStatsPath(),
			0, 60, false, regrowthGetter, LabelGetterBasic.integerLabel,
			(player, itemStack) -> I18n.get(getTooltipPath(),
				regrowthGetter.getValue(player, itemStack), round((float) regrowthTotalGetter.getValue(player, itemStack))));
	}

	@Override
	public ItemEffect getEffect() {
		return ITetraEffect.get("regrowth");
	}
}
