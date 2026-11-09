package net.mcreator.underworldupdate.iles;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public final class BubbleDensity implements DensityFunction.SimpleFunction {
    public enum Mode { SOLID, TYPE, FLAVOR }

    public static final KeyDispatchDataCodec<BubbleDensity> SOLID_CODEC =
            KeyDispatchDataCodec.of(MapCodec.unit(() -> new BubbleDensity(Mode.SOLID)));
    public static final KeyDispatchDataCodec<BubbleDensity> TYPE_CODEC =
            KeyDispatchDataCodec.of(MapCodec.unit(() -> new BubbleDensity(Mode.TYPE)));
    public static final KeyDispatchDataCodec<BubbleDensity> FLAVOR_CODEC =
            KeyDispatchDataCodec.of(MapCodec.unit(() -> new BubbleDensity(Mode.FLAVOR)));

    private final Mode mode;

    private BubbleDensity(Mode mode) {
        this.mode = mode;
    }

    @Override
    public double compute(DensityFunction.FunctionContext ctx) {
        double x = ctx.blockX();
        double y = ctx.blockY();
        double z = ctx.blockZ();
        switch (mode) {
            case SOLID:
                return BubbleField.solid(x, y, z);
            case TYPE:
                return BubbleField.typeAt(x, y, z);
            default:
                return BubbleField.flavorAt(x, y, z) == 0 ? -1.0 : 1.0;
        }
    }

    @Override
    public double minValue() {
        return -1.0;
    }

    @Override
    public double maxValue() {
        return 1.0;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        switch (mode) {
            case SOLID:
                return SOLID_CODEC;
            case TYPE:
                return TYPE_CODEC;
            default:
                return FLAVOR_CODEC;
        }
    }
}
