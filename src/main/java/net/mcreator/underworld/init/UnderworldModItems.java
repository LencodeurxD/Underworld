/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworld.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.mcreator.underworld.UnderworldMod;

public class UnderworldModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(UnderworldMod.MODID);
	public static final DeferredItem<Item> MON_BLOC_A_MOI;
	static {
		MON_BLOC_A_MOI = block(UnderworldModBlocks.MON_BLOC_A_MOI);
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return block(block, new Item.Properties());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}