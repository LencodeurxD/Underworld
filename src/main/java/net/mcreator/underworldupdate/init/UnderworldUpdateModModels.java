/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworldupdate.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.underworldupdate.client.model.Modelvillageois_boss;
import net.mcreator.underworldupdate.client.model.Modeltrou;
import net.mcreator.underworldupdate.client.model.Modelpho;
import net.mcreator.underworldupdate.client.model.Modelboss_ombre_modded_entity;

@EventBusSubscriber(Dist.CLIENT)
public class UnderworldUpdateModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(Modelvillageois_boss.LAYER_LOCATION, Modelvillageois_boss::createBodyLayer);
		event.registerLayerDefinition(Modeltrou.LAYER_LOCATION, Modeltrou::createBodyLayer);
		event.registerLayerDefinition(Modelboss_ombre_modded_entity.LAYER_LOCATION, Modelboss_ombre_modded_entity::createBodyLayer);
		event.registerLayerDefinition(Modelpho.LAYER_LOCATION, Modelpho::createBodyLayer);
	}
}