package net.mcreator.underworldupdate.iles.fairy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Anneau lumineux qui tourne autour de la fée : blanc pour la lumière, mauve pour les ténèbres. */
public class FairyGlowLayer extends RenderLayer<FairyEntity, FairyModel> {
    private static final ResourceLocation GLOW =
            ResourceLocation.fromNamespaceAndPath(FairyEntities.NAMESPACE, "textures/entity/fairy_glow.png");

    /**
     * Opacité de l'anneau (0xCC = 80 %). L'anneau écrit la profondeur (sinon la bulle le repeint et il disparaît) :
     * il doit donc être assez opaque pour rester lumineux. Baisse la valeur pour un anneau plus discret.
     */
    private static final int ALPHA = 0xCC;
    private static final int LIGHT_RGB = 0xFFFFFF;
    private static final int DARK_RGB = 0x8C1FF2;

    public FairyGlowLayer(RenderLayerParent<FairyEntity, FairyModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, FairyEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        VertexConsumer vc = bufferSource.getBuffer(FairyRenderTypes.emissiveDepth(GLOW));
        int rgb = entity.isDark() ? DARK_RGB : LIGHT_RGB;
        this.getParentModel().renderGlow(poseStack, vc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ((int) (ALPHA * (1.0F - 0.5F * entity.anim.wRest)) << 24) | rgb);
    }
}
