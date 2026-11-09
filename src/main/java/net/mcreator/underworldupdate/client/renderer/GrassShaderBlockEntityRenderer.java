package net.mcreator.underworldupdate.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.mcreator.underworldupdate.block.entity.GrassShaderBlockEntity;
import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.mcreator.underworldupdate.init.UnderworldUpdateModBlockEntities;
import net.mcreator.underworldupdate.client.ClientShaderHandler;
import net.mcreator.underworldupdate.init.UnderworldUpdateModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import java.util.function.Supplier;

@EventBusSubscriber(modid = UnderworldUpdateMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class GrassShaderBlockEntityRenderer implements BlockEntityRenderer<GrassShaderBlockEntity> {
    private static final RenderType LIGHT_GRASS = create("underworld_update_light_grass", () -> ClientShaderHandler.lightGrassShader);
    private static final RenderType DARK_GRASS = create("underworld_update_dark_grass", () -> ClientShaderHandler.darkGrassShader);

    public GrassShaderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                UnderworldUpdateModBlockEntities.GRASS_SHADER.get(),
                GrassShaderBlockEntityRenderer::new);
    }

    @Override
    public void render(GrassShaderBlockEntity blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        boolean isLightGrass = state.is(UnderworldUpdateModBlocks.LIGHTGRASS.get());
        ShaderInstance shader = isLightGrass ? ClientShaderHandler.lightGrassShader : ClientShaderHandler.darkGrassShader;
        if (shader == null || blockEntity.getLevel() == null) {
            return;
        }

        float time = (float) ((System.nanoTime() / 1_000_000L) % 100_000_000L) / 1000.0f;
        shader.safeGetUniform("Time").set(time);
        RenderType renderType = isLightGrass ? LIGHT_GRASS : DARK_GRASS;
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                state,
                poseStack,
                ignored -> bufferSource.getBuffer(renderType),
                isLightGrass ? LightTexture.FULL_BRIGHT : packedLight,
                packedOverlay);
    }

    @Override
    public AABB getRenderBoundingBox(GrassShaderBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(1.0);
    }

    private static RenderType create(String name, Supplier<ShaderInstance> shader) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(shader))
                .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, true))
                .setTransparencyState(RenderStateShard.TransparencyStateShard.TRANSLUCENT_TRANSPARENCY)
                .setDepthTestState(RenderStateShard.DepthTestStateShard.LEQUAL_DEPTH_TEST)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setCullState(RenderStateShard.CullStateShard.CULL)
                .setLightmapState(RenderStateShard.LightmapStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OverlayStateShard.OVERLAY)
                .setWriteMaskState(RenderStateShard.WriteMaskStateShard.COLOR_WRITE)
                .createCompositeState(true);
        return RenderType.create(name, DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, state);
    }
}
