package net.mcreator.underworldupdate.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.Minecraft;

import net.mcreator.underworldupdate.entity.VillagerbossEntity;
import net.mcreator.underworldupdate.client.model.animations.villageois_bossAnimation;
import net.mcreator.underworldupdate.client.model.Modelvillageois_boss;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class VillagerbossRenderer extends MobRenderer<VillagerbossEntity, Modelvillageois_boss<VillagerbossEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("underworld_update:textures/entities/villageois_boss.png");

	public VillagerbossRenderer(EntityRendererProvider.Context context) {
		super(context, new AnimatedModel(context.bakeLayer(Modelvillageois_boss.LAYER_LOCATION)), 0.5f);
		this.addLayer(new RenderLayer<VillagerbossEntity, Modelvillageois_boss<VillagerbossEntity>>(this) {
			final ResourceLocation LAYER_TEXTURE = ResourceLocation.parse("underworld_update:textures/entities/eyes.png");
			final EntityModel LAYER_MODEL = new Modelvillageois_boss(Minecraft.getInstance().getEntityModels().bakeLayer(Modelvillageois_boss.LAYER_LOCATION));

			@Override
			public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, VillagerbossEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(LAYER_TEXTURE));
				this.getParentModel().copyPropertiesTo(LAYER_MODEL);
				LAYER_MODEL.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
				LAYER_MODEL.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
				LAYER_MODEL.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
			}
		});
	}

	@Override
	protected void scale(VillagerbossEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(1.2f, 1.2f, 1.2f);
	}

	@Override
	public ResourceLocation getTextureLocation(VillagerbossEntity entity) {
		return entityTexture;
	}

	private static final class AnimatedModel extends Modelvillageois_boss<VillagerbossEntity> {
		private final ModelPart root;
		private final HierarchicalModel animator = new HierarchicalModel<VillagerbossEntity>() {
			@Override
			public ModelPart root() {
				return root;
			}

			@Override
			public void setupAnim(VillagerbossEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
				this.root().getAllParts().forEach(ModelPart::resetPose);
				this.animate(entity.animationState0, villageois_bossAnimation.idle, ageInTicks, 1f);
				this.animate(entity.animationState1, villageois_bossAnimation.teleportation, ageInTicks, 1f);
				this.animate(entity.animationState2, villageois_bossAnimation.rafale_lames, ageInTicks, 1f);
				this.animate(entity.animationState3, villageois_bossAnimation.invocation, ageInTicks, 1f);
				this.animate(entity.animationState4, villageois_bossAnimation.attaque_balayage, ageInTicks, 1f);
				this.animate(entity.animationState5, villageois_bossAnimation.garde, ageInTicks, 1f);
			}
		};

		public AnimatedModel(ModelPart root) {
			super(root);
			this.root = root;
		}

		@Override
		public void setupAnim(VillagerbossEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}
}