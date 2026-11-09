package net.mcreator.underworldupdate.swap;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Principe : on copie le depth buffer à deux moments du rendu du monde.
 *   - après les blocs opaques  -> profondeur du terrain seul
 *   - après les entités / block entities -> profondeur terrain + entités
 * Un pixel dont la profondeur a diminué entre les deux appartient à une entité (-> blanc).
 * On copie aussi la couleur de la scène (pour la vision monochrome) avant de dessiner par-dessus.
 * Le shader applique le tout à l'intérieur d'une onde sphérique centrée sur le joueur.
 */
@EventBusSubscriber(modid = UnderworldUpdateMod.MODID, value = Dist.CLIENT)
public final class SwapRenderer {
    private static TextureTarget terrainDepth;
    private static TextureTarget entityDepth;
    private static TextureTarget sceneColor;
    private static boolean capturedTerrain;
    private static boolean capturedEntities;

    private SwapRenderer() {}

    @SubscribeEvent
    public static void onStage(RenderLevelStageEvent event) {
        if (!SwapClient.isActive()) return;
        RenderLevelStageEvent.Stage stage = event.getStage();
        if (stage == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            terrainDepth = captureDepth(terrainDepth);
            capturedTerrain = true;
        } else if (stage == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            entityDepth = captureDepth(entityDepth);
            capturedEntities = true;
        } else if (stage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            draw(event);
        }
    }

    private static TextureTarget ensure(TextureTarget target, RenderTarget main, boolean depth) {
        if (target == null) {
            return new TextureTarget(main.width, main.height, depth, Minecraft.ON_OSX);
        }
        if (target.width != main.width || target.height != main.height) {
            target.resize(main.width, main.height, Minecraft.ON_OSX);
        }
        return target;
    }

    private static TextureTarget captureDepth(TextureTarget target) {
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        target = ensure(target, main, true);
        target.copyDepthFrom(main);
        main.bindWrite(false);   // copyDepthFrom débind le framebuffer, on remet le principal
        return target;
    }

    private static void captureColor(RenderTarget main) {
        sceneColor = ensure(sceneColor, main, false);
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, main.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, sceneColor.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, main.width, main.height, 0, 0, sceneColor.width, sceneColor.height,
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        main.bindWrite(false);
    }

    private static void draw(RenderLevelStageEvent event) {
        ShaderInstance shader = SwapShaders.swapWorld;
        boolean ready = shader != null && capturedTerrain && capturedEntities;
        capturedTerrain = false;
        capturedEntities = false;
        if (!ready) return;

        float pt = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        SwapClient.updateHoles(pt);
        Vec3 cam = event.getCamera().getPosition();
        float radius = SwapClient.holeRadius(pt);
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();

        captureColor(main);

        Matrix4f invViewProj = new Matrix4f(event.getProjectionMatrix()).mul(event.getModelViewMatrix()).invert();
        shader.safeGetUniform("InvViewProj").set(invViewProj);
        shader.safeGetUniform("Params").set((SwapClient.ticks + pt) / 20f,
                SwapClient.outerRadius(pt), SwapClient.innerRadius(pt), 0f);
        setPoint(shader, "WaveC", SwapClient.waveCenter(), cam, 0f);
        setPoint(shader, "HoleA", SwapClient.holeA(), cam, radius);
        setPoint(shader, "HoleB", SwapClient.holeB(), cam, radius);
        shader.setSampler("TerrainDepth", terrainDepth.getDepthTextureId());
        shader.setSampler("EntityDepth", entityDepth.getDepthTextureId());
        shader.setSampler("SceneColor", sceneColor.getColorTextureId());

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader(() -> shader);

        BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buf.addVertex(-1f, -1f, 0f);
        buf.addVertex(1f, -1f, 0f);
        buf.addVertex(1f, 1f, 0f);
        buf.addVertex(-1f, 1f, 0f);
        BufferUploader.drawWithShader(buf.buildOrThrow());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    /** Position relative à la caméra (calculée en double côté CPU, pour éviter les soucis de précision). */
    private static void setPoint(ShaderInstance shader, String name, Vec3 pos, Vec3 cam, float w) {
        shader.safeGetUniform(name).set((float) (pos.x - cam.x), (float) (pos.y - cam.y), (float) (pos.z - cam.z), w);
    }
}