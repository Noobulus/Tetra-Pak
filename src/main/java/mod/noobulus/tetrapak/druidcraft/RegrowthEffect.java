package mod.noobulus.tetrapak.druidcraft;

import mod.noobulus.tetrapak.util.IEventBusListener;
import mod.noobulus.tetrapak.util.tetra_definitions.IHoloDescription;
import mod.noobulus.tetrapak.util.tetra_definitions.ITetraEffect;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectEfficiency;
import se.mickelus.tetra.items.modular.ModularItem;

import static java.lang.Math.round;

public class RegrowthEffect implements IHoloDescription, IEventBusListener {

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.getCommandSenderWorld();
		if (!(entity instanceof PlayerEntity))
			return;

		for (EquipmentSlotType slot : new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND}) {
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
