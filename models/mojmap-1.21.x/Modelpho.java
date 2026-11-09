// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelpho<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "pho"),
			"main");
	private final ModelPart phoenix_body;
	private final ModelPart left_wings;
	private final ModelPart finsh_wings_left;
	private final ModelPart base_wings_left;
	private final ModelPart right_wings;
	private final ModelPart finish_wings_right;
	private final ModelPart base_wings_right;
	private final ModelPart body_no_wings;
	private final ModelPart right_leg;
	private final ModelPart right_down_leg_feet;
	private final ModelPart right_feet;
	private final ModelPart tail;
	private final ModelPart up_tail;
	private final ModelPart down_tail;
	private final ModelPart left_leg;
	private final ModelPart left_down_leg_feet;
	private final ModelPart left_feet;
	private final ModelPart head_group;
	private final ModelPart eye_group;
	private final ModelPart nose;
	private final ModelPart base_body;

	public Modelpho(ModelPart root) {
		this.phoenix_body = root.getChild("phoenix_body");
		this.left_wings = this.phoenix_body.getChild("left_wings");
		this.finsh_wings_left = this.left_wings.getChild("finsh_wings_left");
		this.base_wings_left = this.left_wings.getChild("base_wings_left");
		this.right_wings = this.phoenix_body.getChild("right_wings");
		this.finish_wings_right = this.right_wings.getChild("finish_wings_right");
		this.base_wings_right = this.right_wings.getChild("base_wings_right");
		this.body_no_wings = this.phoenix_body.getChild("body_no_wings");
		this.right_leg = this.body_no_wings.getChild("right_leg");
		this.right_down_leg_feet = this.right_leg.getChild("right_down_leg_feet");
		this.right_feet = this.right_down_leg_feet.getChild("right_feet");
		this.tail = this.body_no_wings.getChild("tail");
		this.up_tail = this.tail.getChild("up_tail");
		this.down_tail = this.tail.getChild("down_tail");
		this.left_leg = this.body_no_wings.getChild("left_leg");
		this.left_down_leg_feet = this.left_leg.getChild("left_down_leg_feet");
		this.left_feet = this.left_down_leg_feet.getChild("left_feet");
		this.head_group = this.body_no_wings.getChild("head_group");
		this.eye_group = this.head_group.getChild("eye_group");
		this.nose = this.head_group.getChild("nose");
		this.base_body = this.body_no_wings.getChild("base_body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition phoenix_body = partdefinition.addOrReplaceChild("phoenix_body", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, -7.0F));

		PartDefinition left_wings = phoenix_body.addOrReplaceChild("left_wings", CubeListBuilder.create(),
				PartPose.offset(-4.9F, 3.0F, 5.5F));

		PartDefinition finsh_wings_left = left_wings.addOrReplaceChild("finsh_wings_left", CubeListBuilder.create(),
				PartPose.offset(-14.769F, 7.9693F, 10.6F));

		PartDefinition finishleftwings_r1 = finsh_wings_left.addOrReplaceChild("finishleftwings_r1",
				CubeListBuilder.create().texOffs(0, 51).addBox(-10.0F, -4.0F, -6.0F, 19.0F, 0.0F, 12.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.584F, -1.2283F, 3.0691F, 0.0F, 1.2654F, -0.2618F));

		PartDefinition finishleftarmwings_r1 = finsh_wings_left.addOrReplaceChild("finishleftarmwings_r1",
				CubeListBuilder.create().texOffs(86, 67).addBox(-11.5F, -5.0F, -1.0F, 19.0F, 2.0F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.2654F, -0.2618F));

		PartDefinition base_wings_left = left_wings.addOrReplaceChild("base_wings_left", CubeListBuilder.create(),
				PartPose.offset(-5.4F, 5.7F, -1.4F));

		PartDefinition baseleftwings_r1 = base_wings_left.addOrReplaceChild("baseleftwings_r1",
				CubeListBuilder.create().texOffs(0, 39).addBox(-9.5F, -4.0F, -6.0F, 19.0F, 0.0F, 12.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(2.9791F, -0.7983F, 4.4047F, 0.0F, 0.6109F, -0.2618F));

		PartDefinition baseleftwingsarm_r1 = base_wings_left.addOrReplaceChild("baseleftwingsarm_r1",
				CubeListBuilder.create().texOffs(0, 72).addBox(-9.5F, -5.0F, -1.0F, 19.0F, 2.0F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.6109F, -0.2618F));

		PartDefinition right_wings = phoenix_body.addOrReplaceChild("right_wings", CubeListBuilder.create(),
				PartPose.offset(4.9F, 3.0F, 5.5F));

		PartDefinition finish_wings_right = right_wings.addOrReplaceChild("finish_wings_right",
				CubeListBuilder.create(), PartPose.offset(15.169F, 8.3692F, 7.0F));

		PartDefinition finishrightwings_r1 = finish_wings_right.addOrReplaceChild("finishrightwings_r1",
				CubeListBuilder.create().texOffs(38, 39).addBox(-9.0F, -4.0F, -6.0F, 19.0F, 0.0F, 12.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.584F, -1.2283F, 3.0691F, 0.0F, -1.2654F, 0.2618F));

		PartDefinition finishrightarmwings_r1 = finish_wings_right.addOrReplaceChild("finishrightarmwings_r1",
				CubeListBuilder.create().texOffs(66, 71).addBox(-7.5F, -5.0F, -1.0F, 19.0F, 2.0F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.2654F, 0.2618F));

		PartDefinition base_wings_right = right_wings.addOrReplaceChild("base_wings_right", CubeListBuilder.create(),
				PartPose.offset(5.4F, 5.7F, -1.4F));

		PartDefinition baserightwings_r1 = base_wings_right.addOrReplaceChild("baserightwings_r1",
				CubeListBuilder.create().texOffs(56, 21).addBox(-9.5F, -4.0F, -6.0F, 19.0F, 0.0F, 12.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.9791F, -0.7983F, 4.4047F, 0.0F, -0.6109F, 0.2618F));

		PartDefinition baserightarmwings_r1 = base_wings_right.addOrReplaceChild("baserightarmwings_r1",
				CubeListBuilder.create().texOffs(86, 63).addBox(-9.5F, -5.0F, -1.0F, 19.0F, 2.0F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.6109F, 0.2618F));

		PartDefinition body_no_wings = phoenix_body.addOrReplaceChild("body_no_wings", CubeListBuilder.create(),
				PartPose.offset(-0.4F, 2.2F, 6.0F));

		PartDefinition right_leg = body_no_wings.addOrReplaceChild("right_leg", CubeListBuilder.create(),
				PartPose.offset(3.0F, 25.0F, 0.0F));

		PartDefinition upleg_r1 = right_leg.addOrReplaceChild("upleg_r1",
				CubeListBuilder.create().texOffs(106, 16).addBox(-2.5F, -7.5F, -2.5F, 5.0F, 7.0F, 5.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.2F, -7.2F, 3.2F, 0.2618F, -0.1309F, -0.0873F));

		PartDefinition right_down_leg_feet = right_leg.addOrReplaceChild("right_down_leg_feet",
				CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightdownleg_r1 = right_down_leg_feet.addOrReplaceChild("rightdownleg_r1",
				CubeListBuilder.create().texOffs(32, 0).addBox(-1.5F, -7.5F, -1.5F, 3.0F, 7.0F, 3.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.4F, -2.8F, 2.1F, -0.1745F, 0.0F, 0.0F));

		PartDefinition right_feet = right_down_leg_feet.addOrReplaceChild("right_feet", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightclawright_r1 = right_feet.addOrReplaceChild("rightclawright_r1",
				CubeListBuilder.create().texOffs(113, 0).addBox(-0.5F, -4.5F, -2.0F, 1.0F, 1.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(2.9F, 0.0F, 0.0F, 0.0436F, -0.3054F, 0.0F));

		PartDefinition midleclawright_r1 = right_feet.addOrReplaceChild(
				"midleclawright_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -4.5F, -2.0F, 1.0F, 1.0F,
						4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.4F, 0.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition leftclawright_r1 = right_feet.addOrReplaceChild("leftclawright_r1",
				CubeListBuilder.create().texOffs(85, 5).addBox(-0.5F, -4.5F, -2.0F, 1.0F, 1.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0436F, 0.3054F, 0.0F));

		PartDefinition tail = body_no_wings.addOrReplaceChild("tail", CubeListBuilder.create(),
				PartPose.offset(0.4F, 7.5F, 3.0F));

		PartDefinition basetail_r1 = tail
				.addOrReplaceChild("basetail_r1",
						CubeListBuilder.create().texOffs(42, 0).addBox(-6.5F, -6.0F, -8.5F, 13.0F, 4.0F, 17.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 6.3F, 4.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition up_tail = tail.addOrReplaceChild("up_tail", CubeListBuilder.create(),
				PartPose.offset(0.0F, 8.0F, 14.0F));

		PartDefinition uptail_r1 = up_tail.addOrReplaceChild(
				"uptail_r1", CubeListBuilder.create().texOffs(70, 33).addBox(-5.5F, -3.0086F, -8.8695F, 11.0F, 0.0F,
						18.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition down_tail = tail.addOrReplaceChild("down_tail", CubeListBuilder.create(),
				PartPose.offset(0.0F, 4.3F, 4.5F));

		PartDefinition downtail_r1 = down_tail
				.addOrReplaceChild("downtail_r1",
						CubeListBuilder.create().texOffs(28, 59).addBox(-4.0F, -4.0F, 2.0F, 8.0F, 0.0F, 22.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5672F, 0.0F, 0.0F));

		PartDefinition left_leg = body_no_wings.addOrReplaceChild("left_leg", CubeListBuilder.create(),
				PartPose.offset(-5.1F, 25.0F, 0.0F));

		PartDefinition upleg_r2 = left_leg.addOrReplaceChild("upleg_r2",
				CubeListBuilder.create().texOffs(66, 59).addBox(-2.5F, -7.5F, -2.5F, 5.0F, 7.0F, 5.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.7F, -7.2F, 3.2F, 0.2618F, 0.1309F, 0.0873F));

		PartDefinition left_down_leg_feet = left_leg.addOrReplaceChild("left_down_leg_feet", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftdownleg_r1 = left_down_leg_feet.addOrReplaceChild("leftdownleg_r1",
				CubeListBuilder.create().texOffs(44, 0).addBox(-1.5F, -7.5F, -1.5F, 3.0F, 7.0F, 3.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.5F, -2.8F, 2.1F, -0.1745F, 0.0F, 0.0F));

		PartDefinition left_feet = left_down_leg_feet.addOrReplaceChild("left_feet", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightclawleft_r1 = left_feet.addOrReplaceChild("rightclawleft_r1",
				CubeListBuilder.create().texOffs(85, 0).addBox(-0.5F, -4.5F, -2.0F, 1.0F, 1.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(2.9F, 0.0F, 0.0F, 0.0436F, -0.3054F, 0.0F));

		PartDefinition midleclawleft_r1 = left_feet
				.addOrReplaceChild("midleclawleft_r1",
						CubeListBuilder.create().texOffs(0, 5).addBox(-0.5F, -4.5F, -2.0F, 1.0F, 1.0F, 4.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(1.5F, 0.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition leftclawleft_r1 = left_feet.addOrReplaceChild("leftclawleft_r1",
				CubeListBuilder.create().texOffs(113, 5).addBox(-0.5F, -4.5F, -2.0F, 1.0F, 1.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0436F, 0.3054F, 0.0F));

		PartDefinition head_group = body_no_wings.addOrReplaceChild("head_group", CubeListBuilder.create(),
				PartPose.offset(0.4F, -2.2F, -6.0F));

		PartDefinition chin_r1 = head_group
				.addOrReplaceChild("chin_r1",
						CubeListBuilder.create().texOffs(0, 63).addBox(-5.5F, -5.8805F, -2.9914F, 11.0F, 4.0F, 5.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 1.0F, 1.3F, 0.1309F, 0.0F, 0.0F));

		PartDefinition head_r1 = head_group
				.addOrReplaceChild("head_r1",
						CubeListBuilder.create().texOffs(85, 0).addBox(-4.5F, -7.0F, -5.0F, 9.0F, 6.0F, 10.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, -1.0F, 2.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition eye_group = head_group.addOrReplaceChild("eye_group", CubeListBuilder.create().texOffs(88, 51)
				.addBox(-4.0F, -6.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lefteyebrow_r1 = eye_group.addOrReplaceChild("lefteyebrow_r1",
				CubeListBuilder.create().texOffs(21, 22).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 2.0F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.0F, -1.9F, -4.0F, 0.0436F, 0.0F, 0.1745F));

		PartDefinition righteyebrow_r1 = eye_group.addOrReplaceChild("righteyebrow_r1",
				CubeListBuilder.create().texOffs(42, 10).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 2.0F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.0F, -1.9F, -4.0F, 0.0436F, 0.0F, -0.1745F));

		PartDefinition nose = head_group.addOrReplaceChild("nose",
				CubeListBuilder.create().texOffs(28, 67)
						.addBox(-1.0F, -5.5F, -8.5F, 2.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(100, 71)
						.addBox(-1.5F, -6.1F, -4.6F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 1.1F, -5.4F));

		PartDefinition base_body = body_no_wings.addOrReplaceChild("base_body", CubeListBuilder.create(),
				PartPose.offset(0.4F, 8.8F, 2.0F));

		PartDefinition fin_r1 = base_body.addOrReplaceChild("fin_r1",
				CubeListBuilder.create().texOffs(50, 28).addBox(0.0F, -8.0F, -11.5F, 0.0F, 8.0F, 23.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -7.4F, -3.0F, -1.1345F, 0.0F, 0.0F));

		PartDefinition neck_r1 = base_body.addOrReplaceChild("neck_r1",
				CubeListBuilder.create().texOffs(0, 22).addBox(-3.5F, -7.5F, -3.5F, 7.0F, 7.0F, 7.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -8.0F, -4.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition collar_r1 = base_body.addOrReplaceChild("collar_r1",
				CubeListBuilder.create().texOffs(28, 21).addBox(-6.5F, -6.0F, -7.0F, 13.0F, 4.0F, 14.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -6.0F, -3.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition belly_r1 = base_body
				.addOrReplaceChild("belly_r1",
						CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -10.0F, -5.0F, 11.0F, 12.0F, 10.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.48F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		phoenix_body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}