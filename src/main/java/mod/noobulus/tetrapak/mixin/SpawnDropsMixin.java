package mod.noobulus.tetrapak.mixin;

import mod.noobulus.tetrapak.Mods;
import mod.noobulus.tetrapak.create.refined_radiance.FloatingEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class SpawnDropsMixin {
	@Inject(at = @At("HEAD"), method = "dropResources(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V")
	private static void onSpawnDropsPre(BlockState state, World world, BlockPos pos, TileEntity tileEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
		if (!Mods.CREATE.isLoaded)
			return;
		FloatingEffect.INSTANCE.checkFloatiness(stack);
	}

	@Inject(at = @At("RETURN"), method = "dropResources(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V")
	private static void onSpawnDropsEnd(BlockState state, World world, BlockPos pos, TileEntity tileEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
		FloatingEffect.INSTANCE.resetFloatiness();
	}
}
