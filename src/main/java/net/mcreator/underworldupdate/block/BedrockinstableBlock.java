package net.mcreator.underworldupdate.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;

public class BedrockinstableBlock extends Block {
	public BedrockinstableBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(55f, 17f).lightLevel(blockstate -> 4).requiresCorrectToolForDrops().friction(0.53f).pushReaction(PushReaction.BLOCK).instrument(NoteBlockInstrument.DIDGERIDOO));
	}
}