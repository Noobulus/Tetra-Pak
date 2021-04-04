package mod.noobulus.tetrapak;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.IStatGetter;
import se.mickelus.tetra.gui.statbar.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.statbar.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.statbar.getter.TooltipGetterPercentageDecimal;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;

import java.util.UUID;

public class NullifyingEffect {
	public static final AttributeModifier beltGravityModifier = new AttributeModifier(UUID.fromString("678c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.25 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final AttributeModifier beltDoubleGravityModifier = new AttributeModifier(UUID.fromString("778c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", 0.125 - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	private static final ItemEffect nullifying = ItemEffect.get("tetrapak:nullifying");

	@OnlyIn(Dist.CLIENT)
	public static void clientInit() {
		final IStatGetter nullifyingGetter = new StatGetterEffectLevel(nullifying, 12.5, 62.5);
		final GuiStatBar nullifyingBar = new GuiStatBar(0, 0, 59, "tetrapak.stats.nullifying",
			0D, 100D, false, nullifyingGetter, LabelGetterBasic.percentageLabelDecimal,
			new TooltipGetterPercentageDecimal("tetrapak.stats.nullifying.tooltip", nullifyingGetter));

		WorkbenchStatsGui.addBar(nullifyingBar);
		HoloStatsGui.addBar(nullifyingBar);
	}

	public static int getNullifierLevel(ItemStack itemStack) {
		if (!itemStack.isEmpty() && itemStack.getItem() instanceof ModularItem) {
			ModularItem item = (ModularItem) itemStack.getItem();
			return item.getEffectLevel(itemStack, nullifying);
		} else {
			return 0;
		}
	}

	private static int getNullifierLevel(LivingEntity e) {
		if (!(e instanceof PlayerEntity))
			return -1;
		return getNullifierLevel(ToolbeltHelper.findToolbelt((PlayerEntity) e));
	}

	@SubscribeEvent
	public static void nullifierAtrributeModifiers(LivingEvent.LivingUpdateEvent event) {
		ModifiableAttributeInstance gravityAttribute = event.getEntityLiving().getAttribute(ForgeMod.ENTITY_GRAVITY.get());
		int nullifierLevel = getNullifierLevel(event.getEntityLiving());

		if (nullifierLevel == -1 || gravityAttribute == null)
			return;

		updateEffect(nullifierLevel == 1, gravityAttribute, beltGravityModifier);
		updateEffect(nullifierLevel == 2, gravityAttribute, beltDoubleGravityModifier);
	}

	@SubscribeEvent
	public static void nullifyingRemovesFallDamage(LivingAttackEvent event) {
		if (event.getSource().damageType.equals(DamageSource.FALL.damageType) && getNullifierLevel(event.getEntityLiving()) > 0) {
			event.setCanceled(true);
		}
	}

	private static void updateEffect(boolean active, ModifiableAttributeInstance attributeInstance, AttributeModifier modifier) {
		if (active) {
			if (!attributeInstance.hasModifier(modifier))
				attributeInstance.applyNonPersistentModifier(modifier);
		} else if (attributeInstance.hasModifier(modifier)) {
			attributeInstance.removeModifier(modifier);
		}
	}
}
