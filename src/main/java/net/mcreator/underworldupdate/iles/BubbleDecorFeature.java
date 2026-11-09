package net.mcreator.underworldupdate.iles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;

public final class BubbleDecorFeature extends Feature<NoneFeatureConfiguration> {
    private static final Block[] SMALL_FLOWERS = {
            Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP,
            Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY};
    private static final Block[] TALL_FLOWERS = {Blocks.SUNFLOWER, Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.PEONY};

    public BubbleDecorFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!BubbleField.isSeedKnown()) return false;
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        int x0 = (origin.getX() >> 4) << 4;
        int z0 = (origin.getZ() >> 4) << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        boolean placed = false;

        placed |= lakes(level, x0, z0, pos);
        placed |= falls(level, x0, z0, pos);
        placed |= surface(level, x0, z0, pos);
        placed |= darkTrees(level, x0, z0, pos);
        placed |= darkRuin(level, x0, z0, pos);
        return placed;
    }

    private static final Map<String, BlockState> MODDED = new ConcurrentHashMap<>();

    private static BlockState blk(String name) {
        return MODDED.computeIfAbsent(name, n -> {
            Block b = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("underworld_update", n));
            return b == null ? Blocks.AIR.defaultBlockState() : b.defaultBlockState();
        });
    }

    private static boolean has(String name) {
        return !blk(name).isAir();
    }

    private static <T extends Comparable<T>> BlockState with(BlockState s, Property<T> p, T v) {
        return s.hasProperty(p) ? s.setValue(p, v) : s;
    }

    private static void put(WorldGenLevel level, BlockPos.MutableBlockPos pos, int x, int y, int z, BlockState s) {
        if (s.isAir()) return;
        pos.set(x, y, z);
        level.setBlock(pos, s, 2);
    }

    private static boolean lakes(WorldGenLevel level, int x0, int z0, BlockPos.MutableBlockPos pos) {
        boolean placed = false;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = x0 + dx, z = z0 + dz;
                double lv = BubbleField.lakeLevel(x + 0.5, z + 0.5);
                if (Double.isNaN(lv)) continue;
                int waterY = (int) Math.floor(lv);
                BlockState fluid = BubbleField.typeAt(x + 0.5, waterY, z + 0.5) < 0 ? Blocks.LAVA.defaultBlockState() : Blocks.WATER.defaultBlockState();
                int ground = Integer.MIN_VALUE;
                for (int y = waterY; y > waterY - 40; y--) {
                    pos.set(x, y, z);
                    if (!level.getBlockState(pos).isAir()) { ground = y; break; }
                }
                if (ground == Integer.MIN_VALUE) continue;
                for (int y = ground + 1; y <= waterY; y++) {
                    pos.set(x, y, z);
                    if (level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, fluid, 2);
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }

    private static boolean falls(WorldGenLevel level, int x0, int z0, BlockPos.MutableBlockPos pos) {
        boolean placed = false;
        List<double[]> sites = BubbleField.fallsInChunk(x0, z0);
        for (double[] f : sites) {
            int x = (int) Math.floor(f[0]);
            int z = (int) Math.floor(f[1]);
            int yHint = (int) Math.floor(f[2]);
            boolean lava = f[3] > 0.5;
            BlockState state = lava ? Blocks.LAVA.defaultBlockState() : Blocks.WATER.defaultBlockState();
            Fluid fluid = lava ? Fluids.LAVA : Fluids.WATER;
            int ground = Integer.MIN_VALUE;
            for (int y = yHint + 14; y >= yHint - 16; y--) {
                pos.set(x, y, z);
                if (level.getBlockState(pos).isAir()) continue;
                pos.set(x, y + 1, z);
                if (level.getBlockState(pos).isAir()) { ground = y; break; }
            }
            if (ground == Integer.MIN_VALUE) continue;
            for (int ox = 0; ox <= 1; ox++) {
                for (int oz = 0; oz <= 1; oz++) {
                    int px = x + ox, pz = z + oz;
                    if (px >= x0 + 16 || pz >= z0 + 16) continue;
                    pos.set(px, ground + 1, pz);
                    if (!level.getBlockState(pos).isAir()) continue;
                    pos.set(px, ground, pz);
                    if (level.getBlockState(pos).isAir()) continue;
                    pos.set(px, ground + 1, pz);
                    level.setBlock(pos, state, 2);
                    level.scheduleTick(pos, fluid, 0);
                    placed = true;
                }
            }
        }
        return placed;
    }

    private static boolean surface(WorldGenLevel level, int x0, int z0, BlockPos.MutableBlockPos pos) {
        boolean placed = false;
        long seed = BubbleField.seed();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = x0 + dx, z = z0 + dz;
                int h = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                if (h <= 2) continue;
                int y = h - 1;
                pos.set(x, y, z);
                BlockState ground = level.getBlockState(pos);
                if (!ground.is(BlockTags.DIRT)) continue;
                int type = BubbleField.typeAt(x + 0.5, y, z + 0.5);
                if (type == 0) continue;
                BlockPos above = new BlockPos(x, y + 1, z);
                if (!level.getBlockState(above).isAir()) continue;
                int flavor = BubbleField.flavorAt(x + 0.5, y, z + 0.5);
                SplittableRandom rng = new SplittableRandom(seed ^ (x * 341873128712L) ^ (z * 132897987541L) ^ 0x5DEECE66DL);

                if (type > 0) placed |= lightColumn(level, pos, above, x, z, flavor, rng);
                else placed |= darkColumn(level, pos, above, x, z, flavor, rng);
            }
        }
        return placed;
    }

    private static boolean lightColumn(WorldGenLevel level, BlockPos ground, BlockPos above, int x, int z, int flavor, SplittableRandom rng) {
        double patch = BubbleField.noise(1.0, x * 0.07, z * 0.07);
        double species = BubbleField.noise(2.0, x * 0.05, z * 0.05);
        double r = rng.nextDouble();
        if (flavor == 0) {
            if (patch > 0.60 && r < 0.55) {
                Block b = SMALL_FLOWERS[(int) (species * SMALL_FLOWERS.length) % SMALL_FLOWERS.length];
                level.setBlock(above, b.defaultBlockState(), 2);
                return true;
            }
            if (patch < 0.32 && r < 0.035) return tall(level, above, TALL_FLOWERS[rng.nextInt(TALL_FLOWERS.length)]);
        } else {
            if (r < 0.014) { level.setBlock(above, Blocks.FLOWERING_AZALEA.defaultBlockState(), 2); return true; }
            if (r < 0.040) return tall(level, above, TALL_FLOWERS[rng.nextInt(TALL_FLOWERS.length)]);
            if (r < 0.065) { level.setBlock(above, Blocks.PINK_PETALS.defaultBlockState(), 2); return true; }
            if (patch > 0.66 && r < 0.20) {
                Block b = SMALL_FLOWERS[(int) (species * SMALL_FLOWERS.length) % SMALL_FLOWERS.length];
                level.setBlock(above, b.defaultBlockState(), 2);
                return true;
            }
        }
        return false;
    }

    private static boolean darkColumn(WorldGenLevel level, BlockPos ground, BlockPos above, int x, int z, int flavor, SplittableRandom rng) {
        double patch = BubbleField.noise(3.0, x * 0.06, z * 0.06);
        double r = rng.nextDouble();
        boolean placed = false;
        double gp = BubbleField.noise(5.0, x * 0.045, z * 0.045);
        boolean swapped = false;
        if (gp > 0.66 && has("endgrass")) {
            level.setBlock(ground, blk("endgrass"), 2);
            swapped = true;
            placed = true;
        } else if (flavor == 1 && gp < 0.10 && has("edengrass")) {
            level.setBlock(ground, blk("edengrass"), 2);
            swapped = true;
            placed = true;
        }
        if (swapped) {
        } else if (flavor == 0) {
            if (patch > 0.66) {
                level.setBlock(ground, Blocks.SOUL_SOIL.defaultBlockState(), 2);
                placed = true;
                if (r < 0.07) level.setBlock(above, Blocks.SOUL_FIRE.defaultBlockState(), 2);
            }
        } else {
            if (patch > 0.68) {
                level.setBlock(ground, Blocks.SCULK.defaultBlockState(), 2);
                placed = true;
            } else if (r < 0.02) {
                level.setBlock(above, Blocks.DEAD_BUSH.defaultBlockState(), 2);
                placed = true;
            }
        }
        if (rng.nextDouble() < 0.0035) {
            int height = 3 + rng.nextInt(7);
            BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
            for (int i = 0; i < height; i++) {
                p.set(above.getX(), above.getY() + i, above.getZ());
                if (!level.getBlockState(p).isAir()) break;
                level.setBlock(p, (i >= height - 2 ? Blocks.CRYING_OBSIDIAN : Blocks.OBSIDIAN).defaultBlockState(), 2);
            }
            placed = true;
        }
        return placed;
    }

    private static boolean darkTrees(WorldGenLevel level, int x0, int z0, BlockPos.MutableBlockPos pos) {
        if (!has("darkoak")) return false;
        SplittableRandom rng = new SplittableRandom(BubbleField.seed() ^ (x0 * 668265263L) ^ (z0 * 374761393L) ^ 0x7A11L);
        BlockState oak = blk("darkoak"), leaves = blk("godleaves");
        int[] bd = {1, 0, -1, 0};
        boolean placed = false;
        for (int t = 0; t < 6; t++) {
            int x = x0 + 3 + rng.nextInt(10), z = z0 + 3 + rng.nextInt(10);
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
            if (y <= 2) continue;
            pos.set(x, y, z);
            if (level.getBlockState(pos).isAir()) continue;
            if (BubbleField.typeAt(x + 0.5, y, z + 0.5) >= 0) continue;
            int flavor = BubbleField.flavorAt(x + 0.5, y, z + 0.5);
            if (rng.nextDouble() > (flavor == 1 ? 0.8 : 0.3)) continue;
            int h = 5 + rng.nextInt(5);
            boolean clear = true;
            for (int i = 1; i <= h + 3 && clear; i++) {
                pos.set(x, y + i, z);
                if (!level.getBlockState(pos).isAir()) clear = false;
            }
            if (!clear) continue;
            for (int i = 1; i <= h; i++) put(level, pos, x, y + i, z, oak);
            for (int b = 0; b < 2; b++) {
                int by = y + 3 + rng.nextInt(Math.max(1, h - 3));
                int k = rng.nextInt(4);
                int bx = x + bd[k], bz = z + bd[(k + 1) & 3];
                pos.set(bx, by, bz);
                if (!level.getBlockState(pos).isAir()) continue;
                put(level, pos, bx, by, bz, oak);
                pos.set(bx, by + 1, bz);
                if (level.getBlockState(pos).isAir()) put(level, pos, bx, by + 1, bz, oak);
            }
            if (!leaves.isAir() && rng.nextDouble() < 0.6) {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -1; dy <= 2; dy++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            if (dx * dx + dy * dy * 2 + dz * dz > 5) continue;
                            pos.set(x + dx, y + h + dy, z + dz);
                            if (level.getBlockState(pos).isAir()) level.setBlock(pos, leaves, 2);
                        }
                    }
                }
            }
            placed = true;
        }
        return placed;
    }

    private static boolean darkRuin(WorldGenLevel level, int x0, int z0, BlockPos.MutableBlockPos pos) {
        if (!has("darkplanks")) return false;
        SplittableRandom rng = new SplittableRandom(BubbleField.seed() ^ (x0 * 912367421L) ^ (z0 * 668265263L) ^ 0x51CAL);
        if (rng.nextDouble() > 0.12) return false;
        int bx = x0 + 4 + rng.nextInt(5), bz = z0 + 4 + rng.nextInt(5);
        int[] ox = {0, 4, 0, 4, 2}, oz = {0, 0, 4, 4, 2};
        int top = Integer.MIN_VALUE, low = Integer.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            int x = bx + ox[i], z = bz + oz[i];
            int h = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            if (h <= 2 || BubbleField.typeAt(x + 0.5, h - 1, z + 0.5) >= 0) return false;
            top = Math.max(top, h);
            low = Math.min(low, h);
        }
        if (top - low > 3) return false;
        int fy = top;

        BlockState planks = blk("darkplanks"), god = blk("godplanks"), oak = blk("darkoak"), fence = blk("darkfence");
        BlockState slab = has("darkslab") ? blk("darkslab") : planks;
        List<int[]> connect = new ArrayList<>();

        for (int lx = 0; lx <= 4; lx++) {
            for (int lz = 0; lz <= 4; lz++) {
                int x = bx + lx, z = bz + lz;
                boolean corner = (lx == 0 || lx == 4) && (lz == 0 || lz == 4);
                for (int y = fy - 1; y >= fy - 5; y--) {
                    pos.set(x, y, z);
                    if (!level.getBlockState(pos).isAir()) break;
                    level.setBlock(pos, corner && has("darkoak") ? oak : Blocks.DIRT.defaultBlockState(), 2);
                }
                put(level, pos, x, fy, z, !god.isAir() && rng.nextDouble() < 0.22 ? god : planks);
                boolean edge = lx == 0 || lx == 4 || lz == 0 || lz == 4;
                if (!edge) continue;
                if (corner) {
                    for (int yy = 1; yy <= 3; yy++) put(level, pos, x, fy + yy, z, has("darkoak") ? oak : planks);
                    continue;
                }
                if (lz == 0 && lx == 2) {
                    BlockState door = with(blk("darkdoors"), BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
                    put(level, pos, x, fy + 1, z, with(door, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
                    put(level, pos, x, fy + 2, z, with(door, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                    put(level, pos, x, fy + 3, z, planks);
                    continue;
                }
                if (lz == 4 && lx == 2) {
                    BlockState gate = with(blk("darkfencedoor"), BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
                    put(level, pos, x, fy + 1, z, gate);
                    if (!gate.isAir()) connect.add(new int[]{x, fy + 1, z});
                    put(level, pos, x, fy + 3, z, planks);
                    continue;
                }
                if ((lx == 0 || lx == 4) && lz == 2 && has("darktrapdoors")) {
                    BlockState tr = with(with(with(blk("darktrapdoors"), BlockStateProperties.OPEN, true), BlockStateProperties.HALF, Half.BOTTOM),
                            BlockStateProperties.HORIZONTAL_FACING, lx == 0 ? Direction.WEST : Direction.EAST);
                    put(level, pos, x, fy + 1, z, planks);
                    put(level, pos, x, fy + 2, z, tr);
                    put(level, pos, x, fy + 3, z, planks);
                    continue;
                }
                if ((lx == 0 || lx == 4) && has("darkfence")) {
                    for (int yy = 1; yy <= 2; yy++) {
                        if (rng.nextDouble() < 0.9) {
                            put(level, pos, x, fy + yy, z, fence);
                            connect.add(new int[]{x, fy + yy, z});
                        }
                    }
                    put(level, pos, x, fy + 3, z, planks);
                    continue;
                }
                for (int yy = 1; yy <= 3; yy++) {
                    if (yy == 1 || rng.nextDouble() < 0.82) put(level, pos, x, fy + yy, z, planks);
                }
            }
        }

        for (int lx = -1; lx <= 5; lx++) {
            for (int lz = -1; lz <= 5; lz++) {
                boolean inner = lx >= 1 && lx <= 3 && lz >= 1 && lz <= 3;
                if (inner) {
                    boolean mid = lx == 2 && lz == 2;
                    put(level, pos, bx + lx, fy + 4, bz + lz, mid && !god.isAir() ? god : planks);
                } else if (rng.nextDouble() < 0.88) {
                    put(level, pos, bx + lx, fy + 4, bz + lz, slab);
                }
            }
        }
        put(level, pos, bx + 2, fy + 5, bz + 2, blk("godleaves"));

        put(level, pos, bx + 2, fy + 1, bz + 2, blk("darkpressureplate"));
        pos.set(bx + 1, fy + 2, bz + 4);
        if (has("darkbutton") && !level.getBlockState(pos).isAir()) {
            BlockState btn = with(with(blk("darkbutton"), BlockStateProperties.ATTACH_FACE, AttachFace.WALL), BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
            put(level, pos, bx + 1, fy + 2, bz + 3, btn);
        }

        for (int[] c : connect) {
            pos.set(c[0], c[1], c[2]);
            BlockState cur = level.getBlockState(pos);
            level.setBlock(pos, Block.updateFromNeighbourShapes(cur, level, pos), 2);
        }
        return true;
    }

    private static boolean tall(WorldGenLevel level, BlockPos pos, Block block) {
        if (!level.getBlockState(pos.above()).isAir()) return false;
        DoublePlantBlock.placeAt(level, block.defaultBlockState(), pos, 2);
        return true;
    }
}
