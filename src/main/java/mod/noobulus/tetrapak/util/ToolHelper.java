package mod.noobulus.tetrapak.util;

import mod.noobulus.tetrapak.BuildConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = BuildConfig.MODID)
public class ToolHelper {
	private static final Map<Pair<String, Integer>, List<ItemStack>> tools = new HashMap<>();
	private static final List<Pair<String, String>> materialPairs = Arrays.asList(
		Pair.of("oak", "stick"),
		Pair.of("stone", "stick"),
		Pair.of("iron", "spruce"),
		Pair.of("blackstone", "spruce"),
		Pair.of("obsidian", "iron"),
		Pair.of("netherite", "forged_beam")
	);

	private static final List<String> doubleSchematics = Arrays.asList("basic_hammer", "claw");

	private ToolHelper() {
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartedEvent event) {
		tools.clear();
		populate();
	}

	public static void populate() {
		for (ToolAction t : ToolAction.getActions()) {
			for (Item item : ForgeRegistries.ITEMS.getValues()) {
				ItemStack test = new ItemStack(item);
				for (int i = 0; i <= getTierOf(test, t); i++) {
					tools.computeIfAbsent(Pair.of(t.name(), i), p -> new ArrayList<>()).add(test);
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
		if (!(tool.getItem() instanceof ItemModularHandheld item))
			return;
		ToolAction.getActions().stream().filter(tool::canPerformAction).forEach(t -> {
			for (int i = 0; i <= item.getHarvestLevel(tool, t, null, null); i++) {
				tools.computeIfAbsent(Pair.of(t.name(), i), p -> new ArrayList<>()).add(tool);
			}
		});
	}

	public static List<ItemStack> getToolsOf(ToolAction type, int level) {
		if (tools.isEmpty())
			populate();
		return tools.getOrDefault(Pair.of(type.name(), level), Collections.emptyList());
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

	public static int getTierOf(@Nullable ItemStack stack, ToolAction action) {
		if (stack == null || stack.isEmpty())
			return -1;
		if (stack.getItem() instanceof ItemModularHandheld handheld) {
			return handheld.getHarvestLevel(stack, action, null, null);
		} else if (stack.getItem() instanceof TieredItem tieredItem) {
			return tieredItem.getTier().getLevel();
		}
		return -1;
	}
}
