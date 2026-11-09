package net.mcreator.underworldupdate.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;

import net.mcreator.underworldupdate.entity.InfinitdarkEntity;
import net.mcreator.underworldupdate.client.model.animations.boss_ombre_modded_entityAnimation;
import net.mcreator.underworldupdate.client.model.Modelboss_ombre_modded_entity;

import com.mojang.blaze3d.vertex.PoseStack;

public class InfinitdarkRenderer extends MobRenderer<InfinitdarkEntity, Modelboss_ombre_modded_entity<InfinitdarkEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("underworld_update:textures/entities/tenebres_infinies_veines.png");

	public InfinitdarkRenderer(EntityRendererProvider.Context context) {
		super(context, new AnimatedModel(context.bakeLayer(Modelboss_ombre_modded_entity.LAYER_LOCATION)), 0.5f);
	}

	@Override
	protected void scale(InfinitdarkEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(2f, 2f, 2f);
	}

	@Override
	public ResourceLocation getTextureLocation(InfinitdarkEntity entity) {
		return entityTexture;
	}

	private static final class AnimatedModel extends Modelboss_ombre_modded_entity<InfinitdarkEntity> {
		private final ModelPart root;
		private final HierarchicalModel animator = new HierarchicalModel<InfinitdarkEntity>() {
			@Override
			public ModelPart root() {
				return root;
			}

			@Override
			public void setupAnim(InfinitdarkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
				this.root().getAllParts().forEach(ModelPart::resetPose);
				this.animate(entity.animationState0, boss_ombre_modded_entityAnimation.balancement_bras, ageInTicks, 1f);
			}
		};

		public AnimatedModel(ModelPart root) {
			super(root);
			this.root = root;
		}

		@Override
		public void setupAnim(InfinitdarkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}
}