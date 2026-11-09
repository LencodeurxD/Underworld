
package net.mcreator.underworldupdate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.underworldupdate.network.UnderworldUpdateModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import org.joml.Matrix4f;

@EventBusSubscriber(modid = "underworld_update", value = Dist.CLIENT)
public class GrapplingRopeRenderer {

    private static final ResourceLocation ROPE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "underworld_update",
                    "textures/entities/corde_grappin.png"
            );

    private static final RenderType ROPE_RENDER_TYPE =
            RenderType.entityCutoutNoCull(ROPE_TEXTURE);

    // Largeur de chaque surface
    private static final double ROPE_RADIUS = 0.025;

    // Distance maximale d'affichage
    private static final double MAX_DISTANCE = 128.0;

    @SubscribeEvent
    public static void renderRopes(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
            return;

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null)
            return;

        PoseStack poseStack = event.getPoseStack();

        if (poseStack == null)
            return;

        Vec3 camera = event.getCamera().getPosition();

        MultiBufferSource.BufferSource buffers =
                minecraft.renderBuffers().bufferSource();

        boolean rendered = false;

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        for (Player player : minecraft.level.players()) {

            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            if (!isGrappling(mainHand) && !isGrappling(offHand))
                continue;

            // Point d'accroche mémorisé dans les variables du joueur.
            // Cette classe doit exister dans le projet underworld_update.
            Vec3 target = player.getData(
                    UnderworldUpdateModVariables.PLAYER_VARIABLES
            ).xyz;

            // Départ de la corde, près de la tête du joueur.
            Vec3 start = player.position().add(
                    0,
                    player.getEyeHeight() * 0.8,
                    0
            );

            double distance = start.distanceTo(target);

            if (distance < 0.1 || distance > MAX_DISTANCE)
                continue;

            VertexConsumer consumer =
                    buffers.getBuffer(ROPE_RENDER_TYPE);

            drawRope(poseStack, consumer, start, target);

            rendered = true;
        }

        poseStack.popPose();

        if (rendered)
            buffers.endBatch(ROPE_RENDER_TYPE);
    }

    /**
     * Vérifie si le grappin est activé sur l'objet tenu.
     */
    private static boolean isGrappling(ItemStack stack) {
        CustomData data = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
        );

        return data.copyTag().getBoolean("t");
    }

    /**
     * Dessine deux surfaces plates croisées.
     * Aucune géométrie de tube 3D n'est nécessaire.
     */
    private static void drawRope(
            PoseStack poseStack,
            VertexConsumer consumer,
            Vec3 start,
            Vec3 end
    ) {
        Vec3 direction = end.subtract(start);

        if (direction.lengthSqr() < 0.000001)
            return;

        Vec3 axis = direction.normalize();

        // Première direction perpendiculaire à la corde.
        Vec3 side1 = axis.cross(new Vec3(0, 1, 0));

        // Évite un vecteur nul si la corde est verticale.
        if (side1.lengthSqr() < 0.0001) {
            side1 = axis.cross(new Vec3(1, 0, 0));
        }

        side1 = side1.normalize().scale(ROPE_RADIUS);

        // Deuxième surface, croisée à 90 degrés.
        Vec3 side2 = axis.cross(side1.normalize())
                .normalize()
                .scale(ROPE_RADIUS);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        double length = start.distanceTo(end);

        // Répétition de la texture pour obtenir un aspect de corde.
        float textureRepeat = (float) (length * 4.0);

        drawFlatSurface(
                consumer, matrix, pose,
                start, end, side1, textureRepeat
        );

        drawFlatSurface(
                consumer, matrix, pose,
                start, end, side2, textureRepeat
        );
    }

    /**
     * Dessine une surface rectangulaire avec deux faces visibles.
     */
    private static void drawFlatSurface(
            VertexConsumer consumer,
            Matrix4f matrix,
            PoseStack.Pose pose,
            Vec3 start,
            Vec3 end,
            Vec3 side,
            float textureRepeat
    ) {
        Vec3 a = start.add(side);
        Vec3 b = start.subtract(side);
        Vec3 c = end.subtract(side);
        Vec3 d = end.add(side);

        Vec3 normal = end.subtract(start)
                .cross(side)
                .normalize();

        Vec3 backNormal = normal.scale(-1);

        // Face avant
        vertex(consumer, matrix, pose, a, 0, 0, normal);
        vertex(consumer, matrix, pose, b, 1, 0, normal);
        vertex(consumer, matrix, pose, c, 1, textureRepeat, normal);
        vertex(consumer, matrix, pose, d, 0, textureRepeat, normal);

        // Face arrière
        vertex(consumer, matrix, pose, d, 0, textureRepeat, backNormal);
        vertex(consumer, matrix, pose, c, 1, textureRepeat, backNormal);
        vertex(consumer, matrix, pose, b, 1, 0, backNormal);
        vertex(consumer, matrix, pose, a, 0, 0, backNormal);
    }

    /**
     * Ajoute un sommet à la surface texturée.
     */
    private static void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            PoseStack.Pose pose,
            Vec3 position,
            float u,
            float v,
            Vec3 normal
    ) {
        consumer.addVertex(
                        matrix,
                        (float) position.x,
                        (float) position.y,
                        (float) position.z
                )
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(
                        pose,
                        (float) normal.x,
                        (float) normal.y,
                        (float) normal.z
                );
    }
}
