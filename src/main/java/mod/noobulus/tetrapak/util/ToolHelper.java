package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.BuildConfig;
import mod.noobulus.tetrapak.util.tetra_definitions.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.tetra.items.modular.IModularItem;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = BuildConfig.MODID)
public class ToolHelper {
	private static final Map<Pair<ToolType, Integer>, List<ItemStack>> tools = new HashMap<>();
	private static final List<Pair<String, String>> materialPairs = Arrays.asList(
		Pair.of("oak", "stick"),
		Pair.of("stone", "stick"),
		Pair.of("iron", "spruce"),
		Pair.of("blackstone", "spruce"),
		Pair.of("obsidian", "iron"),
		Pair.of("netherite", "forged_beam")
	);

	private static final List<String> doubleSchematics = Arrays.asList("basic_hammer", "claw");

	@SubscribeEvent
	public static void populateToolTable(ClientPlayerNetworkEvent.LoggedInEvent event) {
		tools.clear();
		for (ToolType t : getValues().values()) {
			for (Item item : ForgeRegistries.ITEMS.getValues()) {
				ItemStack test = new ItemStack(item);
				int harvestLevel = test.getHarvestLevel(t, null, null);
				for (int i = 0; i <= harvestLevel; i++) {
					Pair<ToolType, Integer> p = Pair.of(t, i);
					if (!tools.containsKey(p))
						tools.put(p, new ArrayList<>());
					tools.get(p).add(test);
				}
			}
		}

		materialPairs.forEach(materials -> {
			doubleSchematics
				.stream()
				.map(schematic -> setUpDouble(schematic, materials.getFirst(), materials.getSecond()))
				.forEach(ToolHelper::addModular);
			addModular(setUpShortBlade(materials.getFirst(), materials.getSecond()));
		});

	}

	private static void addModular(ItemStack tool) {
		if (tool.isEmpty())
			return;
		tool.getToolTypes().forEach(toolType -> {
			int harvestLevel = tool.getHarvestLevel(toolType, null, null);
			for (int i = 0; i <= harvestLevel; i++) {
				Pair<ToolType, Integer> p = Pair.of(toolType, i);
				if (!tools.containsKey(p))
					tools.put(p, new ArrayList<>());
				tools.get(p).add(tool);
			}
		});
	}

	public static Ingredient getToolsOf(ToolType type, int level) {
		return Ingredient.of(tools.getOrDefault(Pair.of(type, level), Collections.emptyList()).stream());
	}

	public static Map<String, ToolType> getValues() {
		return ObfuscationReflectionHelper.getPrivateValue(ToolType.class, null, "VALUES");
	}

	private static ItemStack setUpDouble(String moduleVariant, String headMaterial, String handleMaterial) {
		Item modularDouble = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:modular_double"));
		if (modularDouble == null)
			return ItemStack.EMPTY;
		ItemStack itemStack = new ItemStack(modularDouble);

		IModularItem.putModuleInSlot(itemStack, "double/head_left", "double/" + moduleVariant + "_left", "double/" + moduleVariant + "_left_material", moduleVariant + "/" + headMaterial);
		IModularItem.putModuleInSlot(itemStack, "double/head_right", "double/" + moduleVariant + "_right", "double/" + moduleVariant + "_right_material", moduleVariant + "/" + headMaterial);
		IModularItem.putModuleInSlot(itemStack, "double/handle", "double/basic_handle", "double/basic_handle_material", "basic_handle/" + handleMaterial);

		IModularItem.updateIdentifier(itemStack);

		return itemStack;
	}

	private static ItemStack setUpShortBlade(String bladeMaterial, String handleMaterial) {
		Item modularSword = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:modular_sword"));
		if (modularSword == null)
			return ItemStack.EMPTY;
		ItemStack itemStack = new ItemStack(modularSword);

		IModularItem.putModuleInSlot(itemStack, "sword/guard", "sword/makeshift_guard", "sword/makeshift_guard_material", "makeshift_guard/oak");
		IModularItem.putModuleInSlot(itemStack, "sword/pommel", "sword/decorative_pommel", "sword/decorative_pommel_material", "decorative_pommel/oak");
		IModularItem.putModuleInSlot(itemStack, "sword/hilt", "sword/basic_hilt", "sword/basic_hilt_material", "basic_hilt/" + handleMaterial);
		IModularItem.putModuleInSlot(itemStack, "sword/blade", "sword/short_blade", "sword/short_blade_material", "short_blade/" + bladeMaterial);
		IModularItem.updateIdentifier(itemStack);

		return itemStack;
	}
}
