package net.mcreator.underworldupdate.iles;

import net.minecraft.world.level.Level;

import java.util.Arrays;

public final class IslandScanner {
    public static final int MAX_ISLANDS = 32;

    private static final double RANGE_MARGIN = 24.0;

    public static volatile int unknownColumns = 0;

    private IslandScanner() {}

    public static int scan(Level level, double camX, double camY, double camZ, int radiusChunks, float[] out, float[] axes) {
        if (!BubbleField.isSeedKnown()) {
            Arrays.fill(out, 0.0f);
            Arrays.fill(axes, 0.0f);
            unknownColumns = 1;
            return 0;
        }
        unknownColumns = 0;

        return BubbleField.fillSpheres(camX, camY, camZ, radiusChunks * 16.0 + RANGE_MARGIN, out, axes);
    }

    public static float cameraPolarity(double x, double y, double z) {
        return BubbleField.isSeedKnown() ? (float) BubbleField.typeAt(x, y, z) : 0.0f;
    }
}
