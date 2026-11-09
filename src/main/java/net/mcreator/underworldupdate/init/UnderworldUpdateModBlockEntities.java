package net.mcreator.underworldupdate.init;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.mcreator.underworldupdate.block.entity.GrassShaderBlockEntity;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class UnderworldUpdateModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, UnderworldUpdateMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrassShaderBlockEntity>> GRASS_SHADER =
            REGISTRY.register("grass_shader", () -> BlockEntityType.Builder.of(
                    GrassShaderBlockEntity::new,
                    UnderworldUpdateModBlocks.LIGHTGRASS.get(),
                    UnderworldUpdateModBlocks.ENDGRASS.get()).build(null));

    private UnderworldUpdateModBlockEntities() {
    }
}
