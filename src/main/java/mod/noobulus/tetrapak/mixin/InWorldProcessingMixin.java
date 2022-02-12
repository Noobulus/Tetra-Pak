package mod.noobulus.tetrapak.mixin;

import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import mod.noobulus.tetrapak.effects.create.recipes.SalvagingRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(InWorldProcessing.class)
public class InWorldProcessingMixin {

	@Inject(at = @At(value = "HEAD", target = "Lcom/simibubi/create/content/contraptions/components/deployer/BeltDeployerCallbacks;activate(Lcom/simibubi/create/content/contraptions/relays/belt/transport/TransportedItemStack;Lcom/simibubi/create/foundation/tileEntity/behaviour/belt/TransportedItemStackHandlerBehaviour;Lcom/simibubi/create/content/contraptions/components/deployer/DeployerTileEntity;Lnet/minecraft/item/crafting/IRecipe;)V"),
		method = "applyRecipeOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;)Ljava/util/List;",
		cancellable = true, remap = false)
	private static void onApply(ItemStack stackIn, Recipe<?> recipe, CallbackInfoReturnable<List<ItemStack>> cir) {
		if (!(recipe instanceof SalvagingRecipe))
			return;
		SalvagingRecipe salvagingRecipe = (SalvagingRecipe) recipe;
		SalvagingRecipe.DeployerAwareInventory inventory = ((SalvagingRecipe) recipe).getRecipeInv();
		if (inventory == null)
			return;
		Level world = inventory.deployerTileEntity.getLevel();
		if (!(world instanceof ServerLevel))
			return;
		Player playerEntity = inventory.deployerFakePlayer;
		List<ItemStack> rolls = salvagingRecipe.rollResults(inventory, ((ServerLevel) world), playerEntity);
		if (!rolls.isEmpty()) {
			inventory.onRecipeApply.accept(rolls.stream().filter(((Predicate<ItemStack>) ItemStack::isEmpty).negate()).map(ItemStack::getItem).collect(Collectors.toList()));
			cir.setReturnValue(rolls);
		}
	}
}
