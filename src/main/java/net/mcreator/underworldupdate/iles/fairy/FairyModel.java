package net.mcreator.underworldupdate.iles.fairy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Une fée = un cube de verre lumineux (1/5 de bloc) avec un noyau blanc qui tourne dedans,
 * entouré d'un ANNEAU incliné qui tourne (12 petits cubes disposés en cercle, comme un halo).
 * L'anneau est indépendant de la fée : ressort suiveur (il traîne et rebondit), précession lente.
 * Les cubes sont modélisés en grand puis réduits (ModelPart.xScale).
 */
public class FairyModel extends EntityModel<FairyEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(FairyEntities.NAMESPACE, "fairy"), "main");

    private static final float LEAN = 2.2F;
    /** 24 px (sol) - 3,6 px : le centre est au milieu de la hitbox de 0,45 bloc. */
    private static final float CENTER_Y = 20.4F;
    private static final float BASE = 0.2F;
    private static final int SEGMENTS = 12;
    private static final float RING_R = 17.0F;

    private final ModelPart body;
    private final ModelPart core;
    private final ModelPart ring;      // incline l'anneau
    private final ModelPart ringSpin;  // le fait tourner sur lui-même

    public FairyModel(ModelPart root) {
        this.body = root.getChild("body");
        this.core = root.getChild("core");
        this.ring = root.getChild("ring");
        this.ringSpin = this.ring.getChild("ringspin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartPose at = PartPose.offset(0.0F, CENTER_Y, 0.0F);
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), at);
        root.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), at);
        PartDefinition ring = root.addOrReplaceChild("ring", CubeListBuilder.create(), at);
        PartDefinition spin = ring.addOrReplaceChild("ringspin", CubeListBuilder.create(), PartPose.ZERO);
        for (int i = 0; i < SEGMENTS; i++) {
            float th = (float) (Math.PI * 2.0 * i / SEGMENTS);
            // chaque petit cube est tangent au cercle : les 12 forment un anneau
            spin.addOrReplaceChild("seg" + i, CubeListBuilder.create().texOffs(0, 64)
                            .addBox(-5.1F, -2.0F, -2.0F, 10.2F, 4.0F, 4.0F),
                    PartPose.offsetAndRotation(RING_R * Mth.cos(th), 0.0F, RING_R * Mth.sin(th), 0.0F, -(th + (float) Math.PI / 2.0F), 0.0F));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    /** 0..1 : à quel point le joueur local regarde cette fée (cône d'environ 10°). */
    private static float gazeOf(FairyEntity e) {
        Player pl = Minecraft.getInstance().player;
        if (pl == null || pl.level() != e.level()) return 0.0F;
        Vec3 to = e.position().add(0.0, 0.22, 0.0).subtract(pl.getEyePosition());
        double d = to.length();
        if (d < 0.6 || d > 12.0) return 0.0F;
        double dot = to.scale(1.0 / d).dot(pl.getLookAngle());
        return (float) Mth.clamp((dot - 0.985) / 0.014, 0.0, 1.0);
    }

    @Override
    public void setupAnim(FairyEntity e, float limbSwing, float limbSwingAmount, float age, float netHeadYaw, float headPitch) {
        FairyAnim a = e.anim;
        a.gazeTarget = gazeOf(e);
        a.update(e, age);

        float p = e.personality();
        float seed = (e.getId() * 0.731F) % 6.2831F;

        // apparition : la fée « pousse » en rebondissant
        float x = Mth.clamp(age / 16.0F, 0.0F, 1.0F) - 1.0F;
        float appear = x >= 0.0F ? 1.0F : 1.0F + 2.70158F * x * x * x + 1.70158F * x * x;
        float size = (0.92F + 0.16F * p) * Math.max(appear, 0.0F);

        float y = CENTER_Y - Mth.sin(a.t + seed) * 0.9F * (1.0F - 0.7F * a.wRest);
        float xRot = Mth.clamp(a.vz * LEAN, -0.7F, 0.7F) + 0.18F * Mth.sin(a.t * 0.6F + seed);
        float zRot = Mth.clamp(a.vx * LEAN, -0.7F, 0.7F) + 0.12F * Mth.cos(a.t * 0.5F + seed);
        float yRot = a.spin + seed;
        float s = 1.0F;

        // elle parle : un bond net à chaque réplique
        y -= a.speak * 3.0F;
        s += a.speak * 0.20F;
        xRot += a.speak * 0.35F * Mth.sin(age * 1.2F);

        // elle écoute : petits hochements de tête
        float hop = Math.max(0.0F, Mth.sin(a.hop + (e.getUUID().hashCode() & 7))) * a.wChat * 0.4F;
        y -= hop * 2.0F;
        xRot += Mth.sin(a.hop * 0.5F + seed) * 0.15F * a.wChat;

        // liée au joueur : respire et rebondit doucement
        s += 0.08F * Mth.sin(age * 0.3F + seed) * a.wBond;
        y -= Mth.sin(age * 0.2F + seed) * 1.4F * a.wBond;

        // fuite : elle tremble et se fait toute petite
        xRot += Mth.sin(age * 1.7F + seed) * 0.16F * a.wFlee;
        zRot += Mth.cos(age * 1.9F + seed) * 0.16F * a.wFlee;
        s *= 1.0F - 0.12F * a.wFlee;

        // jeu de chat : le chasseur est gonflé et vibrant, les fuyardes sont vives et zigzaguent
        s += 0.18F * a.wChase + 0.04F * Mth.sin(age * 1.5F) * a.wChase;
        zRot += Mth.sin(age * 0.9F + seed) * 0.25F * a.wRun;

        // sieste : elle se tasse, respire lentement
        s *= 1.0F - 0.10F * a.wRest;
        y += 0.6F * a.wRest;

        // taquinerie : elle gigote d'un air malicieux
        xRot += Mth.sin(age * 2.3F + seed) * 0.12F * a.wPrank;
        zRot += Mth.cos(age * 2.9F + seed) * 0.12F * a.wPrank;
        s += 0.06F * Mth.sin(age * 1.1F) * a.wPrank;
        y -= Math.abs(Mth.sin(age * 0.9F + seed)) * 1.2F * a.wPrank;

        // numéro d'acrobatie : elle s'étire et se contracte en rythme
        s += 0.12F * Mth.sin(age * 0.9F) * a.wShow;

        // le joueur la regarde : elle se redresse et fait un petit saut joyeux
        y -= a.gaze * Math.abs(Mth.sin(age * 0.6F + seed)) * 1.0F;
        s += a.gaze * 0.06F;

        // étirement quand elle monte ou descend, écrasement sinon
        float stretch = 1.0F + Mth.clamp(a.vy * 2.0F, -0.3F, 0.3F);
        float sy = s * stretch * size;
        float sxz = s / Mth.sqrt(stretch) * size;

        float dx = 0.5F * Mth.sin(a.t * 0.45F + seed);
        float dy = 0.4F * Mth.sin(a.t * 0.60F + seed * 2.0F);
        float dz = 0.5F * Mth.cos(a.t * 0.50F + seed * 3.0F);

        pose(body, dx, y + dy, dz, xRot, yRot, zRot, sxz * BASE, sy * BASE, sxz * BASE);

        float breath = 1.0F + 0.15F * Mth.sin(age * 0.35F + seed) + 0.10F * a.gaze + 0.20F * a.wChase
                + 0.20F * a.wHelp * (0.5F + 0.5F * Mth.sin(age * 0.12F));
        pose(core, dx, y + dy, dz, a.t * 0.9F, -a.spin * 1.7F, a.spin * 0.8F,
                sxz * BASE * breath, sy * BASE * breath, sxz * BASE * breath);

        // ANNEAU : incliné, il tourne sur lui-même ; son inclinaison dérive lentement (précession)
        float gx = a.sx * 0.7F;
        float gy = CENTER_Y + a.sy * 0.7F + 0.4F * Mth.sin(a.shellT * 3.1F + seed);
        float gz = a.sz * 0.7F;
        float pulse = (1.0F + 0.04F * Mth.sin(age * 0.2F + seed)) * a.ss * Math.max(appear, 0.0F);
        pulse *= 1.0F + 0.10F * a.wChase * Mth.sin(age * 0.8F);
        float rs = BASE * pulse * (0.95F + 0.1F * p) * (1.0F + 0.08F * a.wHelp);
        pose(ring, gx, gy, gz,
                0.9F + 0.35F * Mth.sin(a.ringPrec * 1.3F + seed) + a.sz * 0.12F,
                0.0F,
                0.5F * Mth.cos(a.ringPrec + seed) - a.sx * 0.12F,
                rs, rs, rs);
        ringSpin.yRot = a.ringSpin;
    }

    private static void pose(ModelPart part, float x, float y, float z, float xRot, float yRot, float zRot,
                             float sx, float sy, float sz) {
        part.x = x;
        part.y = y;
        part.z = z;
        part.xRot = xRot;
        part.yRot = yRot;
        part.zRot = zRot;
        part.xScale = sx;
        part.yScale = sy;
        part.zScale = sz;
    }

    /** Le rendu normal ne dessine rien : tout passe par les couches. */
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int rgb) {
    }

    /** Noyau puis cube de verre par-dessus. */
    public void renderCore(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int argb) {
        core.render(poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
        body.render(poseStack, buffer, packedLight, packedOverlay, argb);
    }

    /** L'anneau (rend aussi ses 12 petits cubes). */
    public void renderGlow(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int argb) {
        ring.render(poseStack, buffer, packedLight, packedOverlay, argb);
    }
}
