package mod.noobulus.tetrapak.create.recipes;

import com.google.gson.JsonObject;
import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity;
import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.TetraPak;
import mod.noobulus.tetrapak.util.LootLoader;
import mod.noobulus.tetrapak.util.RecalculatableLazyValue;
import mod.noobulus.tetrapak.util.ToolHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SalvagingRecipe implements Recipe<Container> {
	public static final Serializer SERIALIZER = new Serializer();
	private static final LootContextParamSet lootParameters = new LootContextParamSet.Builder()
		.required(LootContextParams.ORIGIN)
		.optional(LootContextParams.TOOL)
		.optional(LootContextParams.THIS_ENTITY)
		.build();

	public final ToolAction toolType;
	public final int toolLevel;
	public final Ingredient startingItem;
	public final ResourceLocation lootTable;
	public final RecalculatableLazyValue<List<LootLoader.LootSlot>> contents;
	public final RecalculatableLazyValue<List<ItemStack>> toolExamples;
	private final ResourceLocation id;
	@Nullable
	private DeployerAwareInventory recipeInv;

	public SalvagingRecipe(ResourceLocation id, ToolAction toolType, int toolLevel, Ingredient startingItem, ResourceLocation lootTable) {
		this.id = id;
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
	public boolean matches(Container iInventory, Level level) {
		if (iInventory.getContainerSize() != 2)
			return false;
		if (ToolHelper.getTierOf(iInventory.getItem(0), toolType) < toolLevel)
			return false;
		return startingItem.test(iInventory.getItem(1));
	}

	public ItemStack assemble(Container inv) {
		return this.getResultItem();
	}

	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	public List<ItemStack> rollResults(Container iInventory, ServerLevel level, @Nullable Player playerEntity) {
		if (iInventory.getContainerSize() != 2)
			return new ArrayList<>();
		ItemStack toolSlot = iInventory.getItem(0);
		if (toolSlot.isEmpty())
			return new ArrayList<>();
		LootContext.Builder builder = new LootContext.Builder(level)
			.withParameter(LootContextParams.TOOL, toolSlot);
		if (playerEntity != null) {
			builder.withParameter(LootContextParams.THIS_ENTITY, playerEntity)
				.withParameter(LootContextParams.ORIGIN, playerEntity.position())
				.withLuck(playerEntity.getLuck());
		}

		LootTable table = level.getServer().getLootTables().get(lootTable);
		return table.getRandomItems(builder.create(lootParameters));
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return SalvagingRecipeType.AUTOMATIC_SALVAGING;
	}

	public void setBufferedInv(DeployerAwareInventory recipeInv) {
		this.recipeInv = recipeInv;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Nullable
	public DeployerAwareInventory getRecipeInv() {
		return recipeInv;
	}

	private List<LootLoader.LootSlot> getContents() {
		LootTables manager = LootLoader.getManager();
		return LootLoader.crawlTable(manager.get(lootTable), manager);
	}

	public static class DeployerAwareInventory extends RecipeWrapper {
		public final DeployerTileEntity deployerTileEntity;
		public final DeployerFakePlayer deployerFakePlayer;
		public final Consumer<List<Item>> onRecipeApply;

		public DeployerAwareInventory(IItemHandlerModifiable inv, DeployerTileEntity deployerTileEntity, DeployerFakePlayer deployerFakePlayer, Consumer<List<Item>> onRecipeApply) {
			super(inv);
			this.deployerTileEntity = deployerTileEntity;
			this.deployerFakePlayer = deployerFakePlayer;
			this.onRecipeApply = onRecipeApply;
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = BuildConfig.MODID)
	public static class SalvagingRecipeType implements RecipeType<SalvagingRecipe> {
		public static final RecipeType<SalvagingRecipe> AUTOMATIC_SALVAGING = new SalvagingRecipeType();

		private SalvagingRecipeType() {
		}

		@SubscribeEvent
		public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
			Registry.register(Registry.RECIPE_TYPE, TetraPak.asId("automatic_salvaging"), AUTOMATIC_SALVAGING);
			event.getRegistry().register(SalvagingRecipe.SERIALIZER.setRegistryName(TetraPak.asId("automatic_salvaging")));
		}
	}

	private static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>>
		implements RecipeSerializer<SalvagingRecipe> {

		@Override
		public SalvagingRecipe fromJson(ResourceLocation id, JsonObject json) {
			final Ingredient ingredient = Ingredient.fromJson(json.get("requirement"));
			final int toolLevel = GsonHelper.isValidNode(json, "tool_level") ? GsonHelper.getAsInt(json, "tool_level") : 0;
			final ToolAction toolType = ToolAction.get(GsonHelper.getAsString(json, "tool_type"));
			final ResourceLocation lootTable = new ResourceLocation(GsonHelper.getAsString(json, "lootTable"));
			return new SalvagingRecipe(id, toolType, toolLevel, ingredient, lootTable);
		}

		@Nullable
		@Override
		public SalvagingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf packetBuffer) {
			final Ingredient ingredient = Ingredient.fromNetwork(packetBuffer);
			final int toolLevel = packetBuffer.readInt();
			final ResourceLocation lootTable = packetBuffer.readResourceLocation();
			final ToolAction toolType = ToolAction.get(packetBuffer.readUtf(32767));
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
		public void toNetwork(FriendlyByteBuf packetBuffer, SalvagingRecipe recipe) {
			recipe.startingItem.toNetwork(packetBuffer);
			packetBuffer.writeInt(recipe.toolLevel);
			packetBuffer.writeResourceLocation(recipe.lootTable);
			packetBuffer.writeUtf(recipe.toolType.name());

			List<LootLoader.LootSlot> slots = recipe.contents.get();
			packetBuffer.writeInt(slots.size());
			slots.forEach(slot -> slot.toBuffer(packetBuffer));

			List<ItemStack> examples = recipe.toolExamples.get();
			packetBuffer.writeInt(examples.size());
			examples.forEach(tool -> packetBuffer.writeItemStack(tool, false));
		}
	}
}
