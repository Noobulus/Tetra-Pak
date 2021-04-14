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
		this.drops = NonNullList.from(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
		this.fallTile = state;
		this.preventEntitySpawning = true;
		this.setPosition(pos.getX() + .5, pos.getY() + (1 - getHeight()) / 2., pos.getZ() + .5);
		this.setMotion(Vector3d.ZERO);
		this.prevPosX = pos.getX() + .5;
		this.prevPosY = pos.getY();
		this.prevPosZ = pos.getZ() + .5;
		this.setOrigin(getBlockPos());
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
	protected void readAdditional(@Nullable CompoundNBT nbt) {
		if (nbt == null)
			return;
		super.readAdditional(nbt);
		drops = NonNullList.withSize(nbt.getList("Items", 10).size(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt, drops);
		ItemStackHelper.loadAllItems(nbt, drops);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		CompoundNBT compound = new CompoundNBT();
		writeAdditional(compound);
		buffer.writeCompoundTag(compound);
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		readAdditional(additionalData.readCompoundTag());
	}
}
