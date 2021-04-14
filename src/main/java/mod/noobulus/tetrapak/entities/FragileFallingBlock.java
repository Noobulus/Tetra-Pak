package mod.noobulus.tetrapak.entities;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FragileFallingBlock extends FallingBlockEntity {
	private final NonNullList<ItemStack> drops;

	public FragileFallingBlock(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(entityType, world);
		drops = NonNullList.create();
	}

	public FragileFallingBlock(World world, BlockPos pos, BlockState state, Collection<ItemStack> drops) {
		super(world, pos.getX() + .5, pos.getY(), pos.getZ() + .5, state);
		this.drops = NonNullList.from(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void tick() {
		if (getBlockState().isAir()) {
			this.dropItems();
			return;
		}

		if (!hasNoGravity())
			setMotion(getMotion().add(0.0D, -0.04D, 0.0D));

		move(MoverType.SELF, getMotion());
		if (!world.isRemote && (onGround || (fallTime > 100 && (getY() < 1 || getY() > 256)) || this.fallTime > 600))
			dropItems();

		setMotion(getMotion().scale(0.98D));
	}

	private void dropItems() {
		drops.forEach(stack -> Block.spawnAsEntity(world, getBlockPos(), stack));
		this.remove();
	}

	@Override
	protected void writeAdditional(CompoundNBT nbt) {
		super.writeAdditional(nbt);
		ItemStackHelper.saveAllItems(nbt, drops);
	}

	@Override
	protected void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		ItemStackHelper.loadAllItems(nbt, drops);
	}
}
