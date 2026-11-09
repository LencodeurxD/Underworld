package net.mcreator.underworldupdate.client;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.mcreator.underworldupdate.init.UnderworldUpdateModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = UnderworldUpdateMod.MODID, value = Dist.CLIENT)
public final class GrassShaderBlockEntityLoader {
    private GrassShaderBlockEntityLoader() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        LevelChunkSection[] sections = chunk.getSections();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minSection = chunk.getMinSection();

        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];
            if (!section.maybeHas(GrassShaderBlockEntityLoader::isGrassShaderBlock)) {
                continue;
            }

            int minY = (minSection + sectionIndex) << 4;
            for (int localY = 0; localY < 16; localY++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    for (int localX = 0; localX < 16; localX++) {
                        BlockState state = section.getBlockState(localX, localY, localZ);
                        if (!isGrassShaderBlock(state)) {
                            continue;
                        }

                        pos.set(chunk.getPos().getMinBlockX() + localX, minY + localY,
                                chunk.getPos().getMinBlockZ() + localZ);
                        if (chunk.getBlockState(pos.above()).canOcclude()) {
                            continue;
                        }
                        chunk.getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);
                    }
                }
            }
        }
    }

    private static boolean isGrassShaderBlock(BlockState state) {
        return state.is(UnderworldUpdateModBlocks.LIGHTGRASS.get()) || state.is(UnderworldUpdateModBlocks.ENDGRASS.get());
    }
}
