package net.mcreator.underworldupdate.iles.fairy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Cube de verre lumineux + noyau + étincelles, en émissif (aucun ombrage : la couleur vient de la texture). */
public class FairyCoreLayer extends RenderLayer<FairyEntity, FairyModel> {
    private static final ResourceLocation LIGHT =
            ResourceLocation.fromNamespaceAndPath(FairyEntities.NAMESPACE, "textures/entity/light_fairy.png");
    private static final ResourceLocation DARK =
            ResourceLocation.fromNamespaceAndPath(FairyEntities.NAMESPACE, "textures/entity/dark_fairy.png");

    private static int ch(float v) {
        return Mth.clamp((int) v, 0, 255);
    }

    public FairyCoreLayer(RenderLayerParent<FairyEntity, FairyModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, FairyEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        VertexConsumer vc = bufferSource.getBuffer(FairyRenderTypes.emissiveDepth(entity.isDark() ? DARK : LIGHT));
        float seed = (entity.getId() * 0.731F) % 6.2831F;
        float tw = 0.5F + 0.5F * Mth.sin(ageInTicks * 0.15F + seed);   // scintillement lent
        float gaze = entity.anim.gaze;                                  // le joueur la regarde : elle brille un peu plus
        float rest = entity.anim.wRest;
        int a = 235 + (int) (20.0F * tw) - (int) (45.0F * rest);
        float c = 240.0F + 15.0F * tw + 15.0F * gaze + 12.0F * entity.anim.wChase
                + 10.0F * entity.anim.wHelp * (0.5F + 0.5F * Mth.sin(ageInTicks * 0.12F)) - 120.0F * rest;
        // chaque fée a sa petite nuance de couleur
        float p = entity.personality(), q = entity.personality2();
        float mr, mg, mb;
        if (entity.isDark()) { mr = 0.90F + 0.10F * q; mg = 0.82F + 0.18F * p; mb = 1.0F; }
        else { mr = 1.0F; mg = 0.90F + 0.10F * p; mb = 0.72F + 0.28F * q; }
        int color = (a << 24) | (ch(c * mr) << 16) | (ch(c * mg) << 8) | ch(c * mb);
        FairyModel model = this.getParentModel();
        model.renderCore(poseStack, vc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
    }
}
