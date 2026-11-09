package net.mcreator.underworldupdate.iles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Vector3f;

import java.util.List;

@EventBusSubscriber(modid = VoidLightClient.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class VoidHoleEvents {
    private VoidHoleEvents() {}

    private static final ResourceKey<Level> UNDERWORLD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(VoidLightClient.NAMESPACE, "underworld"));
    private static final ParticleOptions BLACK = new DustParticleOptions(new Vector3f(0.0f, 0.0f, 0.0f), 4.0f);
    private static final ParticleOptions WHITE = new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 4.0f);

    private static final int ROOF_BOTTOM = 250;
    private static final int ROOF_TOP = 255;
    private static final double ARRIVAL_Y = 253.0;
    private static final int ISLAND_TOP = 236;
    private static final int ISLAND_RADIUS = 9;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level() instanceof ServerLevel here && here.dimension() == UNDERWORLD) {
            goBack(player, here);
            return;
        }
        if (!(player.level() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) return;
        int minY = level.getMinBuildHeight();
        if (player.getY() > minY + 14.0) return;

        VoidHoles holes = VoidHoles.get(level);
        int px = (int) Math.floor(player.getX()), pz = (int) Math.floor(player.getZ());

        if (player.tickCount % 10 == 0) {
            BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    int x = px + dx, z = pz + dz;
                    if (!level.hasChunkAt(p.set(x, minY, z))) continue;
                    if (level.getBlockState(p).isAir() && holes.add(x, z)) carveRoof(level.getServer().getLevel(UNDERWORLD), x, z);
                }
            }
        }

        if (player.getY() < minY + 1.0 && level.getBlockState(new BlockPos(px, minY, pz)).isAir()) {
            if (holes.add(px, pz)) carveRoof(level.getServer().getLevel(UNDERWORLD), px, pz);
            enter(player, level, holes, px, pz);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) return;
        if ((level.getGameTime() & 1L) != 0L) return;
        if (level.players().isEmpty()) return;
        VoidHoles holes = VoidHoles.get(level);
        List<int[]> all = holes.list();
        if (all.isEmpty()) return;
        int minY = level.getMinBuildHeight();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int[] h : all) {
            double cx = h[0] + 0.5, cz = h[1] + 0.5;
            boolean near = false;
            for (ServerPlayer sp : level.players()) {
                double dx = sp.getX() - cx, dz = sp.getZ() - cz;
                if (dx * dx + dz * dz < 40.0 * 40.0 && sp.getY() < minY + 60.0) { near = true; break; }
            }
            if (!near) continue;
            if (!level.hasChunkAt(p.set(h[0], minY, h[1]))) continue;
            if (!level.getBlockState(p).isAir()) { holes.remove(h[0], h[1]); continue; }
            smoke(level, cx, minY, cz);
        }
    }

    private static void smoke(ServerLevel level, double cx, int minY, double cz) {
        double y = minY;
        level.sendParticles(BLACK, cx, y - 3.0, cz, 14, 3.2, 2.6, 3.2, 0.01);
        level.sendParticles(WHITE, cx, y - 3.0, cz, 14, 3.2, 2.6, 3.2, 0.01);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, cx, y - 2.0, cz, 5, 3.0, 2.0, 3.0, 0.01);
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, cx, y - 4.0, cz, 3, 3.0, 2.5, 3.0, 0.002);
        for (int i = 0; i < 2; i++) {
            double ox = (level.random.nextDouble() - 0.5) * 0.5, oz = (level.random.nextDouble() - 0.5) * 0.5;
            level.sendParticles(ParticleTypes.LARGE_SMOKE, cx + ox, y + 0.3, cz + oz, 0, 0.0, 1.0, 0.0, 0.08 + level.random.nextDouble() * 0.04);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, cx - ox, y + 0.3, cz - oz, 0, 0.0, 1.0, 0.0, 0.06);
        }
    }

    private static void carveRoof(ServerLevel underworld, int x, int z) {
        if (underworld == null) return;
        underworld.getChunk(x >> 4, z >> 4);
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int y = ROOF_BOTTOM; y <= ROOF_TOP; y++) {
            p.set(x, y, z);
            if (underworld.getBlockState(p).is(Blocks.BEDROCK)) underworld.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
        }
    }

    private static void enter(ServerPlayer player, ServerLevel overworld, VoidHoles holes, int x, int z) {
        ServerLevel underworld = overworld.getServer().getLevel(UNDERWORLD);
        if (underworld == null) return;
        carveRoof(underworld, x, z);
        boolean first = holes.firstVisit(player.getUUID());
        if (first) buildIsland(underworld, x, z);
        player.teleportTo(underworld, x + 0.5, ARRIVAL_Y, z + 0.5, player.getYRot(), player.getXRot());
        player.fallDistance = 0.0f;
        player.setDeltaMovement(0.0, -0.2, 0.0);
        player.hurtMarked = true;
        if (first) player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 240, 0, false, false));
    }

    private static void goBack(ServerPlayer player, ServerLevel underworld) {
        if (player.getY() < ROOF_TOP - 1.0) return;
        int x = (int) Math.floor(player.getX()), z = (int) Math.floor(player.getZ());
        if (!underworld.getBlockState(new BlockPos(x, ROOF_TOP - 1, z)).isAir()) return;
        ServerLevel overworld = underworld.getServer().overworld();
        VoidHoles holes = VoidHoles.get(overworld);
        if (!holes.has(x, z)) return;

        int minY = overworld.getMinBuildHeight();
        overworld.getChunk(x >> 4, z >> 4);
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        int tx = x + dirs[0][0], tz = z + dirs[0][1];
        for (int[] d : dirs) {
            BlockPos below = new BlockPos(x + d[0], minY, z + d[1]);
            if (!overworld.getBlockState(below).isAir()) {
                tx = below.getX();
                tz = below.getZ();
                break;
            }
        }
        BlockPos floor = new BlockPos(tx, minY, tz);
        if (overworld.getBlockState(floor).isAir()) overworld.setBlock(floor, Blocks.BEDROCK.defaultBlockState(), 2);
        for (int dy = 1; dy <= 2; dy++) {
            BlockPos p = new BlockPos(tx, minY + dy, tz);
            if (!overworld.getBlockState(p).isAir()) overworld.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
        }
        player.teleportTo(overworld, tx + 0.5, minY + 1.0, tz + 0.5, player.getYRot(), player.getXRot());
        player.fallDistance = 0.0f;
        player.setDeltaMovement(0.0, 0.0, 0.0);
        player.hurtMarked = true;
    }

    private static void buildIsland(ServerLevel level, int cx, int cz) {
        boolean neutral = BubbleField.isSeedKnown() && BubbleField.typeAt(cx, ISLAND_TOP, cz) == 0;
        BlockState grass = neutral ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState dirt = neutral ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.DIRT.defaultBlockState();
        BlockState stone = neutral ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int dx = -ISLAND_RADIUS; dx <= ISLAND_RADIUS; dx++) {
            for (int dz = -ISLAND_RADIUS; dz <= ISLAND_RADIUS; dz++) {
                double r = Math.sqrt(dx * dx + dz * dz) / ISLAND_RADIUS;
                if (r > 1.0) continue;
                int bottom = ISLAND_TOP - 3 - (int) Math.round(9.0 * (1.0 - r * r));
                for (int y = ISLAND_TOP; y >= bottom; y--) {
                    p.set(cx + dx, y, cz + dz);
                    if (!level.getBlockState(p).isAir()) continue;
                    level.setBlock(p, y == ISLAND_TOP ? grass : (y >= ISLAND_TOP - 2 ? dirt : stone), 2);
                }
            }
        }
    }
}
