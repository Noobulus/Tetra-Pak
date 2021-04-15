package mod.noobulus.tetrapak.util;


import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerTickList;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WrappedServerWorld extends ServerWorld {
	protected World world;

	public WrappedServerWorld(World world) {
		super(world.getServer(), Util.getServerExecutor(), getLevelSaveFromWorld(world), (IServerWorldInfo) world.getWorldInfo(), world.getRegistryKey(), world.getDimension(), null, ((ServerChunkProvider) world.getChunkProvider()).getChunkGenerator(), world.isDebugWorld(), world.getBiomeAccess().seed, Collections.emptyList(), false);
		this.world = world;
	}

	private static SaveFormat.LevelSave getLevelSaveFromWorld(World world) {
		return ObfuscationReflectionHelper.getPrivateValue(MinecraftServer.class, world.getServer(), "field_71310_m");
	}

	@Override
	public float getCelestialAngleRadians(float p_72826_1_) {
		return 0.0F;
	}

	@Override
	public int getLight(BlockPos pos) {
		return 15;
	}

	@Override
	public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		this.world.notifyBlockUpdate(pos, oldState, newState, flags);
	}

	@Override
	public ServerTickList<Block> getPendingBlockTicks() {
		ITickList<Block> tl = this.world.getPendingBlockTicks();
		return tl instanceof ServerTickList ? (ServerTickList) tl : super.getPendingBlockTicks();
	}

	@Override
	public ServerTickList<Fluid> getPendingFluidTicks() {
		ITickList<Fluid> tl = this.world.getPendingFluidTicks();
		return tl instanceof ServerTickList ? (ServerTickList) tl : super.getPendingFluidTicks();
	}

	@Override
	public void playEvent(PlayerEntity player, int type, BlockPos pos, int data) {
	}

	@Override
	public List<ServerPlayerEntity> getPlayers() {
		return Collections.emptyList();
	}

	@Override
	public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
	}

	@Override
	public void playMovingSound(PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
	}

	@Override
	public Entity getEntityByID(int id) {
		return null;
	}

	@Override
	public MapData getMapData(String mapName) {
		return null;
	}

	@Override
	public boolean addEntity(Entity entityIn) {
		entityIn.setWorld(this.world);
		return this.world.addEntity(entityIn);
	}

	@Override
	public void registerMapData(MapData mapDataIn) {
	}

	@Override
	public int getNextMapId() {
		return 0;
	}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.world.getRecipeManager();
	}

	@Override
	public ITagCollectionSupplier getTags() {
		return this.world.getTags();
	}

	@Override
	public Biome getGeneratorStoredBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
		return this.world.getGeneratorStoredBiome(p_225604_1_, p_225604_2_, p_225604_3_);
	}

	@Override
	public GameRules getGameRules() {
		return world.getGameRules();
	}

	public static World unwrap(World world) {
		return world instanceof WrappedServerWorld ? ((WrappedServerWorld) world).world : world;
	}
}
