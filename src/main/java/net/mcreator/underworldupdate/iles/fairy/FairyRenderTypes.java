package net.mcreator.underworldupdate.iles.fairy;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Type de rendu émissif (aucun éclairage, aucun ombrage) qui ÉCRIT LA PROFONDEUR.
 * Indispensable ici : l'effet de la bulle est une passe plein écran qui lit le tampon de profondeur ;
 * avec RenderType.entityTranslucentEmissive (qui n'écrit pas la profondeur), la fée était prise pour du vide
 * et se faisait repeindre par le ciel de la bulle.
 * Même configuration que entityTranslucentEmissive, avec COLOR_DEPTH_WRITE.
 */
public abstract class FairyRenderTypes extends RenderType {
    private static final Map<ResourceLocation, RenderType> CACHE = new HashMap<>();

    private FairyRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                             boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
        throw new UnsupportedOperationException("Ne pas instancier : classe utilitaire");
    }

    public static RenderType emissiveDepth(ResourceLocation texture) {
        return CACHE.computeIfAbsent(texture, tex -> create("fairy_emissive_depth",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(true)));
    }
}
