package net.mcreator.underworldupdate.iles;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Random;

public final class VoidLightClient {
    public static final String MOD_ID = "underworld_update";

    public static final String NAMESPACE = "underworld";

    private static final ResourceKey<Level> DIM =
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(NAMESPACE, "underworld"));

    private static final float INTENSITY = 0.9f;
    private static final float VOID_Y = 30.0f;
    private static final float FALLOFF = 45.0f;
    private static final float MAX_DIST = 192.0f;

    private static final float DARK_INTENSITY = 1.0f;
    private static final float DARK_Y = 225.0f;
    private static final float DARK_FALLOFF = 65.0f;
    private static final float FLOW_SPEED = 0.35f;

    private static final float SMOKE_INTENSITY = 0.0f;
    private static final float SMOKE_BASE_Y = 190.0f;
    private static final float SMOKE_SPEED = 0.06f;

    private static final float HORIZON_SOFTNESS = 0.08f;

    private static final float EFFECT_FADE_START = 0.35f;
    private static final float EFFECT_FADE_END = 0.75f;

    private static final float VOID_BRIGHTNESS = 0.6f;

    private static final float MIST_INTENSITY = 0.6f;
    private static final float MIST_SCALE = 0.015f;
    private static final float MIST_SPEED = 0.006f;
    private static final float MIST_CONTRAST = 0.8f;

    private static final boolean SURFACE_PASS = true;
    private static final float DARK_DIM = 0.55f;
    private static final float NEUTRAL_DIM = 0.35f;

    private static final float UNDERSIDE_GLOW = 0.05f;

    private static final float SURFACE_FOG = 0.30f;

    private static final float LIGHT_ISLAND_GLOW = 0.18f;

    private static final float DARK_ISLAND_GLOW = 0.04f;

    private static final float HORIZON_DROP = 0.12f;

    private static final float SKY_HAZE = 0.3f;

    private static final float ISLAND_MIST_LIGHT = 0.6f;
    private static final float ISLAND_MIST_DARK = 0.4f;
    private static final float COLUMN_RADIUS = 0.25f;
    private static final float COLUMN_FADE = 140.0f;
    private static final float COLUMN_BOTTOM_Y = -30.0f;
    private static final float COLUMN_TOP_Y = 256.0f;

    private static final float BUBBLE_SCALE = 1.0f;
    private static final float BUBBLE_FILL_LIGHT = 0.85f;
    private static final float BUBBLE_FILL_DARK = 0.90f;
    private static final float BUBBLE_RIM = 1.0f;
    private static final float LIGHT_LIFT = 0.85f;
    private static final float DARK_CRUSH = 0.55f;
    private static final float BUBBLE_HAZE_LIGHT = 0.007f;
    private static final float BUBBLE_HAZE_DARK = 0.010f;
    private static final float BUBBLE_VOLUME = 1.0f;
    private static final float BUBBLE_WALL = 1.0f;
    private static final float BUBBLE_WALL_DENSITY = 1.0f;
    private static final float BUBBLE_SEAM = 0.0f;
    private static final float BUBBLE_WALL_VEINS = 1.0f;
    private static final float BUBBLE_SKY = 1.0f;
    private static final float BUBBLE_MOTES = 1.0f;
    private static final float BUBBLE_SMOOTH = 4.0f;

    private static final float INVERT_CHANCE = 0.35f;
    private static final float INVERT_FADE_SECONDS = 3.0f;

    private static final long SCAN_INTERVAL_MS = 1000L;
    private static final int SCAN_MAX_CHUNKS = 32;

    private static final float VOID_RADIUS = 100000.0f;

    private static final boolean POST_PASS = true;

    private static final boolean DEBUG = false;

    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean loggedFirstDraw = false;

    private static final float[] ISLANDS = new float[IslandScanner.MAX_ISLANDS * 4];
    private static int bubbleCount = 0;
    private static final float[] AXES = new float[IslandScanner.MAX_ISLANDS * 4];
    private static long lastScanMs = 0L;

    public static volatile float bubbleLight = 0.0f;
    public static volatile float bubbleDark = 0.0f;
    private static float bubbleInvert = 0.0f;
    private static float camPolarity = 0.0f;
    private static boolean warnedUnknownBiomes = false;
    private static float invertTarget = 0.0f;
    private static boolean wasInside = false;
    private static long lastBubbleNs = 0L;
    private static final Random RANDOM = new Random();

    private static ShaderInstance shader;
    private static TextureTarget depthCopy;

    private VoidLightClient() {}

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {
        static {
            LOGGER.info("[UnderWorld] VoidLightClient enregistré (mod id = " + MOD_ID + ")");
        }

        @SubscribeEvent
        public static void registerEffects(RegisterDimensionSpecialEffectsEvent event) {
            event.register(ResourceLocation.fromNamespaceAndPath(NAMESPACE, "underworld"), new UnderworldEffects());
            LOGGER.info("[UnderWorld] effets de dimension underworld:underworld enregistrés (sans nuages)");
        }

        @SubscribeEvent
        public static void registerShaders(RegisterShadersEvent event) {
            try {
                event.registerShader(
                        new ShaderInstance(event.getResourceProvider(),
                                ResourceLocation.fromNamespaceAndPath(NAMESPACE, "void_light"),
                                DefaultVertexFormat.POSITION),
                        s -> {
                            shader = s;
                            LOGGER.info("[UnderWorld] shader void_light chargé");
                        });
            } catch (IOException e) {
                throw new RuntimeException("[UnderWorld] impossible de charger le shader void_light", e);
            }
        }
    }

    public static final class UnderworldEffects extends DimensionSpecialEffects {
        public UnderworldEffects() {
            super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, true, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
            return fogColor.multiply(brightness * 0.94F + 0.06F, brightness * 0.94F + 0.06F, brightness * 0.91F + 0.09F);
        }

        @Override
        public boolean isFoggyAt(int x, int y) {
            return false;
        }

        public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker,
                                         float skyLight, int pixelX, int pixelY, Vector3f colors) {
            float bl = bubbleLight;
            float bd = bubbleDark;
            if (bl > 0.001f) {
                float k = bl * LIGHT_LIFT;
                colors.set(colors.x + (1.0f - colors.x) * k, colors.y + (1.0f - colors.y) * k, colors.z + (1.0f - colors.z) * k);
            }
            if (bd > 0.001f) {
                float a = bd * DARK_CRUSH;
                float x = Math.max(colors.x, 0.0f), y = Math.max(colors.y, 0.0f), z = Math.max(colors.z, 0.0f);
                colors.set(x * (1.0f - a + a * x), y * (1.0f - a + a * y), z * (1.0f - a + a * Math.min(z, 1.0f)));
            }
        }
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class GameBus {
        @SubscribeEvent
        public static void onRenderLevelEvent(RenderLevelStageEvent event) {
            onRenderLevel(event);
        }

        @SubscribeEvent
        public static void onFogColor(ViewportEvent.ComputeFogColor event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.level.dimension() != DIM) return;
            float bl = bubbleLight;
            float bd = bubbleDark;
            if (bl < 0.001f && bd < 0.001f) return;
            float r = event.getRed(), g = event.getGreen(), b = event.getBlue();
            r += (0.97f - r) * bl; g += (0.95f - g) * bl; b += (0.88f - b) * bl;
            r += (0.02f - r) * bd; g += (0.01f - g) * bd; b += (0.04f - b) * bd;
            event.setRed(r);
            event.setGreen(g);
            event.setBlue(b);
        }
    }

    private static final boolean RUN_WITH_SHADERPACK = false;
    private static long lastPackCheckMs = 0L;
    private static boolean shaderPackActiveCached = false;
    private static boolean loggedPackState = false;

    private static boolean shaderPackActive() {
        long now = System.currentTimeMillis();
        if (now - lastPackCheckMs < 500L) return shaderPackActiveCached;
        lastPackCheckMs = now;
        boolean active = false;
        try {
            ModList mods = ModList.get();
            if (mods != null && (mods.isLoaded("iris") || mods.isLoaded("oculus"))) {
                try {
                    Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                    Object inst = api.getMethod("getInstance").invoke(null);
                    active = (Boolean) api.getMethod("isShaderPackInUse").invoke(inst);
                } catch (Throwable t) {
                    active = true;
                }
            }
        } catch (Throwable t) {
            active = false;
        }
        if (active != shaderPackActiveCached || !loggedPackState) {
            loggedPackState = true;
            LOGGER.info("[UnderWorld] shaderpack Iris actif : " + active
                    + (active && !RUN_WITH_SHADERPACK ? " -> effet void_light coupé (RUN_WITH_SHADERPACK = false)" : ""));
        }
        shaderPackActiveCached = active;
        return active;
    }

    private static boolean passBroken = false;
    private static int passFailures = 0;

    private static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
        Minecraft mc = Minecraft.getInstance();
        if (shader == null || mc.level == null || mc.level.dimension() != DIM) return;
        if (!POST_PASS || passBroken) return;
        if (!RUN_WITH_SHADERPACK && shaderPackActive()) {
            bubbleLight = 0.0f;
            bubbleDark = 0.0f;
            return;
        }
        try {
            renderPass(event);
        } catch (RuntimeException | LinkageError e) {
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            passFailures++;
            LOGGER.error("[UnderWorld] erreur dans la passe void_light (" + passFailures + "/3) : " + e);
            if (passFailures >= 3) {
                passBroken = true;
                LOGGER.error("[UnderWorld] passe void_light désactivée pour cette session (conflit probable avec un autre mod de rendu)");
            }
        }
    }

    private static void renderPass(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        Minecraft mc = Minecraft.getInstance();
        if (shader == null || mc.level == null || mc.level.dimension() != DIM) return;
        if (!POST_PASS) return;

        if (!loggedFirstDraw) {
            loggedFirstDraw = true;
            LOGGER.info("[UnderWorld] effet void_light actif dans la dimension underworld:underworld");
        }

        RenderTarget main = mc.getMainRenderTarget();

        if (depthCopy == null) {
            depthCopy = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
        } else if (depthCopy.width != main.width || depthCopy.height != main.height) {
            depthCopy.resize(main.width, main.height, Minecraft.ON_OSX);
        }
        depthCopy.copyDepthFrom(main);

        main.bindWrite(true);

        Matrix4f invViewProj = new Matrix4f(event.getProjectionMatrix())
                .mul(event.getModelViewMatrix())
                .invert();
        Vec3 cam = event.getCamera().getPosition();
        float time = (float) ((System.nanoTime() / 1_000_000L) % 100_000_000L) / 1000.0f;

        long nowMs = System.currentTimeMillis();
        if (nowMs - lastScanMs > SCAN_INTERVAL_MS) {
            lastScanMs = nowMs;
            try {
                int rd = Math.max(2, Math.min(mc.options.renderDistance().get(), SCAN_MAX_CHUNKS));
                bubbleCount = IslandScanner.scan(mc.level, cam.x, cam.y, cam.z, rd, ISLANDS, AXES);
                camPolarity = IslandScanner.cameraPolarity(cam.x, cam.y, cam.z);
                if (!warnedUnknownBiomes && IslandScanner.unknownColumns > 0) {
                    warnedUnknownBiomes = true;
                    LOGGER.warn("[UnderWorld] graine du monde inconnue côté client (serveur dédié ?) : les bulles ne sont pas dessinées. "
                            + "En solo / serveur intégré elle est partagée automatiquement.");
                }
            } catch (RuntimeException e) {
                LOGGER.warn("[UnderWorld] détection des îles impossible : " + e);
            }
        }

        updateBubbles(cam.x, cam.y, cam.z);

        shader.setSampler("Depth", depthCopy.getDepthTextureId());
        setU("InvViewProj", invViewProj);
        setU("CameraPos", (float) cam.x, (float) cam.y, (float) cam.z);
        setU("Time", time);
        setU("Params", DEBUG ? -1.0f : INTENSITY, VOID_Y, FALLOFF, MAX_DIST);
        setU("Params2", DARK_INTENSITY, DARK_Y, DARK_FALLOFF, FLOW_SPEED);
        setU("Params3", SMOKE_INTENSITY, SMOKE_BASE_Y, SMOKE_SPEED, HORIZON_SOFTNESS);

        setU("Params4", EFFECT_FADE_START, EFFECT_FADE_END, VOID_BRIGHTNESS, VOID_RADIUS);

        setU("Params5", MIST_INTENSITY, MIST_SCALE, MIST_SPEED, MIST_CONTRAST);
        setU("Params8", ISLAND_MIST_LIGHT, ISLAND_MIST_DARK, COLUMN_RADIUS, COLUMN_FADE);
        setU("Params12", SURFACE_PASS ? 1.0f : 0.0f, (float) bubbleCount, 0.0f, 0.0f);
        setU("Params10", LIGHT_ISLAND_GLOW, DARK_ISLAND_GLOW, HORIZON_DROP, SKY_HAZE);
        setU("Params9", COLUMN_BOTTOM_Y, COLUMN_TOP_Y, UNDERSIDE_GLOW, SURFACE_FOG);
        setU("Params13", bubbleLight, bubbleDark, bubbleInvert, BUBBLE_SCALE);
        setU("Params14", BUBBLE_FILL_LIGHT, BUBBLE_FILL_DARK, BUBBLE_RIM, BUBBLE_SEAM);
        setU("Params15", BUBBLE_HAZE_LIGHT, BUBBLE_HAZE_DARK, BUBBLE_VOLUME, BUBBLE_WALL_VEINS);
        setU("Params16", BUBBLE_WALL, BUBBLE_SKY, BUBBLE_MOTES, BUBBLE_WALL_DENSITY);
        setIslands(shader);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        drawFullscreen(shader);

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private static float smooth(float e0, float e1, float x) {
        float t = Math.max(0.0f, Math.min(1.0f, (x - e0) / (e1 - e0)));
        return t * t * (3.0f - 2.0f * t);
    }

    private static void updateBubbles(double cx, double cy, double cz) {
        long now = System.nanoTime();
        if (lastBubbleNs != 0L && now - lastBubbleNs > 2_000_000_000L) {
            bubbleLight = 0.0f; bubbleDark = 0.0f; bubbleInvert = 0.0f; invertTarget = 0.0f; wasInside = false;
        }
        float dt = lastBubbleNs == 0L ? 0.016f : Math.min((now - lastBubbleNs) / 1.0e9f, 0.25f);
        lastBubbleNs = now;

        float tl = 0.0f, td = 0.0f;
        for (int i = 0; i < IslandScanner.MAX_ISLANDS; i++) {
            float w = ISLANDS[i * 4 + 3];
            float ax = AXES[i * 4], ay = AXES[i * 4 + 1], az = AXES[i * 4 + 2];
            if (Math.abs(w) < 1.0f || ax < 1.0f || ay < 1.0f || az < 1.0f) continue;
            double dx = (cx - ISLANDS[i * 4]) / (ax * BUBBLE_SCALE);
            double dy = (cy - ISLANDS[i * 4 + 1]) / (ay * BUBBLE_SCALE);
            double dz = (cz - ISLANDS[i * 4 + 2]) / (az * BUBBLE_SCALE);
            float k = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            float thick = Math.min(0.20f, 24.0f / Math.min(ax, Math.min(ay, az)));
            float f = 1.0f - smooth(1.0f - thick, 1.0f, k);
            if (w > 0.0f) tl = Math.max(tl, f); else td = Math.max(td, f);
        }

        if (tl > 0.0f && td > 0.0f) {
            if (camPolarity > 0.0f) td *= (1.0f - tl); else tl *= (1.0f - td);
        }

        float kf = 1.0f - (float) Math.exp(-dt * BUBBLE_SMOOTH);
        bubbleLight += (tl - bubbleLight) * kf;
        bubbleDark += (td - bubbleDark) * kf;

        float m = Math.max(bubbleLight, bubbleDark);
        if (!wasInside && m > 0.6f) {
            wasInside = true;
            invertTarget = 0.0f;
        } else if (wasInside && m < 0.2f) {
            wasInside = false;
            invertTarget = RANDOM.nextFloat() < INVERT_CHANCE ? 1.0f : 0.0f;
        }
        float step = dt / INVERT_FADE_SECONDS;
        bubbleInvert += Math.max(-step, Math.min(step, invertTarget - bubbleInvert));
    }

    private static final java.util.Set<String> MISSING_UNIFORMS = new java.util.HashSet<>();

    private static Uniform uni(String name) {
        Uniform u = shader.getUniform(name);
        if (u == null && MISSING_UNIFORMS.add(name)) {
            LOGGER.error("[UnderWorld] uniform \"" + name + "\" ABSENT de void_light.json : le fichier assets/underworld/shaders/core/void_light.json "
                    + "de ton projet n'est pas celui du zip (remplace-le puis rebuild)");
        }
        return u;
    }

    private static void setU(String name, Matrix4f m) { Uniform u = uni(name); if (u != null) u.set(m); }
    private static void setU(String name, float a) { Uniform u = uni(name); if (u != null) u.set(a); }
    private static void setU(String name, float a, float b, float c) { Uniform u = uni(name); if (u != null) u.set(a, b, c); }
    private static void setU(String name, float a, float b, float c, float d) { Uniform u = uni(name); if (u != null) u.set(a, b, c, d); }

    private static void setIslands(ShaderInstance sh) {
        for (int i = 0; i < IslandScanner.MAX_ISLANDS; i++) {
            Uniform u = uni("Island" + i);
            if (u != null) u.set(ISLANDS[i * 4], ISLANDS[i * 4 + 1], ISLANDS[i * 4 + 2], ISLANDS[i * 4 + 3]);
            Uniform ua = uni("IslandA" + i);
            if (ua != null) ua.set(AXES[i * 4], AXES[i * 4 + 1], AXES[i * 4 + 2], AXES[i * 4 + 3]);
        }
    }

    private static void drawFullscreen(ShaderInstance sh) {
        RenderSystem.setShader(() -> sh);
        BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buf.addVertex(-1.0f, -1.0f, 0.0f);
        buf.addVertex(1.0f, -1.0f, 0.0f);
        buf.addVertex(1.0f, 1.0f, 0.0f);
        buf.addVertex(-1.0f, 1.0f, 0.0f);
        BufferUploader.drawWithShader(buf.buildOrThrow());
    }
}
