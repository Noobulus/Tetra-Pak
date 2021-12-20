package mod.noobulus.tetrapak.util.tetra_definitions;

import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;

import javax.annotation.Nullable;

import static mod.noobulus.tetrapak.util.ItemHelper.getItemOfDamgeSource;

public interface ITetraEffect {
	static ItemEffect get(String key) {
		return ItemEffect.get(BuildConfig.MODID + ":" + key);
	}

	ItemEffect getEffect();

	default boolean hasEffect(@Nullable ItemStack stack) {
		return getEffectLevel(stack) > 0;
	}

	default void doBeltTick(Player player, int effectLevel) {
	}

	default void doBeltTick(Player player, @Nullable ItemStack belt) {
		doBeltTick(player, getEffectLevel(belt));
	}

	default int getBeltEffectLevel(Player playerEntity) {
		return getEffectLevel(ToolbeltHelper.findToolbelt((playerEntity)));
	}

	default boolean hasBeltEffect(@Nullable Entity entity) {
		return entity instanceof Player player && getBeltEffectLevel(player) > 0;
	}

	default double getEffectEfficiency(@Nullable DamageSource source) {
		return getEffectEfficiency(getItemOfDamgeSource(source));
	}

	default boolean hasEffect(DamageSource source) {
		return hasEffect(getItemOfDamgeSource(source));
	}

	default double getEffectEfficiency(@Nullable ItemStack test) {
		if (test == null || test.isEmpty() || !(test.getItem() instanceof ModularItem item))
			return 0;
		return item.getEffectEfficiency(test, getEffect());
	}

	default String getStatsPath() {
		return BuildConfig.MODID + ".stats." + new ResourceLocation(getEffect().getKey()).getPath();
	}

	default int getEffectLevel(@Nullable ItemStack test) {
		if (test == null || test.isEmpty() || !(test.getItem() instanceof ModularItem item))
			return 0;
		return item.getEffectLevel(test, getEffect());
	}

	default String getTooltipPath() {
		return getStatsPath() + ".tooltip";
	}
}
