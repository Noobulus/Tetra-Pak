package mod.noobulus.tetrapak.entities;

import mod.noobulus.tetrapak.registries.Entities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FragileFallingBlock extends FallingBlockEntity implements IEntityAdditionalSpawnData {
	private NonNullList<ItemStack> drops;

	public FragileFallingBlock(EntityType<FragileFallingBlock> entityType, Level world) {
		super(entityType, world);
		drops = NonNullList.create();
	}

	public FragileFallingBlock(Level world, BlockPos pos, BlockState state, Collection<ItemStack> drops) {
		super(Entities.FRAGILE_FALLING_BLOCK.get(), world);
		this.drops = NonNullList.of(ItemStack.EMPTY, drops.toArray(new ItemStack[0]));
		this.blockState = state;
		this.blocksBuilding = true;
		this.setPos(pos.getX() + .5, pos.getY() + (1 - getBbHeight()) / 2., pos.getZ() + .5);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = pos.getX() + .5;
		this.yo = pos.getY();
		this.zo = pos.getZ() + .5;
		this.setStartPos(blockPosition());
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
		if (onGround || (time > 100 && (getY() < -63 || getY() > 320)) || this.time > 600) {
			this.dropItems();
			return;
		}
		setDeltaMovement(getDeltaMovement().scale(0.98D));
	}

	private void dropItems() { // TODO: why the hell does this not sync to the client?
		if (!level.isClientSide && this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
			drops.forEach(stack -> Block.popResource(level, blockPosition(), stack));
		this.discard();
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		ContainerHelper.saveAllItems(nbt, drops);
	}

	@Override
	protected void readAdditionalSaveData(@Nullable CompoundTag nbt) {
		if (nbt == null)
			return;
		super.readAdditionalSaveData(nbt);
		drops = NonNullList.withSize(nbt.getList("Items", 10).size(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, drops);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		CompoundTag compound = new CompoundTag();
		addAdditionalSaveData(compound);
		buffer.writeNbt(compound);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		readAdditionalSaveData(additionalData.readNbt());
	}
}
