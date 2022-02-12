package mod.noobulus.tetrapak.mixin;

import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity;
import mod.noobulus.tetrapak.effects.create.recipes.SalvagingRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DeployerTileEntity.class)
public class DeployerTileEntityMixin {
	private final DeployerTileEntity self = (DeployerTileEntity) (Object) this;
	public List<Item> lastProduced = new ArrayList<>();
	@Shadow(remap = false)
	protected DeployerFakePlayer player;

	@Inject(at = @At("HEAD"), method = "getRecipe", remap = false, cancellable = true)
	private void onGetRecipe(ItemStack stack, CallbackInfoReturnable<Recipe<?>> cir) {
		Level level = self.getLevel();
		if (player == null || level == null || lastProduced.contains(stack.getItem()))
			return;
		ItemStack heldItemMainhand = player.getMainHandItem();
		SalvagingRecipe.DeployerAwareInventory recipeInv = new SalvagingRecipe.DeployerAwareInventory(new ItemStackHandler(2), self, player, list -> lastProduced = list);
		recipeInv.setItem(0, heldItemMainhand);
		recipeInv.setItem(1, stack);
		Optional<SalvagingRecipe> recipe = level.getRecipeManager().getRecipeFor(SalvagingRecipe.SalvagingRecipeType.AUTOMATIC_SALVAGING, recipeInv, level);
		if (!recipe.isPresent())
			return;
		recipe.get().setBufferedInv(recipeInv);
		recipe.ifPresent(cir::setReturnValue);
	}
}
