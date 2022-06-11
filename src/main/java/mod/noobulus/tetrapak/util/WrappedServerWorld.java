package mod.noobulus.tetrapak.util;


import mod.noobulus.tetrapak.mixin.accessor.MinecraftServerAccessor;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WrappedServerWorld extends ServerLevel {
	protected final Level world;

	public WrappedServerWorld(Level world) {
		super(world.getServer(), Util.backgroundExecutor(),
				getLevelSaveFromWorld(world),
				(ServerLevelData) world.getLevelData(),
				world.dimension(),
				world.dimensionTypeRegistration(),
				getChunkProgressListener(world),
				((ServerChunkCache) world.getChunkSource()).getGenerator(),
				world.isDebug(),
				world.getBiomeManager().biomeZoomSeed,
				Collections.emptyList(),
				false);
		this.world = world;
	}

	@Nullable
	private static ChunkProgressListener getChunkProgressListener(Level level) {
		// if level is not Serverlevel, raise an error
		if (!(level instanceof ServerLevel serverLevel)) {
			throw new IllegalArgumentException("level is not ServerLevel");
		}

		ChunkMap chunkMap = serverLevel.getChunkSource().chunkMap;

		// reflectively get the ChunkProgressListener
		return ObfuscationReflectionHelper.getPrivateValue(ChunkMap.class, chunkMap, "progressListener");
	}

	private static LevelStorageSource.LevelStorageAccess getLevelSaveFromWorld(Level world) {
		return ((MinecraftServerAccessor) world.getServer()).getStorageSource();
	}

	public static Level unwrap(Level world) {
		return world instanceof WrappedServerWorld wrappedServerWorld ? wrappedServerWorld.world : world;
	}

	@Override
	public float getSunAngle(float p_72826_1_) {
		return 0.0F;
	}

	@Override
	public int getMaxLocalRawBrightness(BlockPos pos) {
		return 15;
	}

	@Override
	public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		this.world.sendBlockUpdated(pos, oldState, newState, flags);
	}

	@Override
	public LevelTicks<Block> getBlockTicks() {
		LevelTickAccess<Block> tl = this.world.getBlockTicks();
		return tl instanceof LevelTicks ? (LevelTicks<Block>) tl : super.getBlockTicks();
	}

	@Override
	public LevelTicks<Fluid> getFluidTicks() {
		LevelTickAccess<Fluid> tl = this.world.getFluidTicks();
		return tl instanceof LevelTicks ? (LevelTicks<Fluid>) tl : super.getFluidTicks();
	}

	@Override
	public void levelEvent(Player player, int type, BlockPos pos, int data) {
	}

	@Override
	public List<ServerPlayer> players() {
		return Collections.emptyList();
	}

	@Override
	public void playSound(Player player, double x, double y, double z, SoundEvent soundIn, SoundSource category, float volume, float pitch) {
	}

	@Override
	public void playSound(Player p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundSource p_217384_4_, float p_217384_5_, float p_217384_6_) {
	}

	@Override
	public Entity getEntity(int id) {
		return null;
	}

	@Override
	public MapItemSavedData getMapData(String mapName) {
		return null;
	}

	@Override
	public boolean addFreshEntity(Entity entityIn) {
		entityIn.level = this.world;
		return this.world.addFreshEntity(entityIn);
	}

	@Override
	public void setMapData(String p_143305_, MapItemSavedData p_143306_) {
	}

	@Override
	public int getFreeMapId() {
		return 0;
	}

	@Override
	public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.world.getRecipeManager();
	}


	@Override
	public Holder<Biome> getUncachedNoiseBiome(int p_203775_, int p_203776_, int p_203777_) {
		return world.getUncachedNoiseBiome(p_203775_, p_203776_, p_203777_);
	}

	@Override
	public GameRules getGameRules() {
		return world.getGameRules();
	}
}
