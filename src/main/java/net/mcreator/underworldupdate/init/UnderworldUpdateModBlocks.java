/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworldupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

import net.mcreator.underworldupdate.block.*;
import net.mcreator.underworldupdate.UnderworldUpdateMod;

public class UnderworldUpdateModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(UnderworldUpdateMod.MODID);
	public static final DeferredBlock<Block> TRONC_DIVIN;
	public static final DeferredBlock<Block> GODPLANKS;
	public static final DeferredBlock<Block> GODLEAVES;
	public static final DeferredBlock<Block> ENDGRASS;
	public static final DeferredBlock<Block> DARKOAK;
	public static final DeferredBlock<Block> DARKPLANKS;
	public static final DeferredBlock<Block> DARKTRAPDOORS;
	public static final DeferredBlock<Block> DARKFENCE;
	public static final DeferredBlock<Block> DARKSLAB;
	public static final DeferredBlock<Block> DARKFENCEDOOR;
	public static final DeferredBlock<Block> DARKBUTTON;
	public static final DeferredBlock<Block> DARKPRESSUREPLATE;
	public static final DeferredBlock<Block> DARKDOORS;
	public static final DeferredBlock<Block> DARKSTAIRS;
	public static final DeferredBlock<Block> LIGHTOAK;
	public static final DeferredBlock<Block> LIGHTPLANKS;
	public static final DeferredBlock<Block> LIGHTSTAIRS;
	public static final DeferredBlock<Block> LIGHTSLAB;
	public static final DeferredBlock<Block> LIGHTGRASS;
	public static final DeferredBlock<Block> LIGHTDIRT;
	public static final DeferredBlock<Block> BARRIERE_CLAIRE;
	public static final DeferredBlock<Block> PORTILLON_CLAIR;
	public static final DeferredBlock<Block> BOUTON_CLAIR;
	public static final DeferredBlock<Block> PLAQUE_DE_PRESSION_CLAIRE;
	public static final DeferredBlock<Block> PORTE_CLAIRE;
	public static final DeferredBlock<Block> TRAPPE_CLAIRE;
	public static final DeferredBlock<Block> ESCALIER_DIVIN;
	public static final DeferredBlock<Block> DALLE_DIVINE;
	public static final DeferredBlock<Block> BARRIERE_DIVINE;
	public static final DeferredBlock<Block> PORTILLON_DIVIN;
	public static final DeferredBlock<Block> BOUTON_DIVIN;
	public static final DeferredBlock<Block> PLAQUE_DE_PRESSION_DIVINE;
	public static final DeferredBlock<Block> PORTE_DIVINE;
	public static final DeferredBlock<Block> TRAPPE_DIVINE;
	public static final DeferredBlock<Block> BOIS_DIVIN;
	public static final DeferredBlock<Block> BOIS_DE_LUMIERE;
	public static final DeferredBlock<Block> BOIS_OMBRE;
	public static final DeferredBlock<Block> BUCHE_ECORCEE_OMBRE;
	public static final DeferredBlock<Block> BOIS_ECORCE_OMBRE;
	public static final DeferredBlock<Block> BUCHE_ECORCEE_DE_LUMIERE;
	public static final DeferredBlock<Block> BOIS_ECORCE_DE_LUMIERE;
	public static final DeferredBlock<Block> BUCHE_ECORCEE_DIVINE;
	public static final DeferredBlock<Block> BOIS_ECORCE_DIVIN;
	public static final DeferredBlock<Block> BEDROCKINSTABLE;
	static {
		TRONC_DIVIN = REGISTRY.register("tronc_divin", TroncDivinBlock::new);
		GODPLANKS = REGISTRY.register("godplanks", GodplanksBlock::new);
		GODLEAVES = REGISTRY.register("godleaves", GodleavesBlock::new);
		ENDGRASS = REGISTRY.register("endgrass", EndgrassBlock::new);
		DARKOAK = REGISTRY.register("darkoak", DarkoakBlock::new);
		DARKPLANKS = REGISTRY.register("darkplanks", DarkplanksBlock::new);
		DARKTRAPDOORS = REGISTRY.register("darktrapdoors", DarktrapdoorsBlock::new);
		DARKFENCE = REGISTRY.register("darkfence", DarkfenceBlock::new);
		DARKSLAB = REGISTRY.register("darkslab", DarkslabBlock::new);
		DARKFENCEDOOR = REGISTRY.register("darkfencedoor", DarkfencedoorBlock::new);
		DARKBUTTON = REGISTRY.register("darkbutton", DarkbuttonBlock::new);
		DARKPRESSUREPLATE = REGISTRY.register("darkpressureplate", DarkpressureplateBlock::new);
		DARKDOORS = REGISTRY.register("darkdoors", DarkdoorsBlock::new);
		DARKSTAIRS = REGISTRY.register("darkstairs", DarkstairsBlock::new);
		LIGHTOAK = REGISTRY.register("lightoak", LightoakBlock::new);
		LIGHTPLANKS = REGISTRY.register("lightplanks", LightplanksBlock::new);
		LIGHTSTAIRS = REGISTRY.register("lightstairs", LightstairsBlock::new);
		LIGHTSLAB = REGISTRY.register("lightslab", LightslabBlock::new);
		LIGHTGRASS = REGISTRY.register("lightgrass", LightgrassBlock::new);
		LIGHTDIRT = REGISTRY.register("lightdirt", LightdirtBlock::new);
		BARRIERE_CLAIRE = REGISTRY.register("barriere_claire", BarriereClaireBlock::new);
		PORTILLON_CLAIR = REGISTRY.register("portillon_clair", PortillonClairBlock::new);
		BOUTON_CLAIR = REGISTRY.register("bouton_clair", BoutonClairBlock::new);
		PLAQUE_DE_PRESSION_CLAIRE = REGISTRY.register("plaque_de_pression_claire", PlaqueDePressionClaireBlock::new);
		PORTE_CLAIRE = REGISTRY.register("porte_claire", PorteClaireBlock::new);
		TRAPPE_CLAIRE = REGISTRY.register("trappe_claire", TrappeClaireBlock::new);
		ESCALIER_DIVIN = REGISTRY.register("escalier_divin", EscalierDivinBlock::new);
		DALLE_DIVINE = REGISTRY.register("dalle_divine", DalleDivineBlock::new);
		BARRIERE_DIVINE = REGISTRY.register("barriere_divine", BarriereDivineBlock::new);
		PORTILLON_DIVIN = REGISTRY.register("portillon_divin", PortillonDivinBlock::new);
		BOUTON_DIVIN = REGISTRY.register("bouton_divin", BoutonDivinBlock::new);
		PLAQUE_DE_PRESSION_DIVINE = REGISTRY.register("plaque_de_pression_divine", PlaqueDePressionDivineBlock::new);
		PORTE_DIVINE = REGISTRY.register("porte_divine", PorteDivineBlock::new);
		TRAPPE_DIVINE = REGISTRY.register("trappe_divine", TrappeDivineBlock::new);
		BOIS_DIVIN = REGISTRY.register("bois_divin", BoisDivinBlock::new);
		BOIS_DE_LUMIERE = REGISTRY.register("bois_de_lumiere", BoisDeLumiereBlock::new);
		BOIS_OMBRE = REGISTRY.register("bois_ombre", BoisOmbreBlock::new);
		BUCHE_ECORCEE_OMBRE = REGISTRY.register("buche_ecorcee_ombre", BucheEcorceeOmbreBlock::new);
		BOIS_ECORCE_OMBRE = REGISTRY.register("bois_ecorce_ombre", BoisEcorceOmbreBlock::new);
		BUCHE_ECORCEE_DE_LUMIERE = REGISTRY.register("buche_ecorcee_de_lumiere", BucheEcorceeDeLumiereBlock::new);
		BOIS_ECORCE_DE_LUMIERE = REGISTRY.register("bois_ecorce_de_lumiere", BoisEcorceDeLumiereBlock::new);
		BUCHE_ECORCEE_DIVINE = REGISTRY.register("buche_ecorcee_divine", BucheEcorceeDivineBlock::new);
		BOIS_ECORCE_DIVIN = REGISTRY.register("bois_ecorce_divin", BoisEcorceDivinBlock::new);
		BEDROCKINSTABLE = REGISTRY.register("bedrockinstable", BedrockinstableBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}