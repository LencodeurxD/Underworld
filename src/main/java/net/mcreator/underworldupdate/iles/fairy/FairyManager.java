package net.mcreator.underworldupdate.iles.fairy;

import net.mcreator.underworldupdate.iles.BubbleField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Gère, côté serveur, les fées autour des joueurs de la dimension UnderWorld :
 * apparition (~20 par bulle), rassemblement à l'entrée d'une bulle, liaison d'une fée au joueur.
 * Une fois par seconde et par joueur.
 */
@EventBusSubscriber(modid = FairyEntities.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class FairyManager {
    /** Nombre de fées visé par bulle, autour d'un joueur. */
    public static final int TARGET = 20;

    private static final ResourceKey<Level> UNDERWORLD = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(FairyEntities.DIMENSION_NAMESPACE, FairyEntities.DIMENSION_PATH));

    private static final Map<UUID, Long> BUBBLE_OF = new HashMap<>();
    private static final Map<UUID, Long> ENTERED_AT = new HashMap<>();

    private FairyManager() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level) || level.dimension() != UNDERWORLD) return;
        if ((player.tickCount + player.getId() * 7) % 20 != 0) return;

        UUID id = player.getUUID();
        if (player.isSpectator() || !BubbleField.isSeedKnown()) {
            BUBBLE_OF.remove(id);
            return;
        }
        long now = level.getGameTime();
        BubbleField.BubbleInfo info = BubbleField.bubbleAt(player.getX(), player.getY(), player.getZ());
        if (info == null) {
            BUBBLE_OF.remove(id);
            return;
        }
        Long prev = BUBBLE_OF.put(id, info.id);
        boolean entered = prev == null || prev.longValue() != info.id;
        if (entered) ENTERED_AT.put(id, now);
        boolean fresh = now - ENTERED_AT.getOrDefault(id, 0L) < 600L;

        FairyFear fear = FairyFear.get(level);
        boolean scared = fear.isScared(id, info.id, now);

        List<FairyEntity> near = new ArrayList<>(level.getEntitiesOfClass(FairyEntity.class,
                player.getBoundingBox().inflate(96.0), f -> f.getHomeId() == info.id && f.isAlive()));

        // trop de fées dans le coin : on retire les plus éloignées (jamais celles qui sont liées)
        if (near.size() > TARGET + 6) {
            List<FairyEntity> sorted = new ArrayList<>(near);
            sorted.sort(Comparator.comparingDouble((FairyEntity f) -> f.distanceToSqr(player)).reversed());
            int excess = sorted.size() - (TARGET + 2);
            for (FairyEntity f : sorted) {
                if (excess <= 0) break;
                if (f.getBondedTo() == null) { f.discard(); excess--; }
            }
            near.removeIf(FairyEntity::isRemoved);
        }

        int missing = TARGET - near.size();
        if (missing > 0) spawnGroups(level, player, info, missing, fresh);

        // une fée se lie au joueur (la plus proche, dans 30 blocs : elle vole jusqu'à lui)
        if (!scared) {
            boolean has = false;
            for (FairyEntity f : near) {
                if (id.equals(f.getBondedTo())) { has = true; break; }
            }
            if (!has) {
                FairyEntity best = null;
                double bd = 30.0 * 30.0;
                for (FairyEntity f : near) {
                    if (f.isRemoved() || f.getBondedTo() != null) continue;
                    double d = f.distanceToSqr(player);
                    if (d < bd) { bd = d; best = f; }
                }
                if (best != null) best.bondTo(id);
            }
        }
    }

    private static void spawnGroups(ServerLevel level, ServerPlayer player, BubbleField.BubbleInfo info,
                                    int missing, boolean all) {
        RandomSource r = level.random;
        int budget = all ? missing : Math.min(missing, 4);
        int guard = 0;
        while (budget > 0 && guard++ < 12) {
            int size = Math.min(budget, 2 + r.nextInt(3));
            Vec3 c = pickSpawn(level, info, player, r);
            if (c == null) continue;
            long group = r.nextLong();
            for (int i = 0; i < size; i++) {
                Vec3 q = c.add((r.nextDouble() - 0.5) * 2.8, (r.nextDouble() - 0.5) * 1.6, (r.nextDouble() - 0.5) * 2.8);
                if (info.k(q.x, q.y, q.z) > 0.84 || !level.noCollision(AABB.ofSize(q, 0.6, 0.6, 0.6))) q = c;
                FairyEntity f = (info.type > 0 ? FairyEntities.LIGHT : FairyEntities.DARK).create(level);
                if (f == null) continue;
                f.moveTo(q.x, q.y, q.z, 0.0F, 0.0F);
                f.setHome(info, group);
                level.addFreshEntity(f);
            }
            budget -= size;
        }
    }

    /** Un point libre, dans la bulle, à 22–60 blocs du joueur, dans une zone chargée. */
    private static Vec3 pickSpawn(ServerLevel level, BubbleField.BubbleInfo info, ServerPlayer player, RandomSource r) {
        for (int i = 0; i < 14; i++) {
            double a = r.nextDouble() * Math.PI * 2.0;
            double dist = 22.0 + r.nextDouble() * 38.0;
            Vec3 c = player.position().add(Math.cos(a) * dist, (r.nextDouble() * 2.0 - 1.0) * 14.0 + 1.5, Math.sin(a) * dist);
            if (info.k(c.x, c.y, c.z) > 0.80) continue;
            if (!level.hasChunkAt(BlockPos.containing(c.x, c.y, c.z))) continue;
            if (!level.noCollision(AABB.ofSize(c, 3.0, 3.0, 3.0))) continue;
            return c;
        }
        return null;
    }
}
