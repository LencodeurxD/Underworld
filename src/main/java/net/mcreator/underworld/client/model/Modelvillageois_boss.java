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

import net.mcreator.underworldupdate.client.model.Modelvillageois_boss;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class Modelvillageois_boss<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("underworld", "modelvillageois_boss"), "main");
	public final ModelPart root;
	public final ModelPart body;
	public final ModelPart head;
	public final ModelPart helmet;
	public final ModelPart brim;
	public final ModelPart nose;
	public final ModelPart arms;
	public final ModelPart bras_hg_1;
	public final ModelPart bras_hg_2;
	public final ModelPart bras_hg_3;
	public final ModelPart bras_hd_1;
	public final ModelPart bras_hd_2;
	public final ModelPart bras_hd_3;
	public final ModelPart bras_bg_1;
	public final ModelPart bras_bg_2;
	public final ModelPart bras_bg_3;
	public final ModelPart bras_bd_1;
	public final ModelPart bras_bd_2;
	public final ModelPart bras_bd_3;
	public final ModelPart RightLeg;
	public final ModelPart LeftLeg;

	public Modelvillageois_boss(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.head = this.body.getChild("head");
		this.helmet = this.head.getChild("helmet");
		this.brim = this.head.getChild("brim");
		this.nose = this.head.getChild("nose");
		this.arms = this.body.getChild("arms");
		this.bras_hg_1 = this.body.getChild("bras_hg_1");
		this.bras_hg_2 = this.bras_hg_1.getChild("bras_hg_2");
		this.bras_hg_3 = this.bras_hg_2.getChild("bras_hg_3");
		this.bras_hd_1 = this.body.getChild("bras_hd_1");
		this.bras_hd_2 = this.bras_hd_1.getChild("bras_hd_2");
		this.bras_hd_3 = this.bras_hd_2.getChild("bras_hd_3");
		this.bras_bg_1 = this.body.getChild("bras_bg_1");
		this.bras_bg_2 = this.bras_bg_1.getChild("bras_bg_2");
		this.bras_bg_3 = this.bras_bg_2.getChild("bras_bg_3");
		this.bras_bd_1 = this.body.getChild("bras_bd_1");
		this.bras_bd_2 = this.bras_bd_1.getChild("bras_bd_2");
		this.bras_bd_3 = this.bras_bd_2.getChild("bras_bd_3");
		this.RightLeg = this.root.getChild("RightLeg");
		this.LeftLeg = this.root.getChild("LeftLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition body = root.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 38).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));
		PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition brim = head.addOrReplaceChild("brim", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));
		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
		PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(44, 22)
				.addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition bras_hg_1 = body.addOrReplaceChild("bras_hg_1", CubeListBuilder.create().texOffs(64, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(9.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.9599F));
		PartDefinition bras_hg_2 = bras_hg_1.addOrReplaceChild("bras_hg_2", CubeListBuilder.create().texOffs(72, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.5236F));
		PartDefinition bras_hg_3 = bras_hg_2.addOrReplaceChild("bras_hg_3", CubeListBuilder.create().texOffs(80, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 25.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.4363F, 0.0F, -0.9599F));
		PartDefinition bras_hd_1 = body.addOrReplaceChild("bras_hd_1", CubeListBuilder.create().texOffs(64, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-9.0F, -10.0F, 0.0F, 0.0F, 0.0F, 0.9599F));
		PartDefinition bras_hd_2 = bras_hd_1.addOrReplaceChild("bras_hd_2", CubeListBuilder.create().texOffs(72, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, -0.5236F));
		PartDefinition bras_hd_3 = bras_hd_2.addOrReplaceChild("bras_hd_3", CubeListBuilder.create().texOffs(80, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 25.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.4363F, 0.0F, 0.9599F));
		PartDefinition bras_bg_1 = body.addOrReplaceChild("bras_bg_1", CubeListBuilder.create().texOffs(64, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(9.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.6109F));
		PartDefinition bras_bg_2 = bras_bg_1.addOrReplaceChild("bras_bg_2", CubeListBuilder.create().texOffs(72, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.1745F));
		PartDefinition bras_bg_3 = bras_bg_2.addOrReplaceChild("bras_bg_3", CubeListBuilder.create().texOffs(80, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 25.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.3491F, 0.0F, -0.4363F));
		PartDefinition bras_bd_1 = body.addOrReplaceChild("bras_bd_1", CubeListBuilder.create().texOffs(64, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-9.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.6109F));
		PartDefinition bras_bd_2 = bras_bd_1.addOrReplaceChild("bras_bd_2", CubeListBuilder.create().texOffs(72, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, -0.1745F));
		PartDefinition bras_bd_3 = bras_bd_2.addOrReplaceChild("bras_bd_3", CubeListBuilder.create().texOffs(80, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 25.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.3491F, 0.0F, 0.4363F));
		PartDefinition RightLeg = root.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 0.0F));
		PartDefinition LeftLeg = root.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -12.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
	}
}