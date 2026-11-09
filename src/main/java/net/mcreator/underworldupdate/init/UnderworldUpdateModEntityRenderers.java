/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworldupdate.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import net.mcreator.underworldupdate.client.renderer.VillagerbossRenderer;
import net.mcreator.underworldupdate.client.renderer.TrouRenderer;
import net.mcreator.underworldupdate.client.renderer.PhoenixRenderer;
import net.mcreator.underworldupdate.client.renderer.InfinitdarkRenderer;

@EventBusSubscriber(Dist.CLIENT)
public class UnderworldUpdateModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(UnderworldUpdateModEntities.TETE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(UnderworldUpdateModEntities.TROU.get(), TrouRenderer::new);
		event.registerEntityRenderer(UnderworldUpdateModEntities.PHOENIX.get(), PhoenixRenderer::new);
		event.registerEntityRenderer(UnderworldUpdateModEntities.INFINITDARK.get(), InfinitdarkRenderer::new);
		event.registerEntityRenderer(UnderworldUpdateModEntities.INFINITDARK_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(UnderworldUpdateModEntities.VILLAGERBOSS.get(), VillagerbossRenderer::new);
	}
}