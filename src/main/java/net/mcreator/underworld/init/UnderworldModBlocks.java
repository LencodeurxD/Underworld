/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworld.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

import net.mcreator.underworld.block.MonBlocAMoiBlock;
import net.mcreator.underworld.UnderworldMod;

public class UnderworldModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(UnderworldMod.MODID);
	public static final DeferredBlock<Block> MON_BLOC_A_MOI;
	static {
		MON_BLOC_A_MOI = REGISTRY.register("mon_bloc_a_moi", MonBlocAMoiBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}