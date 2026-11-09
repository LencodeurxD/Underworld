/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworldupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.underworldupdate.UnderworldUpdateMod;

@EventBusSubscriber
public class UnderworldUpdateModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UnderworldUpdateMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD = REGISTRY.register("mod",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.underworld_update.mod")).icon(() -> new ItemStack(UnderworldUpdateModItems.SWITCHENDERPERLE.get())).displayItems((parameters, tabData) -> {
				tabData.accept(UnderworldUpdateModBlocks.TRONC_DIVIN.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.GODLEAVES.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.ENDGRASS.get().asItem());
				tabData.accept(UnderworldUpdateModItems.SWITCHENDERPERLE.get());
				tabData.accept(UnderworldUpdateModItems.CLEPSIDRE.get());
				tabData.accept(UnderworldUpdateModItems.YING.get());
				tabData.accept(UnderworldUpdateModItems.YANG.get());
				tabData.accept(UnderworldUpdateModBlocks.DARKOAK.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DARKPLANKS.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DARKTRAPDOORS.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DARKFENCE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DARKSLAB.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DARKFENCEDOOR.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DARKBUTTON.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DARKPRESSUREPLATE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DARKDOORS.get().asItem());
				tabData.accept(UnderworldUpdateModItems.GRAPPIN.get());
				tabData.accept(UnderworldUpdateModItems.FIRESWORD.get());
				tabData.accept(UnderworldUpdateModBlocks.DARKSTAIRS.get().asItem());
				tabData.accept(UnderworldUpdateModItems.INKBOTTLE.get());
				tabData.accept(UnderworldUpdateModBlocks.LIGHTOAK.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.LIGHTPLANKS.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.LIGHTSTAIRS.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.LIGHTSLAB.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.LIGHTGRASS.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.LIGHTDIRT.get().asItem());
				tabData.accept(UnderworldUpdateModItems.BLACKHOLE.get());
				tabData.accept(UnderworldUpdateModItems.SOLARBEAM.get());
				tabData.accept(UnderworldUpdateModItems.PHOENIX_SPAWN_EGG.get());
				tabData.accept(UnderworldUpdateModItems.INFINITDARK_SPAWN_EGG.get());
				tabData.accept(UnderworldUpdateModItems.VILLAGERBOSS_SPAWN_EGG.get());
				tabData.accept(UnderworldUpdateModBlocks.BARRIERE_CLAIRE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.PORTILLON_CLAIR.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BOUTON_CLAIR.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.PLAQUE_DE_PRESSION_CLAIRE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.PORTE_CLAIRE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.TRAPPE_CLAIRE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.ESCALIER_DIVIN.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.DALLE_DIVINE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BARRIERE_DIVINE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.PORTILLON_DIVIN.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BOUTON_DIVIN.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.PLAQUE_DE_PRESSION_DIVINE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.PORTE_DIVINE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.TRAPPE_DIVINE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BUCHE_ECORCEE_OMBRE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BOIS_ECORCE_OMBRE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BUCHE_ECORCEE_DE_LUMIERE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BOIS_ECORCE_DE_LUMIERE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BUCHE_ECORCEE_DIVINE.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BOIS_ECORCE_DIVIN.get().asItem());
				tabData.accept(UnderworldUpdateModBlocks.BEDROCKINSTABLE.get().asItem());
			}).withSearchBar().build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			tabData.accept(UnderworldUpdateModBlocks.GODPLANKS.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKPLANKS.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKTRAPDOORS.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKFENCE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKSLAB.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKFENCEDOOR.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKBUTTON.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKPRESSUREPLATE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKDOORS.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DARKSTAIRS.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.LIGHTSTAIRS.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.LIGHTSLAB.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BARRIERE_CLAIRE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.PORTILLON_CLAIR.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BOUTON_CLAIR.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.PLAQUE_DE_PRESSION_CLAIRE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.PORTE_CLAIRE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.TRAPPE_CLAIRE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.ESCALIER_DIVIN.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.DALLE_DIVINE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BARRIERE_DIVINE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.PORTILLON_DIVIN.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BOUTON_DIVIN.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.PLAQUE_DE_PRESSION_DIVINE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.PORTE_DIVINE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.TRAPPE_DIVINE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BUCHE_ECORCEE_OMBRE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BOIS_ECORCE_OMBRE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BUCHE_ECORCEE_DE_LUMIERE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BOIS_ECORCE_DE_LUMIERE.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.BUCHE_ECORCEE_DIVINE.get().asItem());
		} else if (tabData.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			tabData.accept(UnderworldUpdateModBlocks.DARKOAK.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.LIGHTOAK.get().asItem());
			tabData.accept(UnderworldUpdateModBlocks.LIGHTGRASS.get().asItem());
		}
	}
}