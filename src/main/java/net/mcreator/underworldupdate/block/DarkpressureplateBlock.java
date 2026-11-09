package net.mcreator.underworldupdate.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.PressurePlateBlock;

public class DarkpressureplateBlock extends PressurePlateBlock {
	public DarkpressureplateBlock() {
		super(BlockSetType.OAK, BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1f, 10f).noCollission().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false).forceSolidOn());
	}
}