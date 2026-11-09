package net.mcreator.underworldupdate.iles.fairy;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class FairyRenderer extends MobRenderer<FairyEntity, FairyModel> {
    private static final ResourceLocation LIGHT =
            ResourceLocation.fromNamespaceAndPath(FairyEntities.NAMESPACE, "textures/entity/light_fairy.png");
    private static final ResourceLocation DARK =
            ResourceLocation.fromNamespaceAndPath(FairyEntities.NAMESPACE, "textures/entity/dark_fairy.png");

    public FairyRenderer(EntityRendererProvider.Context context) {
        super(context, new FairyModel(context.bakeLayer(FairyModel.LAYER)), 0.0F);
        this.addLayer(new FairyCoreLayer(this));
        this.addLayer(new FairyGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(FairyEntity entity) {
        return entity.isDark() ? DARK : LIGHT;
    }

    /** Les fées brillent : toujours pleinement éclairées, même dans le noir. */
    @Override
    protected int getBlockLightLevel(FairyEntity entity, BlockPos pos) {
        return 15;
    }
}
