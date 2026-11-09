package net.mcreator.underworldupdate.iles;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Place le temple final (templefinale.nbt) une seule fois, au centre exact 0,30,0
 * de la dimension underworld:underworld, des qu'un joueur y est present.
 *
 * Un jigsaw ne peut pas garantir une position fixe (rotation aleatoire + position
 * liee au chunk), d'ou ce placement direct du template.
 */
@EventBusSubscriber(modid = VoidLightClient.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class TemplePlacer {
    private TemplePlacer() {}

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceKey<Level> UNDERWORLD =
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(VoidLightClient.NAMESPACE, "underworld"));
    private static final ResourceLocation TEMPLE =
            ResourceLocation.fromNamespaceAndPath(VoidLightClient.MOD_ID, "templefinale");

    // Mettre a true pour poser en plus un temple unique au centre exact 0,30,0.
    // Desactive : le temple genere maintenant normalement dans le biome underworld:empty_plain.
    private static final boolean ENABLED = false;

    // Point cible : le CENTRE du temple (x et z) et sa base (y).
    private static final int TEMPLE_X = 0;
    private static final int TEMPLE_Y = 30;
    private static final int TEMPLE_Z = 0;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!ENABLED) return;
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != UNDERWORLD) return;
        if (level.getGameTime() % 20L != 0L || level.players().isEmpty()) return;

        State state = State.get(level);
        if (state.placed) return;

        Optional<StructureTemplate> opt = level.getStructureManager().get(TEMPLE);
        if (opt.isEmpty()) {
            LOGGER.error("[UnderWorld] template introuvable : {}", TEMPLE);
            return;
        }
        StructureTemplate template = opt.get();
        Vec3i size = template.getSize();
        BlockPos origin = new BlockPos(TEMPLE_X - size.getX() / 2, TEMPLE_Y, TEMPLE_Z - size.getZ() / 2);

        // Charge/genere les chunks couverts par le temple avant de le poser.
        int minCx = origin.getX() >> 4, maxCx = (origin.getX() + size.getX() - 1) >> 4;
        int minCz = origin.getZ() >> 4, maxCz = (origin.getZ() + size.getZ() - 1) >> 4;
        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                level.getChunk(cx, cz);
            }
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .addProcessor(new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW)));
        boolean ok = template.placeInWorld(level, origin, origin, settings, level.getRandom(), 2);
        if (ok) {
            state.placed = true;
            state.setDirty();
            LOGGER.info("[UnderWorld] temple final place : coin {} (centre {},{},{})", origin, TEMPLE_X, TEMPLE_Y, TEMPLE_Z);
        } else {
            LOGGER.error("[UnderWorld] echec du placement du temple final");
        }
    }

    public static final class State extends SavedData {
        private static final String NAME = "underworld_update_temple";
        boolean placed = false;

        public static State get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(State::new, State::load, DataFixTypes.LEVEL), NAME);
        }

        private static State load(CompoundTag tag, HolderLookup.Provider lookup) {
            State s = new State();
            s.placed = tag.getBoolean("placed");
            return s;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
            tag.putBoolean("placed", placed);
            return tag;
        }
    }
}
