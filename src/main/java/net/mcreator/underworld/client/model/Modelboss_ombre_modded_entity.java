package net.mcreator.underworld.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.EntityModel;

import net.mcreator.underworldupdate.client.model.Modelboss_ombre_modded_entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class Modelboss_ombre_modded_entity<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("underworld", "modelboss_ombre_modded_entity"), "main");
	public final ModelPart body;
	public final ModelPart astarm;
	public final ModelPart baseastarm;
	public final ModelPart middleastarm;
	public final ModelPart finishastarm;
	public final ModelPart bndarm;
	public final ModelPart basebndarm;
	public final ModelPart middlebndarm;
	public final ModelPart finishbndarm;
	public final ModelPart crdarm;
	public final ModelPart basecrdarm;
	public final ModelPart middlecrdarm;
	public final ModelPart finishcrdarm;
	public final ModelPart dtharm;
	public final ModelPart basedtharm;
	public final ModelPart middledtharm;
	public final ModelPart finishdtharm;
	public final ModelPart etharm;
	public final ModelPart baseetharm;
	public final ModelPart middleetharm;
	public final ModelPart finishetharm;
	public final ModelPart ftharm;
	public final ModelPart baseftharm;
	public final ModelPart middleftharm;
	public final ModelPart finishftharm;
	public final ModelPart gtharm;
	public final ModelPart basegtharm;
	public final ModelPart middlegtharm;
	public final ModelPart finishgtharm;
	public final ModelPart htharm;
	public final ModelPart basehtharm;
	public final ModelPart middlehtharm;
	public final ModelPart finishhtharm;

	public Modelboss_ombre_modded_entity(ModelPart root) {
		this.body = root.getChild("body");
		this.astarm = this.body.getChild("astarm");
		this.baseastarm = this.astarm.getChild("baseastarm");
		this.middleastarm = this.baseastarm.getChild("middleastarm");
		this.finishastarm = this.middleastarm.getChild("finishastarm");
		this.bndarm = this.body.getChild("bndarm");
		this.basebndarm = this.bndarm.getChild("basebndarm");
		this.middlebndarm = this.basebndarm.getChild("middlebndarm");
		this.finishbndarm = this.middlebndarm.getChild("finishbndarm");
		this.crdarm = this.body.getChild("crdarm");
		this.basecrdarm = this.crdarm.getChild("basecrdarm");
		this.middlecrdarm = this.basecrdarm.getChild("middlecrdarm");
		this.finishcrdarm = this.middlecrdarm.getChild("finishcrdarm");
		this.dtharm = this.body.getChild("dtharm");
		this.basedtharm = this.dtharm.getChild("basedtharm");
		this.middledtharm = this.basedtharm.getChild("middledtharm");
		this.finishdtharm = this.middledtharm.getChild("finishdtharm");
		this.etharm = this.body.getChild("etharm");
		this.baseetharm = this.etharm.getChild("baseetharm");
		this.middleetharm = this.baseetharm.getChild("middleetharm");
		this.finishetharm = this.middleetharm.getChild("finishetharm");
		this.ftharm = this.body.getChild("ftharm");
		this.baseftharm = this.ftharm.getChild("baseftharm");
		this.middleftharm = this.baseftharm.getChild("middleftharm");
		this.finishftharm = this.middleftharm.getChild("finishftharm");
		this.gtharm = this.body.getChild("gtharm");
		this.basegtharm = this.gtharm.getChild("basegtharm");
		this.middlegtharm = this.basegtharm.getChild("middlegtharm");
		this.finishgtharm = this.middlegtharm.getChild("finishgtharm");
		this.htharm = this.body.getChild("htharm");
		this.basehtharm = this.htharm.getChild("basehtharm");
		this.middlehtharm = this.basehtharm.getChild("middlehtharm");
		this.finishhtharm = this.middlehtharm.getChild("finishhtharm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 0).addBox(-52.0F, -1.0F, -2.0F, 44.0F, 1.0F, 44.0F, new CubeDeformation(0.0F)), PartPose.offset(30.0F, 24.0F, -20.0F));
		PartDefinition astarm = body.addOrReplaceChild("astarm", CubeListBuilder.create(), PartPose.offsetAndRotation(-22.0F, -1.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));
		PartDefinition baseastarm = astarm.addOrReplaceChild("baseastarm", CubeListBuilder.create().texOffs(64, 111).addBox(-1.0F, -21.0F, -1.0F, 2.0F, 21.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		PartDefinition middleastarm = baseastarm.addOrReplaceChild("middleastarm", CubeListBuilder.create(), PartPose.offset(0.0F, -20.25F, 0.0F));
		PartDefinition cube_r1 = middleastarm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -15.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.25F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition finishastarm = middleastarm.addOrReplaceChild("finishastarm", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0556F, -10.9958F));
		PartDefinition cube_r2 = finishastarm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -16.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.3056F, 0.9958F, 0.5236F, 0.0F, 0.0F));
		PartDefinition bndarm = body.addOrReplaceChild("bndarm", CubeListBuilder.create(), PartPose.offsetAndRotation(-38.0F, -1.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));
		PartDefinition basebndarm = bndarm.addOrReplaceChild("basebndarm", CubeListBuilder.create().texOffs(64, 111).addBox(-1.0F, -21.0F, -1.0F, 2.0F, 21.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		PartDefinition middlebndarm = basebndarm.addOrReplaceChild("middlebndarm", CubeListBuilder.create(), PartPose.offset(0.0F, -20.25F, 0.0F));
		PartDefinition cube_r3 = middlebndarm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -15.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.25F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition finishbndarm = middlebndarm.addOrReplaceChild("finishbndarm", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0556F, -10.9958F));
		PartDefinition cube_r4 = finishbndarm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -16.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.3056F, 0.9958F, 0.5236F, 0.0F, 0.0F));
		PartDefinition crdarm = body.addOrReplaceChild("crdarm", CubeListBuilder.create(), PartPose.offset(-22.0F, -1.0F, 39.0F));
		PartDefinition basecrdarm = crdarm.addOrReplaceChild("basecrdarm", CubeListBuilder.create().texOffs(64, 111).addBox(-1.0F, -21.0F, -1.0F, 2.0F, 21.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		PartDefinition middlecrdarm = basecrdarm.addOrReplaceChild("middlecrdarm", CubeListBuilder.create(), PartPose.offset(0.0F, -20.25F, 0.0F));
		PartDefinition cube_r5 = middlecrdarm.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -15.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.25F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition finishcrdarm = middlecrdarm.addOrReplaceChild("finishcrdarm", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0556F, -10.9958F));
		PartDefinition cube_r6 = finishcrdarm.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -16.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.3056F, 0.9958F, 0.5236F, 0.0F, 0.0F));
		PartDefinition dtharm = body.addOrReplaceChild("dtharm", CubeListBuilder.create(), PartPose.offset(-38.0F, -1.0F, 39.0F));
		PartDefinition basedtharm = dtharm.addOrReplaceChild("basedtharm", CubeListBuilder.create().texOffs(64, 111).addBox(-1.0F, -21.0F, -1.0F, 2.0F, 21.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		PartDefinition middledtharm = basedtharm.addOrReplaceChild("middledtharm", CubeListBuilder.create(), PartPose.offset(0.0F, -20.25F, 0.0F));
		PartDefinition cube_r7 = middledtharm.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -15.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.25F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition finishdtharm = middledtharm.addOrReplaceChild("finishdtharm", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0556F, -10.9958F));
		PartDefinition cube_r8 = finishdtharm.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -16.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.3056F, 0.9958F, 0.5236F, 0.0F, 0.0F));
		PartDefinition etharm = body.addOrReplaceChild("etharm", CubeListBuilder.create(), PartPose.offsetAndRotation(-49.0F, -1.0F, 12.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition baseetharm = etharm.addOrReplaceChild("baseetharm", CubeListBuilder.create().texOffs(64, 111).addBox(-1.0F, -21.0F, -1.0F, 2.0F, 21.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		PartDefinition middleetharm = baseetharm.addOrReplaceChild("middleetharm", CubeListBuilder.create(), PartPose.offset(0.0F, -20.25F, 0.0F));
		PartDefinition cube_r9 = middleetharm.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -15.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.25F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition finishetharm = middleetharm.addOrReplaceChild("finishetharm", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0556F, -10.9958F));
		PartDefinition cube_r10 = finishetharm.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -16.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.3056F, 0.9958F, 0.5236F, 0.0F, 0.0F));
		PartDefinition ftharm = body.addOrReplaceChild("ftharm", CubeListBuilder.create(), PartPose.offsetAndRotation(-49.0F, -1.0F, 28.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition baseftharm = ftharm.addOrReplaceChild("baseftharm", CubeListBuilder.create().texOffs(64, 111).addBox(-1.0F, -21.0F, -1.0F, 2.0F, 21.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		PartDefinition middleftharm = baseftharm.addOrReplaceChild("middleftharm", CubeListBuilder.create(), PartPose.offset(0.0F, -20.25F, 0.0F));
		PartDefinition cube_r11 = middleftharm.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -15.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.25F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition finishftharm = middleftharm.addOrReplaceChild("finishftharm", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0556F, -10.9958F));
		PartDefinition cube_r12 = finishftharm.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -16.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.3056F, 0.9958F, 0.5236F, 0.0F, 0.0F));
		PartDefinition gtharm = body.addOrReplaceChild("gtharm", CubeListBuilder.create(), PartPose.offsetAndRotation(-11.0F, -1.0F, 12.0F, 0.0F, 1.5708F, 0.0F));
		PartDefinition basegtharm = gtharm.addOrReplaceChild("basegtharm", CubeListBuilder.create().texOffs(64, 111).addBox(-1.0F, -21.0F, -1.0F, 2.0F, 21.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		PartDefinition middlegtharm = basegtharm.addOrReplaceChild("middlegtharm", CubeListBuilder.create(), PartPose.offset(0.0F, -20.25F, 0.0F));
		PartDefinition cube_r13 = middlegtharm.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -15.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.25F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition finishgtharm = middlegtharm.addOrReplaceChild("finishgtharm", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0556F, -10.9958F));
		PartDefinition cube_r14 = finishgtharm.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -16.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.3056F, 0.9958F, 0.5236F, 0.0F, 0.0F));
		PartDefinition htharm = body.addOrReplaceChild("htharm", CubeListBuilder.create(), PartPose.offsetAndRotation(-11.0F, -1.0F, 28.0F, 0.0F, 1.5708F, 0.0F));
		PartDefinition basehtharm = htharm.addOrReplaceChild("basehtharm", CubeListBuilder.create().texOffs(64, 111).addBox(-1.0F, -21.0F, -1.0F, 2.0F, 21.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		PartDefinition middlehtharm = basehtharm.addOrReplaceChild("middlehtharm", CubeListBuilder.create(), PartPose.offset(0.0F, -20.25F, 0.0F));
		PartDefinition cube_r15 = middlehtharm.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -15.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.25F, 0.0F, -0.6109F, 0.0F, 0.0F));
		PartDefinition finishhtharm = middlehtharm.addOrReplaceChild("finishhtharm", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0556F, -10.9958F));
		PartDefinition cube_r16 = finishhtharm.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -16.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.3056F, 0.9958F, 0.5236F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
	}
}