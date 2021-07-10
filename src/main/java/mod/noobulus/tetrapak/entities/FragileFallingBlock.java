package mod.noobulus.tetrapak.entities;

import mcp.MethodsReturnNonnullByDefault;
import mod.noobulus.tetrapak.registries.Entities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FragileFallingBlock extends FallingBlockEntity implements IEntityAdditionalSpawnData {
	private NonNullList<ItemStack> drops;

	public FragileFallingBlock(EntityType<FragileFallingBlock> entityType, World world) {
		super(entityType, world);
		drops = NonNullList.create();
	}

	public FragileFallingBlock(World world, BlockPos pos, BlockState state, Collection<ItemStack> drops) {
		super(Entities.FRAGILE_FALLING_BLOCK.get(), world);
		this.drops = NonNullList.of(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
		this.blockState = state;
		this.blocksBuilding = true;
		this.setPos(pos.getX() + .5, pos.getY() + (1 - getBbHeight()) / 2., pos.getZ() + .5);
		this.setDeltaMovement(Vector3d.ZERO);
		this.xo = pos.getX() + .5;
		this.yo = pos.getY();
		this.zo = pos.getZ() + .5;
		this.setStartPos(blockPosition());
		this.drops = NonNullList.of(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void tick() {
		if (!isAlive())
			return;
		if (getBlockState().isAir()) {
			this.dropItems();
			return;
		}

		if (!isNoGravity())
			setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));

		move(MoverType.SELF, getDeltaMovement());
		if (!level.isClientSide && (onGround || (time > 100 && (getY() < 1 || getY() > 256)) || this.time > 600))
			dropItems();

		setDeltaMovement(getDeltaMovement().scale(0.98D));
	}

	private void dropItems() {
		if (!level.isClientSide)
			drops.forEach(stack -> Block.popResource(level, blockPosition(), stack));
		this.remove();
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		ItemStackHelper.saveAllItems(nbt, drops);
	}

	@Override
	protected void readAdditionalSaveData(@Nullable CompoundNBT nbt) {
		if (nbt == null)
			return;
		super.readAdditionalSaveData(nbt);
		drops = NonNullList.withSize(nbt.getList("Items", 10).size(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt, drops);
		ItemStackHelper.loadAllItems(nbt, drops);
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		CompoundNBT compound = new CompoundNBT();
		addAdditionalSaveData(compound);
		buffer.writeNbt(compound);
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		readAdditionalSaveData(additionalData.readNbt());
	}
}
