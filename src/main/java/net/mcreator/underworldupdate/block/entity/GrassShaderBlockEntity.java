package net.mcreator.underworldupdate.block.entity;

import net.mcreator.underworldupdate.init.UnderworldUpdateModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GrassShaderBlockEntity extends BlockEntity {
    public GrassShaderBlockEntity(BlockPos pos, BlockState state) {
        super(UnderworldUpdateModBlockEntities.GRASS_SHADER.get(), pos, state);
    }
}
