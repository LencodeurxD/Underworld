package net.mcreator.underworldupdate.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;

import net.mcreator.underworldupdate.entity.PhoenixEntity;
import net.mcreator.underworldupdate.client.model.animations.phoAnimation;
import net.mcreator.underworldupdate.client.model.Modelpho;

public class PhoenixRenderer extends MobRenderer<PhoenixEntity, Modelpho<PhoenixEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("underworld_update:textures/entities/phenix_floraison.png");

	public PhoenixRenderer(EntityRendererProvider.Context context) {
		super(context, new AnimatedModel(context.bakeLayer(Modelpho.LAYER_LOCATION)), 0.4f);
	}

	@Override
	public ResourceLocation getTextureLocation(PhoenixEntity entity) {
		return entityTexture;
	}

	private static final class AnimatedModel extends Modelpho<PhoenixEntity> {
		private final ModelPart root;
		private final HierarchicalModel animator = new HierarchicalModel<PhoenixEntity>() {
			@Override
			public ModelPart root() {
				return root;
			}

			@Override
			public void setupAnim(PhoenixEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
				this.root().getAllParts().forEach(ModelPart::resetPose);
				this.animate(entity.animationState0, phoAnimation.fly, ageInTicks, 1f);
			}
		};

		public AnimatedModel(ModelPart root) {
			super(root);
			this.root = root;
		}

		@Override
		public void setupAnim(PhoenixEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}
}