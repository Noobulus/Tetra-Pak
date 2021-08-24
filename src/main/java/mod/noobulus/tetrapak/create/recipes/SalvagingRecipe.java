package mod.noobulus.tetrapak.create.recipes;

import com.google.gson.JsonObject;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;
import mcp.MethodsReturnNonnullByDefault;
import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.util.LootLoader;
import mod.noobulus.tetrapak.util.RecalculatableLazyValue;
import mod.noobulus.tetrapak.util.ToolHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SalvagingRecipe extends ProcessingRecipe<IInventory> {
	public static final ResourceLocation ID = TetraPak.asId("automatic_salvaging");
	public static final IRecipeTypeInfo INFO = new IRecipeTypeInfo() {
		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		public IRecipeSerializer<SalvagingRecipe> getSerializer() {
			return Serializer.INSTANCE;
		}

		@Override
		public IRecipeType<SalvagingRecipe> getType() {
			return SalvagingRecipeType.INSTANCE;
		}
	};
	private static final LootParameterSet lootParameters = new LootParameterSet.Builder()
		.required(LootParameters.ORIGIN)
		.optional(LootParameters.TOOL)
		.optional(LootParameters.THIS_ENTITY)
		.build();
	public final ToolType toolType;
	public final int toolLevel;
	public final Ingredient startingItem;
	public final ResourceLocation lootTable;
	public final RecalculatableLazyValue<List<LootLoader.LootSlot>> contents;
	public final RecalculatableLazyValue<List<ItemStack>> toolExamples;

	@Nullable
	private DeployerTileEntity bufferedDeployerTile = null;
	private ItemStack bufferedToolStack = ItemStack.EMPTY;

	public SalvagingRecipe(ResourceLocation id, ToolType toolType, int toolLevel, Ingredient startingItem, ResourceLocation lootTable) {
		super(INFO, new ProcessingRecipeBuilder.ProcessingRecipeParams(id) {
		});

		this.toolType = toolType;
		this.toolLevel = toolLevel;
		this.startingItem = startingItem;
		this.lootTable = lootTable;
		this.contents = new RecalculatableLazyValue<>(DistExecutor.unsafeRunForDist(() -> () -> () -> {
			if (Minecraft.getInstance().hasSingleplayerServer())
				return getContents();
			return new ArrayList<>();
		}, () -> () -> this::getContents));

		this.toolExamples = new RecalculatableLazyValue<>(() -> ToolHelper.getToolsOf(toolType, toolLevel));
	}

	@Override
	protected int getMaxInputCount() {
		return 2;
	}

	@Override
	protected int getMaxOutputCount() {
		return 20;
	}

	@Override
	public boolean matches(IInventory iInventory, World level) {
		if (iInventory.getContainerSize() != 2)
			return false;
		ItemStack toolSlot = iInventory.getItem(1);
		if (toolSlot.isEmpty())
			return false;
		if (toolSlot.getItem().getHarvestLevel(toolSlot, toolType, null, null) < toolLevel)
			return false;
		return startingItem.test(iInventory.getItem(0));
	}

	public void setBufferedDeployer(DeployerTileEntity recipeInv) {
		this.bufferedDeployerTile = recipeInv;
	}

	public void setBufferedTool(ItemStack tool) {
		this.bufferedToolStack = tool;
	}

	private List<LootLoader.LootSlot> getContents() {
		LootTableManager manager = LootLoader.getManager();
		return LootLoader.crawlTable(manager.get(lootTable), manager);
	}

	@Override
	public List<ItemStack> rollResults() {
		if (bufferedDeployerTile == null)
			return new ArrayList<>();
		World level = bufferedDeployerTile.getLevel();
		if (!(level instanceof ServerWorld))
			return new ArrayList<>();
		if (bufferedToolStack.isEmpty())
			return new ArrayList<>();
		LootContext.Builder builder = new LootContext.Builder(((ServerWorld) level))
			.withParameter(LootParameters.TOOL, bufferedToolStack);
		PlayerEntity player = bufferedDeployerTile.getPlayer();
		if (player != null) {
			builder.withParameter(LootParameters.THIS_ENTITY, player)
				.withParameter(LootParameters.ORIGIN, player.position())
				.withLuck(player.getLuck());
		}

		LootTable table = level.getServer().getLootTables().get(lootTable);
		List<ItemStack> rolls = table.getRandomItems(builder.create(lootParameters));
		if (!rolls.isEmpty()) {
			@Nullable
			DeployerStoresLastRecipeOutputBehaviour store = bufferedDeployerTile.getBehaviour(DeployerStoresLastRecipeOutputBehaviour.TYPE);
			if (store != null)
				store.setLastProduced(rolls
					.stream()
					.filter(((Predicate<ItemStack>) ItemStack::isEmpty).negate())
					.map(ItemStack::getItem)
					.collect(Collectors.toList()));
		}
		return rolls;
	}

	private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
		implements IRecipeSerializer<SalvagingRecipe> {
		public static final IRecipeSerializer<SalvagingRecipe> INSTANCE = new Serializer();

		@Override
		public SalvagingRecipe fromJson(ResourceLocation id, JsonObject json) {
			final Ingredient ingredient = Ingredient.fromJson(json.get("requirement"));
			final int toolLevel = JSONUtils.isValidNode(json, "tool_level") ? JSONUtils.getAsInt(json, "tool_level") : 0;
			final ToolType toolType = ToolType.get(JSONUtils.getAsString(json, "tool_type"));
			final ResourceLocation lootTable = new ResourceLocation(JSONUtils.getAsString(json, "lootTable"));
			return new SalvagingRecipe(id, toolType, toolLevel, ingredient, lootTable);
		}

		@Nullable
		@Override
		public SalvagingRecipe fromNetwork(ResourceLocation id, PacketBuffer packetBuffer) {
			final Ingredient ingredient = Ingredient.fromNetwork(packetBuffer);
			final int toolLevel = packetBuffer.readInt();
			final ResourceLocation lootTable = packetBuffer.readResourceLocation();
			final ToolType toolType = ToolType.get(packetBuffer.readUtf(32767));
			SalvagingRecipe recipe = new SalvagingRecipe(id, toolType, toolLevel, ingredient, lootTable);

			int slotCount = packetBuffer.readInt();
			List<LootLoader.LootSlot> slots = new ArrayList<>();
			for (int i = 0; i < slotCount; i++) {
				slots.add(new LootLoader.LootSlot(packetBuffer));
			}
			recipe.contents.updateSupplier(() -> slots);

			int exampleCount = packetBuffer.readInt();
			List<ItemStack> examples = new ArrayList<>();
			for (int i = 0; i < exampleCount; i++) {
				examples.add(packetBuffer.readItem());
			}
			recipe.toolExamples.updateSupplier(() -> examples);

			return recipe;
		}

		@Override
		public void toNetwork(PacketBuffer packetBuffer, SalvagingRecipe recipe) {
			recipe.startingItem.toNetwork(packetBuffer);
			packetBuffer.writeInt(recipe.toolLevel);
			packetBuffer.writeResourceLocation(recipe.lootTable);
			packetBuffer.writeUtf(recipe.toolType.getName());

			List<LootLoader.LootSlot> slots = recipe.contents.get();
			packetBuffer.writeInt(slots.size());
			slots.forEach(slot -> slot.toBuffer(packetBuffer));

			List<ItemStack> examples = recipe.toolExamples.get();
			packetBuffer.writeInt(examples.size());
			examples.forEach(tool -> packetBuffer.writeItemStack(tool, false));
		}
	}

	public static class SalvagingRecipeType implements IRecipeType<SalvagingRecipe> {
		public static final IRecipeType<SalvagingRecipe> INSTANCE = new SalvagingRecipe.SalvagingRecipeType();

		private SalvagingRecipeType() {
		}

		public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
			Registry.register(Registry.RECIPE_TYPE, SalvagingRecipe.ID, INSTANCE);
			event.getRegistry().register(SalvagingRecipe.Serializer.INSTANCE.setRegistryName(SalvagingRecipe.ID));
		}
	}
}
