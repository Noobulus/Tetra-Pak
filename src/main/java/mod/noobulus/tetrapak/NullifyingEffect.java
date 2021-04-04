package mod.noobulus.tetrapak;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.LazyValue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.statbar.GuiStatBar;
import se.mickelus.tetra.gui.statbar.getter.*;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;

import java.util.UUID;

public class NullifyingEffect {
    private static final ItemEffect nullifying = ItemEffect.get("tetrapak:nullifying");

    public static final AttributeModifier beltGravityModifier = new AttributeModifier(UUID.fromString("678c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", -0.06, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier beltDoubleGravityModifier = new AttributeModifier(UUID.fromString("778c7388-ba1d-45c8-9f51-d6e4f1c4e3ac"), "Gravity modifier", -0.07, AttributeModifier.Operation.ADDITION);

    private static int lastNulliferLevel = 0;

    static LazyValue<Multimap<Attribute, AttributeModifier>> gravityModifier =
            new LazyValue<>(() ->
                    // Entity has a belt with one nullifier
                    ImmutableMultimap.of(ForgeMod.ENTITY_GRAVITY.get(), beltGravityModifier)
            );

    static LazyValue<Multimap<Attribute, AttributeModifier>> doubleGravityModifier =
            new LazyValue<>(() ->
                    // Entity has a belt with two nullifiers
                    ImmutableMultimap.of(ForgeMod.ENTITY_GRAVITY.get(), beltDoubleGravityModifier)
            );

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
            ModularItem item = (ModularItem)itemStack.getItem();
            return item.getEffectLevel(itemStack, nullifying);
        } else {
            return 0;
        }
    }

    @SubscribeEvent
    public static void nullifierAtrributeModifiers(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        ItemStack itemStack = ToolbeltHelper.findToolbelt(player);
        int nullifierLevel = getNullifierLevel(itemStack);

        if (nullifierLevel != lastNulliferLevel) {
            player.getAttributeManager().removeModifiers(gravityModifier.getValue());
            player.getAttributeManager().removeModifiers(doubleGravityModifier.getValue());
            if (nullifierLevel == 1) {
                player.getAttributeManager().reapplyModifiers(gravityModifier.getValue());
            } else if (nullifierLevel == 2) {
                player.getAttributeManager().reapplyModifiers(doubleGravityModifier.getValue());
            }
        }
    }

    @SubscribeEvent
    public static void nullifyingRemovesFallDamage(LivingAttackEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if (event.getSource().damageType.equals(DamageSource.FALL.damageType)) {
            ItemStack itemStack = ToolbeltHelper.findToolbelt(player);
            int nullifierLevel = getNullifierLevel(itemStack);

            if (nullifierLevel > 0) { // cancel damage with a nullifier equipped
                event.setCanceled(true);
            }
        }
    }
}
