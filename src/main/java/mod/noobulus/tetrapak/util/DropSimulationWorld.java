package mod.noobulus.tetrapak.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DropSimulationWorld extends WrappedServerWorld {
	private final List<ItemStack> items = new ArrayList<>();

	private DropSimulationWorld(Level world) {
		super(world);
		restoringBlockSnapshots = world.restoringBlockSnapshots;
	}

	public static Level of(Level world) {
		return world instanceof ServerLevel ? new DropSimulationWorld(world) : world;
	}

	public Collection<ItemStack> getItems() {
		Collection<ItemStack> out = new ArrayList<>(items);
		items.clear();
		return out;
	}

	@Override
	public boolean addFreshEntity(Entity entityIn) {
		if (entityIn instanceof ItemEntity)
			items.add(((ItemEntity) entityIn).getItem());
		return false;
	}
}
