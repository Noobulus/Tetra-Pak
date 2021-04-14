package mod.noobulus.tetrapak.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DropSimulationWorld extends WrappedServerWorld {
	private final List<ItemStack> items = new ArrayList<>();

	private DropSimulationWorld(World world) {
		super(world);
		restoringBlockSnapshots = world.restoringBlockSnapshots;
	}

	public static World of(World world) {
		return world instanceof ServerWorld ? new DropSimulationWorld(world) : world;
	}

	public Collection<ItemStack> getItems() {
		Collection<ItemStack> out = new ArrayList<>(items);
		items.clear();
		return out;
	}

	@Override
	public boolean addEntity(Entity entityIn) {
		if (entityIn instanceof ItemEntity)
			items.add(((ItemEntity) entityIn).getItem());
		return false;
	}
}
