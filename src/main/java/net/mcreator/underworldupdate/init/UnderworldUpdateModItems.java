/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworldupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.BlockItem;

import net.mcreator.underworldupdate.item.*;
import net.mcreator.underworldupdate.UnderworldUpdateMod;

public class UnderworldUpdateModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(UnderworldUpdateMod.MODID);
	public static final DeferredItem<Item> TRONC_DIVIN;
	public static final DeferredItem<Item> GODPLANKS;
	public static final DeferredItem<Item> GODLEAVES;
	public static final DeferredItem<Item> ENDGRASS;
	public static final DeferredItem<Item> SWITCHENDERPERLE;
	public static final DeferredItem<Item> CLEPSIDRE;
	public static final DeferredItem<Item> YING;
	public static final DeferredItem<Item> YANG;
	public static final DeferredItem<Item> DARKOAK;
	public static final DeferredItem<Item> DARKPLANKS;
	public static final DeferredItem<Item> DARKTRAPDOORS;
	public static final DeferredItem<Item> DARKFENCE;
	public static final DeferredItem<Item> DARKSLAB;
	public static final DeferredItem<Item> DARKFENCEDOOR;
	public static final DeferredItem<Item> DARKBUTTON;
	public static final DeferredItem<Item> DARKPRESSUREPLATE;
	public static final DeferredItem<Item> DARKDOORS;
	public static final DeferredItem<Item> GRAPPIN;
	public static final DeferredItem<Item> FIRESWORD;
	public static final DeferredItem<Item> DARKSTAIRS;
	public static final DeferredItem<Item> INKBOTTLE;
	public static final DeferredItem<Item> LIGHTOAK;
	public static final DeferredItem<Item> LIGHTPLANKS;
	public static final DeferredItem<Item> LIGHTSTAIRS;
	public static final DeferredItem<Item> LIGHTSLAB;
	public static final DeferredItem<Item> LIGHTGRASS;
	public static final DeferredItem<Item> LIGHTDIRT;
	public static final DeferredItem<Item> BLACKHOLE;
	public static final DeferredItem<Item> SOLARBEAM;
	public static final DeferredItem<Item> PHOENIX_SPAWN_EGG;
	public static final DeferredItem<Item> INFINITDARK_SPAWN_EGG;
	public static final DeferredItem<Item> VILLAGERBOSS_SPAWN_EGG;
	public static final DeferredItem<Item> BARRIERE_CLAIRE;
	public static final DeferredItem<Item> PORTILLON_CLAIR;
	public static final DeferredItem<Item> BOUTON_CLAIR;
	public static final DeferredItem<Item> PLAQUE_DE_PRESSION_CLAIRE;
	public static final DeferredItem<Item> PORTE_CLAIRE;
	public static final DeferredItem<Item> TRAPPE_CLAIRE;
	public static final DeferredItem<Item> ESCALIER_DIVIN;
	public static final DeferredItem<Item> DALLE_DIVINE;
	public static final DeferredItem<Item> BARRIERE_DIVINE;
	public static final DeferredItem<Item> PORTILLON_DIVIN;
	public static final DeferredItem<Item> BOUTON_DIVIN;
	public static final DeferredItem<Item> PLAQUE_DE_PRESSION_DIVINE;
	public static final DeferredItem<Item> PORTE_DIVINE;
	public static final DeferredItem<Item> TRAPPE_DIVINE;
	public static final DeferredItem<Item> BOIS_DIVIN;
	public static final DeferredItem<Item> BOIS_DE_LUMIERE;
	public static final DeferredItem<Item> BOIS_OMBRE;
	public static final DeferredItem<Item> BUCHE_ECORCEE_OMBRE;
	public static final DeferredItem<Item> BOIS_ECORCE_OMBRE;
	public static final DeferredItem<Item> BUCHE_ECORCEE_DE_LUMIERE;
	public static final DeferredItem<Item> BOIS_ECORCE_DE_LUMIERE;
	public static final DeferredItem<Item> BUCHE_ECORCEE_DIVINE;
	public static final DeferredItem<Item> BOIS_ECORCE_DIVIN;
	public static final DeferredItem<Item> BEDROCKINSTABLE;
	static {
		TRONC_DIVIN = block(UnderworldUpdateModBlocks.TRONC_DIVIN);
		GODPLANKS = block(UnderworldUpdateModBlocks.GODPLANKS);
		GODLEAVES = block(UnderworldUpdateModBlocks.GODLEAVES);
		ENDGRASS = block(UnderworldUpdateModBlocks.ENDGRASS);
		SWITCHENDERPERLE = REGISTRY.register("switchenderperle", SwitchenderperleItem::new);
		CLEPSIDRE = REGISTRY.register("clepsidre", ClepsidreItem::new);
		YING = REGISTRY.register("ying", YingItem::new);
		YANG = REGISTRY.register("yang", YangItem::new);
		DARKOAK = block(UnderworldUpdateModBlocks.DARKOAK);
		DARKPLANKS = block(UnderworldUpdateModBlocks.DARKPLANKS);
		DARKTRAPDOORS = block(UnderworldUpdateModBlocks.DARKTRAPDOORS);
		DARKFENCE = block(UnderworldUpdateModBlocks.DARKFENCE);
		DARKSLAB = block(UnderworldUpdateModBlocks.DARKSLAB);
		DARKFENCEDOOR = block(UnderworldUpdateModBlocks.DARKFENCEDOOR);
		DARKBUTTON = block(UnderworldUpdateModBlocks.DARKBUTTON);
		DARKPRESSUREPLATE = block(UnderworldUpdateModBlocks.DARKPRESSUREPLATE);
		DARKDOORS = doubleBlock(UnderworldUpdateModBlocks.DARKDOORS);
		GRAPPIN = REGISTRY.register("grappin", GrappinItem::new);
		FIRESWORD = REGISTRY.register("firesword", FireswordItem::new);
		DARKSTAIRS = block(UnderworldUpdateModBlocks.DARKSTAIRS);
		INKBOTTLE = REGISTRY.register("inkbottle", InkbottleItem::new);
		LIGHTOAK = block(UnderworldUpdateModBlocks.LIGHTOAK);
		LIGHTPLANKS = block(UnderworldUpdateModBlocks.LIGHTPLANKS);
		LIGHTSTAIRS = block(UnderworldUpdateModBlocks.LIGHTSTAIRS);
		LIGHTSLAB = block(UnderworldUpdateModBlocks.LIGHTSLAB);
		LIGHTGRASS = block(UnderworldUpdateModBlocks.LIGHTGRASS);
		LIGHTDIRT = block(UnderworldUpdateModBlocks.LIGHTDIRT);
		BLACKHOLE = REGISTRY.register("blackhole", BlackholeItem::new);
		SOLARBEAM = REGISTRY.register("solarbeam", SolarbeamItem::new);
		PHOENIX_SPAWN_EGG = REGISTRY.register("phoenix_spawn_egg", () -> new DeferredSpawnEggItem(UnderworldUpdateModEntities.PHOENIX, -13312, -205, new Item.Properties()));
		INFINITDARK_SPAWN_EGG = REGISTRY.register("infinitdark_spawn_egg", () -> new DeferredSpawnEggItem(UnderworldUpdateModEntities.INFINITDARK, -16777216, -10092544, new Item.Properties()));
		VILLAGERBOSS_SPAWN_EGG = REGISTRY.register("villagerboss_spawn_egg", () -> new DeferredSpawnEggItem(UnderworldUpdateModEntities.VILLAGERBOSS, -1, -16777216, new Item.Properties()));
		BARRIERE_CLAIRE = block(UnderworldUpdateModBlocks.BARRIERE_CLAIRE);
		PORTILLON_CLAIR = block(UnderworldUpdateModBlocks.PORTILLON_CLAIR);
		BOUTON_CLAIR = block(UnderworldUpdateModBlocks.BOUTON_CLAIR);
		PLAQUE_DE_PRESSION_CLAIRE = block(UnderworldUpdateModBlocks.PLAQUE_DE_PRESSION_CLAIRE);
		PORTE_CLAIRE = doubleBlock(UnderworldUpdateModBlocks.PORTE_CLAIRE);
		TRAPPE_CLAIRE = block(UnderworldUpdateModBlocks.TRAPPE_CLAIRE);
		ESCALIER_DIVIN = block(UnderworldUpdateModBlocks.ESCALIER_DIVIN);
		DALLE_DIVINE = block(UnderworldUpdateModBlocks.DALLE_DIVINE);
		BARRIERE_DIVINE = block(UnderworldUpdateModBlocks.BARRIERE_DIVINE);
		PORTILLON_DIVIN = block(UnderworldUpdateModBlocks.PORTILLON_DIVIN);
		BOUTON_DIVIN = block(UnderworldUpdateModBlocks.BOUTON_DIVIN);
		PLAQUE_DE_PRESSION_DIVINE = block(UnderworldUpdateModBlocks.PLAQUE_DE_PRESSION_DIVINE);
		PORTE_DIVINE = doubleBlock(UnderworldUpdateModBlocks.PORTE_DIVINE);
		TRAPPE_DIVINE = block(UnderworldUpdateModBlocks.TRAPPE_DIVINE);
		BOIS_DIVIN = block(UnderworldUpdateModBlocks.BOIS_DIVIN);
		BOIS_DE_LUMIERE = block(UnderworldUpdateModBlocks.BOIS_DE_LUMIERE);
		BOIS_OMBRE = block(UnderworldUpdateModBlocks.BOIS_OMBRE);
		BUCHE_ECORCEE_OMBRE = block(UnderworldUpdateModBlocks.BUCHE_ECORCEE_OMBRE);
		BOIS_ECORCE_OMBRE = block(UnderworldUpdateModBlocks.BOIS_ECORCE_OMBRE);
		BUCHE_ECORCEE_DE_LUMIERE = block(UnderworldUpdateModBlocks.BUCHE_ECORCEE_DE_LUMIERE);
		BOIS_ECORCE_DE_LUMIERE = block(UnderworldUpdateModBlocks.BOIS_ECORCE_DE_LUMIERE);
		BUCHE_ECORCEE_DIVINE = block(UnderworldUpdateModBlocks.BUCHE_ECORCEE_DIVINE);
		BOIS_ECORCE_DIVIN = block(UnderworldUpdateModBlocks.BOIS_ECORCE_DIVIN);
		BEDROCKINSTABLE = block(UnderworldUpdateModBlocks.BEDROCKINSTABLE, new Item.Properties().fireResistant());
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return block(block, new Item.Properties());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}

	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block) {
		return doubleBlock(block, new Item.Properties());
	}

	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), properties));
	}
}