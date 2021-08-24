package mod.noobulus.tetrapak.create.recipes;

import com.simibubi.create.content.contraptions.components.deployer.DeployerRecipeSearchEvent;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.util.IEventBusListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class AutoSalvageManager implements IEventBusListener {
	LazyValue<Method> behaviourAttachmentMethod = new LazyValue<>(() -> {
		try {
			return SmartTileEntity.class.getDeclaredMethod("attachBehaviourLate", TileEntityBehaviour.class);
		} catch (NoSuchMethodException e) {
			TetraPak.LOGGER.error("Error finding attachBehaviourLate method: {}", e.getMessage());
			return null;
		}
	});

	@SubscribeEvent
	public void onDeployerRecipeCollection(DeployerRecipeSearchEvent event) {
		event.addRecipe(() -> {
			RecipeWrapper inv = event.getInventory();

			// inventory has an unexpected size?
			if (inv.getContainerSize() != 2)
				return Optional.empty();

			ItemStack beltStack = inv.getItem(0);
			ItemStack tool = inv.getItem(1);
			DeployerTileEntity deployer = event.getTileEntity();
			World level = deployer.getLevel();

			if (level == null)
				return Optional.empty();


			@Nullable
			DeployerStoresLastRecipeOutputBehaviour store = deployer.getBehaviour(DeployerStoresLastRecipeOutputBehaviour.TYPE);

			// update the behaviours, should be run only once per deployer
			if (store == null) {
				store = new DeployerStoresLastRecipeOutputBehaviour(deployer);
				Method attach = behaviourAttachmentMethod.get();
				if (attach != null) {
					attach.setAccessible(true);
					try {
						attach.invoke(deployer, store);
					} catch (IllegalAccessException | InvocationTargetException e) {
						TetraPak.LOGGER.error("Error calling attachBehaviourLate method: {}", e.getMessage());
					}
				}
			}

			// don't process items that were just produced
			if (store.lastProduced.contains(beltStack.getItem()))
				return Optional.empty();

			Optional<SalvagingRecipe> recipe = level.getRecipeManager().getRecipeFor(SalvagingRecipe.SalvagingRecipeType.AUTOMATIC_SALVAGING, event.getInventory(), level);
			recipe.ifPresent(r -> r.setBufferedDeployer(deployer));
			recipe.ifPresent(r -> r.setBufferedTool(tool));
			return recipe;
		}, 42);
	}
}
