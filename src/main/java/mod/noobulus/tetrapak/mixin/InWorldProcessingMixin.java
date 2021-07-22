package mod.noobulus.tetrapak.mixin;

import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import mod.noobulus.tetrapak.create.recipes.SalvagingRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(InWorldProcessing.class)
public class InWorldProcessingMixin {

	@Inject(at = @At(value = "HEAD", target = "Lcom/simibubi/create/content/contraptions/components/deployer/BeltDeployerCallbacks;activate(Lcom/simibubi/create/content/contraptions/relays/belt/transport/TransportedItemStack;Lcom/simibubi/create/foundation/tileEntity/behaviour/belt/TransportedItemStackHandlerBehaviour;Lcom/simibubi/create/content/contraptions/components/deployer/DeployerTileEntity;Lnet/minecraft/item/crafting/IRecipe;)V"),
		method = "applyRecipeOn(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/crafting/IRecipe;)Ljava/util/List;",
		cancellable = true, remap = false)
	private static void onApply(ItemStack stackIn, IRecipe<?> recipe, CallbackInfoReturnable<List<ItemStack>> cir) {
		if (!(recipe instanceof SalvagingRecipe))
			return;
		SalvagingRecipe salvagingRecipe = (SalvagingRecipe) recipe;
		SalvagingRecipe.DeployerAwareInventory inventory = ((SalvagingRecipe) recipe).getRecipeInv();
		if (inventory == null)
			return;
		World world = inventory.deployerTileEntity.getLevel();
		if (!(world instanceof ServerWorld))
			return;
		PlayerEntity playerEntity = inventory.deployerFakePlayer;
		List<ItemStack> rolls = salvagingRecipe.rollResults(inventory, ((ServerWorld) world), playerEntity);
		if (!rolls.isEmpty())
			cir.setReturnValue(rolls);
	}
}
