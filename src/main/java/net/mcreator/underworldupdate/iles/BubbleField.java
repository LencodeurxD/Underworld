package net.mcreator.underworldupdate.iles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;

public final class BubbleField {
    private BubbleField() {}

    private static volatile long seed = 0L;
    private static volatile boolean seedKnown = false;

    public static void setSeed(long s) {
        if (!seedKnown || seed != s) {
            seed = s;
            seedKnown = true;
            GROUPS.clear();
            SMALLS.clear();
            RELICS.clear();
        }
    }

    public static long seed() { return seed; }
    public static boolean isSeedKnown() { return seedKnown; }

    public static final int CELL = 640;
    public static final int SMALL_CELL = 128;
    private static final double FLOOR_Y = 4.0;
    private static final double ROOF_Y = 214.0;
    private static final double GROUP_CHANCE = 0.80;
    private static final double SMALL_CHANCE = 0.92;
    private static final double SMALL_MARGIN = 45.0;
    private static final double WALL = 0.94;

    public static final int P_DOME = 0, P_MESA = 1, P_TERRACE = 2, P_ATOLL = 3, P_MOUNTAIN = 4, P_TWIN = 5, P_SPIRES = 6, P_VOLCANO = 7,
            P_ARCH = 8, P_CRESCENT = 9, P_HALO = 10, P_ORGAN = 11, P_LOTUS = 12,
            P_MAW = 13, P_COLONNADE = 14, P_SPIRAL = 15, P_PIT = 16, P_RIBS = 17;

    public static final int N_PORTAL = 0, N_BRIDGE = 1, N_OBELISK = 2, N_HELIX = 3, N_GEODE = 4;
    private static final int RELIC_CELL = 224;
    private static final double RELIC_CHANCE = 0.85;
    private static final double TAU = Math.PI * 2.0;

    public static final class Island {
        final double x, y, z, rx, rz, top, depth, off, ex;
        final int profile;
        final double a2, p2, a3, p3, a5, p5;
        final double warpMax;
        final double pw;
        final double wA;
        final double bx0, bx1, by0, by1, bz0, bz1;

        Island(double x, double y, double z, double rx, double rz, double top, double depth, int profile, double off, double ex,
               double a2, double p2, double a3, double p3, double a5, double p5) {
            this.x = x; this.y = y; this.z = z; this.rx = rx; this.rz = rz; this.top = top; this.depth = depth;
            this.profile = profile; this.off = off; this.ex = ex;
            this.a2 = a2; this.p2 = p2; this.a3 = a3; this.p3 = p3; this.a5 = a5; this.p5 = p5;
            this.warpMax = a2 + a3 + a5 + 0.16;
            this.pw = 0.42 + 0.55 * hash01(Double.doubleToLongBits(off) ^ 0x77L);
            this.wA = Math.min(16.0, 0.05 * Math.max(rx, rz));
            double w = 1.0 + warpMax;
            this.bx0 = x - rx * w - 2 - wA; this.bx1 = x + rx * w + 2 + wA;
            this.bz0 = z - rz * w - 2 - wA; this.bz1 = z + rz * w + 2 + wA;
            this.by0 = y - depth * 2.05 - 4; this.by1 = y + top * 1.45 + 8;
        }

        double rr(double px, double pz) {
            double ex_ = (px - x) / rx, ez_ = (pz - z) / rz;
            double r0 = Math.sqrt(ex_ * ex_ + ez_ * ez_);
            double th = Math.atan2(ez_, ex_);
            return r0 / outline(th, px, pz);
        }

        double outline(double th, double px, double pz) {
            double m = 1.0 + a2 * Math.cos(2 * th + p2) + a3 * Math.cos(3 * th + p3) + a5 * Math.cos(5 * th + p5)
                    + 0.10 * (vnoise2(off, px * 0.012, pz * 0.012) - 0.5) * 2.0
                    + 0.04 * (vnoise2(off + 31.7, px * 0.05, pz * 0.05) - 0.5) * 2.0;
            return Math.max(0.35, m);
        }
    }

    public static final class Bubble {
        public final long id;
        public final double x, y, z, ax, ay, az;
        public final int type;
        public final int flavor;
        public final int arch;
        public int roll;
        final double minAxis, maxAxis;
        final Island[] islands;
        final double[][] falls;
        final Island lake;
        final double lakeY;
        final double lakeR;

        Bubble(long id, double x, double y, double z, double ax, double ay, double az, int type, int flavor, int arch,
               Island[] islands, double[][] falls, Island lake, double lakeY, double lakeR) {
            this.id = id;
            this.x = x; this.y = y; this.z = z; this.ax = ax; this.ay = ay; this.az = az;
            this.type = type; this.flavor = flavor; this.arch = arch;
            this.minAxis = Math.min(ax, Math.min(ay, az));
            this.maxAxis = Math.max(ax, Math.max(ay, az));
            this.islands = islands; this.falls = falls; this.lake = lake; this.lakeY = lakeY; this.lakeR = lakeR;
        }

        double k(double px, double py, double pz) {
            double dx = (px - x) / ax, dy = (py - y) / ay, dz = (pz - z) / az;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    private static final class Group {
        final Bubble[] bubbles;
        Group(Bubble[] b) { bubbles = b; }
    }

    private static final class Small {
        final boolean enabled;
        final Island[] islets;
        Small(boolean enabled, Island[] islets) { this.enabled = enabled; this.islets = islets; }
    }

    private static final Group EMPTY_GROUP = new Group(new Bubble[0]);
    private static final Small NO_SMALL = new Small(false, new Island[0]);
    private static final ConcurrentHashMap<Long, Group> GROUPS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Small> SMALLS = new ConcurrentHashMap<>();

    private static long mix(long z) {
        z += 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    private static long cellHash(long s, int a, int b, int salt) {
        return mix(s ^ mix(a * 0x632BE59BD9B4E019L + mix(b * 0x1B873593L + salt)));
    }

    private static double hash01(long h) {
        return (mix(h) >>> 11) * (1.0 / (1L << 53));
    }

    private static double h2(double off, int xi, int yi) {
        return hash01(Double.doubleToLongBits(off) ^ (xi * 0x9E3779B97F4A7C15L) ^ (yi * 0xC2B2AE3D27D4EB4FL) ^ seed);
    }

    private static double vnoise2(double off, double x, double y) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);
        double fx = x - xi, fy = y - yi;
        fx = fx * fx * (3.0 - 2.0 * fx);
        fy = fy * fy * (3.0 - 2.0 * fy);
        double a = h2(off, xi, yi), b = h2(off, xi + 1, yi), c = h2(off, xi, yi + 1), d = h2(off, xi + 1, yi + 1);
        double ab = a + (b - a) * fx;
        double cd = c + (d - c) * fx;
        return ab + (cd - ab) * fy;
    }

    private static double ridge(double n) { return 1.0 - Math.abs(2.0 * n - 1.0); }

    private static double smooth01(double t) {
        t = Math.max(0.0, Math.min(1.0, t));
        return t * t * (3.0 - 2.0 * t);
    }

    private static double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }

    private static int cellIndex(double v, int size) {
        return Math.floorDiv((int) Math.floor(v) + size / 2, size);
    }

    private static double cellMin(int idx, int size) {
        return idx * (double) size - size / 2.0;
    }

    private static long key(int a, int b) {
        return ((long) a << 32) ^ (b & 0xFFFFFFFFL);
    }

    private static Group group(int ix, int iz) {
        return GROUPS.computeIfAbsent(key(ix, iz), k -> makeGroup(seed, ix, iz));
    }

    private static double extent(double[] e, double dx, double dy, double dz) {
        double a = dx / e[3], b = dy / e[4], c = dz / e[5];
        return 1.0 / Math.sqrt(a * a + b * b + c * c);
    }

    private static double kOf(double[] e, double px, double py, double pz) {
        double a = (px - e[0]) / e[3], b = (py - e[1]) / e[4], c = (pz - e[2]) / e[5];
        return Math.sqrt(a * a + b * b + c * c);
    }

    private static Group makeGroup(long sd, int ix, int iz) {
        SplittableRandom rng = new SplittableRandom(cellHash(sd, ix, iz, 1));
        boolean origin = (ix == 0 && iz == 0);
        if (!origin && rng.nextDouble() > GROUP_CHANCE) return EMPTY_GROUP;

        double x0 = cellMin(ix, CELL);
        double z0 = cellMin(iz, CELL);
        int type = rng.nextBoolean() ? 1 : -1;
        double u = rng.nextDouble();

        int kind = u < 0.28 ? 0 : (u < 0.60 ? 1 : (u < 0.80 ? 2 : 3));

        List<double[]> el = new ArrayList<>();
        double h0 = rng.nextDouble();
        double ax, az;
        if (kind == 0) ax = 195.0 + 95.0 * h0 * h0 * (3 - 2 * h0);
        else if (kind == 1) ax = 140.0 + 85.0 * h0;
        else if (kind == 2) ax = 105.0 + 70.0 * h0;
        else ax = 90.0 + 100.0 * h0;
        az = ax * (0.58 + 0.42 * rng.nextDouble());
        if (rng.nextBoolean()) { double t = ax; ax = az; az = t; }
        double ay = clamp(Math.min(ax, az) * (0.45 + 0.35 * rng.nextDouble()), 64.0, 103.0);
        double cx, cy, cz;
        if (origin) {
            type = 1; ax = 210.0; ay = 88.0; az = 170.0;
            cx = 0.0; cy = 112.0; cz = 0.0;
        } else {
            cx = x0 + ax + 12.0 + rng.nextDouble() * Math.max(0.0, CELL - 2.0 * (ax + 12.0));
            cz = z0 + az + 12.0 + rng.nextDouble() * Math.max(0.0, CELL - 2.0 * (az + 12.0));
            double lo = FLOOR_Y + ay, hi = ROOF_Y - ay;
            cy = hi > lo ? lo + rng.nextDouble() * (hi - lo) : (lo + hi) * 0.5;
        }
        el.add(new double[]{cx, cy, cz, ax, ay, az});

        int want = kind == 1 ? 2 : (kind == 2 ? 3 : 1);
        for (int s = 1; s < want; s++) {
            for (int attempt = 0; attempt < 18; attempt++) {
                double[] base = el.get(rng.nextInt(el.size()));
                double h = rng.nextDouble();
                double bax = (kind == 1 ? 85.0 + 70.0 * h : 80.0 + 60.0 * h);
                double baz = bax * (0.6 + 0.4 * rng.nextDouble());
                if (rng.nextBoolean()) { double t = bax; bax = baz; baz = t; }
                double bay = clamp(Math.min(bax, baz) * (0.50 + 0.35 * rng.nextDouble()), 58.0, 98.0);
                double th = rng.nextDouble() * Math.PI * 2.0;
                double ph = (rng.nextDouble() * 2.0 - 1.0) * 0.30;
                double dx = Math.cos(ph) * Math.cos(th), dy = Math.sin(ph), dz = Math.cos(ph) * Math.sin(th);
                double[] cand = {0, 0, 0, bax, bay, baz};
                double d = (extent(base, dx, dy, dz) + extent(cand, -dx, -dy, -dz)) * (0.55 + 0.22 * rng.nextDouble());
                double nx = base[0] + d * dx, ny = base[1] + d * dy, nz = base[2] + d * dz;
                if (nx < x0 + bax + 12.0 || nx > x0 + CELL - bax - 12.0 || nz < z0 + baz + 12.0 || nz > z0 + CELL - baz - 12.0) continue;
                if (ny < FLOOR_Y + bay || ny > ROOF_Y - bay) continue;
                cand[0] = nx; cand[1] = ny; cand[2] = nz;
                boolean ok = true;
                for (double[] o : el) {
                    if (kOf(o, nx, ny, nz) < 0.50 || kOf(cand, o[0], o[1], o[2]) < 0.50) { ok = false; break; }
                }
                if (!ok) continue;
                el.add(cand);
                break;
            }
        }

        Bubble[] out = new Bubble[el.size()];
        for (int i = 0; i < out.length; i++) {
            double[] e = el.get(i);
            out[i] = makeBubble(cellHash(sd, ix, iz, 10 + i), e[0], e[1], e[2], e[3], e[4], e[5], type, origin && i == 0);
        }
        return new Group(out);
    }

    private static int pickArch(SplittableRandom rng, int type) {
        double u = rng.nextDouble();
        if (type > 0) {
            if (u < 0.10) return P_ARCH;
            if (u < 0.20) return P_CRESCENT;
            if (u < 0.29) return P_HALO;
            if (u < 0.39) return P_ORGAN;
            if (u < 0.48) return P_LOTUS;
            u = (u - 0.48) / 0.52;
            if (u < 0.24) return P_MOUNTAIN;
            if (u < 0.42) return P_TERRACE;
            if (u < 0.56) return P_ATOLL;
            if (u < 0.68) return P_MESA;
            if (u < 0.80) return P_TWIN;
            if (u < 0.90) return P_VOLCANO;
            return P_DOME;
        }

        if (u < 0.10) return P_MAW;
        if (u < 0.20) return P_COLONNADE;
        if (u < 0.29) return P_SPIRAL;
        if (u < 0.38) return P_PIT;
        if (u < 0.47) return P_RIBS;
        u = (u - 0.47) / 0.53;
        if (u < 0.28) return P_SPIRES;
        if (u < 0.50) return P_VOLCANO;
        if (u < 0.68) return P_MOUNTAIN;
        if (u < 0.78) return P_MESA;
        if (u < 0.88) return P_TWIN;
        if (u < 0.95) return P_ATOLL;
        return P_TERRACE;
    }

    private static double hmaxOf(int arch) {
        switch (arch) {
            case P_MOUNTAIN: return 1.38;
            case P_TWIN: return 1.32;
            case P_SPIRES: return 1.16;
            case P_VOLCANO: return 1.35;
            case P_ARCH: return 1.05;
            case P_CRESCENT: return 1.10;
            case P_HALO: return 1.30;
            case P_LOTUS: return 1.25;
            case P_ORGAN: return 1.12;
            case P_MAW: return 1.35;
            case P_COLONNADE: return 1.25;
            case P_SPIRAL: return 1.25;
            case P_RIBS: return 1.15;
            default: return 1.05;
        }
    }

    private static Bubble makeBubble(long hseed, double cx, double cy, double cz, double ax, double ay, double az, int type, boolean origin) {
        SplittableRandom rng = new SplittableRandom(hseed);
        int arch = origin ? P_MESA : pickArch(rng, type);
        int flavor = origin ? 0 : (rng.nextDouble() < 0.5 ? 0 : 1);

        double planeFrac = -(0.02 + 0.16 * rng.nextDouble());
        double planeY = cy + planeFrac * ay;
        double topF;
        switch (arch) {
            case P_MOUNTAIN: topF = 0.90 + 0.15 * rng.nextDouble(); break;
            case P_SPIRES:   topF = 0.95 + 0.15 * rng.nextDouble(); break;
            case P_TWIN:     topF = 0.85 + 0.15 * rng.nextDouble(); break;
            case P_VOLCANO:  topF = 0.90 + 0.15 * rng.nextDouble(); break;
            case P_MESA:     topF = 0.45 + 0.20 * rng.nextDouble(); break;
            case P_TERRACE:  topF = 0.70 + 0.20 * rng.nextDouble(); break;
            case P_ATOLL:    topF = 0.50 + 0.15 * rng.nextDouble(); break;
            case P_ARCH:     topF = 0.75 + 0.20 * rng.nextDouble(); break;
            case P_CRESCENT: topF = 0.60 + 0.25 * rng.nextDouble(); break;
            case P_HALO:     topF = 0.70 + 0.20 * rng.nextDouble(); break;
            case P_ORGAN:    topF = 0.55 + 0.20 * rng.nextDouble(); break;
            case P_LOTUS:    topF = 0.60 + 0.25 * rng.nextDouble(); break;
            case P_MAW:      topF = 0.75 + 0.20 * rng.nextDouble(); break;
            case P_COLONNADE: topF = 0.70 + 0.20 * rng.nextDouble(); break;
            case P_SPIRAL:   topF = 0.80 + 0.20 * rng.nextDouble(); break;
            case P_PIT:      topF = 0.50 + 0.20 * rng.nextDouble(); break;
            case P_RIBS:     topF = 0.65 + 0.20 * rng.nextDouble(); break;
            default:         topF = 0.50 + 0.20 * rng.nextDouble(); break;
        }
        double top = ay * topF;
        double depth = ay * (0.55 + 0.30 * rng.nextDouble());
        double hmax = hmaxOf(arch);
        if (origin) { planeY = 86.0; top = 14.0; depth = 40.0; planeFrac = (planeY - cy) / ay; }
        top = Math.min(top, (cy + 0.88 * ay - planeY - 4.0) / hmax);
        depth = Math.min(depth, (planeY - (cy - 0.90 * ay)) / 2.0);
        depth = Math.max(depth, 8.0);

        double w2 = rng.nextDouble(), w3 = rng.nextDouble(), w5 = rng.nextDouble();
        double wsum = Math.max(0.001, w2 + w3 + w5);
        double amp = origin ? 0.16 : 0.22 + 0.16 * rng.nextDouble();
        double a2 = amp * w2 / wsum, a3 = amp * w3 / wsum, a5 = amp * w5 / wsum;
        double hz = Math.sqrt(Math.max(0.04, WALL * WALL - planeFrac * planeFrac));
        double wmax = 1.0 + a2 + a3 + a5 + 0.16;
        double rxI = ax * hz / wmax * 0.98, rzI = az * hz / wmax * 0.98;
        double ox = cx, oz = cz;
        if (!origin) { ox += (rng.nextDouble() - 0.5) * 0.10 * ax; oz += (rng.nextDouble() - 0.5) * 0.10 * az; }
        double ex = 0.0;
        if (arch == P_TERRACE) ex = 3 + rng.nextInt(3);
        if (arch == P_TWIN) ex = rng.nextDouble() * Math.PI;
        if (arch >= P_ARCH) ex = rng.nextDouble() * Math.PI;
        Island main = new Island(ox, planeY, oz, rxI, rzI, top, depth, arch, rng.nextDouble() * 1000.0, ex,
                a2, rng.nextDouble() * 6.283, a3, rng.nextDouble() * 6.283, a5, rng.nextDouble() * 6.283);

        List<Island> list = new ArrayList<>();
        list.add(main);

        int count = (int) Math.round(Math.min(ax, az) / 50.0 + rng.nextDouble() * 2.5);
        count = (int) clamp(count, 2, 9);
        for (int n = 0; n < count; n++) {
            for (int attempt = 0; attempt < 16; attempt++) {
                double rI = rng.nextDouble() < 0.15 ? 42.0 + 30.0 * rng.nextDouble() : 12.0 + 32.0 * rng.nextDouble() * rng.nextDouble();
                double aspect = 0.7 + 0.7 * rng.nextDouble();
                double rxs = rI * aspect, rzs = rI / aspect;
                double th = rng.nextDouble() * Math.PI * 2.0;
                double ph = (rng.nextDouble() * 2.0 - 1.0) * 0.9;
                double kk = 0.25 + 0.55 * rng.nextDouble();
                double ix = cx + ax * kk * Math.cos(ph) * Math.cos(th);
                double iy = cy + ay * kk * Math.sin(ph);
                double iz = cz + az * kk * Math.cos(ph) * Math.sin(th);
                double ts = rI * (0.30 + 0.50 * rng.nextDouble());
                double ds = rI * (0.7 + 0.9 * rng.nextDouble());
                double pr = rng.nextDouble();
                int prof;
                if (pr < 0.34) prof = P_DOME;
                else if (pr < 0.54) prof = P_MESA;
                else if (pr < (type < 0 ? 0.80 : 0.62)) prof = P_SPIRES;
                else if (pr < 0.80) prof = P_TERRACE;
                else prof = P_MOUNTAIN;
                if (rI < 22.0 && (prof == P_TERRACE)) prof = P_DOME;
                double sx = 0.0;
                if (prof == P_TERRACE) sx = 3;
                Island s = new Island(ix, iy, iz, rxs, rzs, ts, ds, prof, rng.nextDouble() * 1000.0, sx,
                        0.10, rng.nextDouble() * 6.283, 0.08, rng.nextDouble() * 6.283, 0.04, rng.nextDouble() * 6.283);
                if (!fitsInside(s, cx, cy, cz, ax, ay, az)) continue;
                boolean ok = true;
                for (Island o : list) {
                    if (s.bx1 + 6 > o.bx0 && s.bx0 - 6 < o.bx1 && s.bz1 + 6 > o.bz0 && s.bz0 - 6 < o.bz1
                            && s.by1 + 6 > o.by0 && s.by0 - 6 < o.by1) { ok = false; break; }
                }
                if (!ok) continue;
                list.add(s);
                break;
            }
        }

        List<double[]> falls = new ArrayList<>();
        int nFalls;
        switch (arch) {
            case P_ATOLL: case P_VOLCANO: nFalls = 1; break;
            case P_MESA: case P_TERRACE: nFalls = 2 + (Math.min(ax, az) > 150 ? 1 : 0); break;
            case P_SPIRES: nFalls = 1 + rng.nextInt(2); break;
            default: nFalls = 1 + rng.nextInt(2); break;
        }
        double base = rng.nextDouble() * Math.PI * 2.0;
        for (int f = 0; f < nFalls; f++) {
            double th = base + f * (Math.PI * 2.0 / nFalls) + (rng.nextDouble() - 0.5) * 0.9;
            double rrT = type < 0 ? 0.965 + 0.015 * rng.nextDouble() : 0.92 + 0.04 * rng.nextDouble();
            double[] p = rimPoint(main, th, rrT);
            if (p != null) falls.add(new double[]{p[0], p[1], p[2], type < 0 ? 1.0 : 0.0});
        }

        Island lake = (arch == P_ATOLL || arch == P_VOLCANO) ? main : null;
        double lakeY = arch == P_ATOLL ? planeY + top * 0.30 : (arch == P_VOLCANO ? planeY + top * 0.40 : 0.0);
        double lakeR = arch == P_ATOLL ? 0.50 : 0.11;
        Bubble made = new Bubble(hseed, cx, cy, cz, ax, ay, az, type, flavor, arch, list.toArray(new Island[0]), falls.toArray(new double[0][]), lake, lakeY, lakeR);
        made.roll = origin ? 0 : flavor + 2 * (int) (mix(hseed ^ 0x5DEECE66DL) >>> 40);
        return made;
    }

    private static boolean fitsInside(Island s, double cx, double cy, double cz, double ax, double ay, double az) {
        double[] xs = {s.bx0, s.bx1};
        double[] ys = {s.by0, s.by1};
        double[] zs = {s.bz0, s.bz1};
        for (double x : xs) for (double y : ys) for (double z : zs) {
            double a = (x - cx) / ax, b = (y - cy) / ay, c = (z - cz) / az;
            if (a * a + b * b + c * c > 0.90 * 0.90) return false;
        }
        return true;
    }

    private static double[] rimPoint(Island is, double th, double rrTarget) {
        double c = Math.cos(th), s = Math.sin(th);
        for (int i = 0; i < 60; i++) {
            double r = 1.5 - i * (1.2 / 60.0);
            double px = is.x + c * r * is.rx, pz = is.z + s * r * is.rz;
            if (is.rr(px, pz) <= rrTarget) {
                double rr = is.rr(px, pz);
                double h = profileHeight(is, rr, 1.0 - rr, px, pz);
                return new double[]{px, pz, is.y + is.top * h};
            }
        }
        return null;
    }

    private static Small small(int sx, int sz) {
        return SMALLS.computeIfAbsent(key(sx, sz), k -> makeSmall(seed, sx, sz));
    }

    private static int smallFlavor(long sd, int sx, int sz) {
        return (int) (cellHash(sd, sx, sz, 3) & 1L);
    }

    private static Small makeSmall(long sd, int sx, int sz) {
        SplittableRandom rng = new SplittableRandom(cellHash(sd, sx, sz, 2));
        if (rng.nextDouble() > SMALL_CHANCE) return NO_SMALL;
        double cx = cellMin(sx, SMALL_CELL) + 36.0 + rng.nextDouble() * (SMALL_CELL - 72.0);
        double cz = cellMin(sz, SMALL_CELL) + 36.0 + rng.nextDouble() * (SMALL_CELL - 72.0);
        double cy = 25.0 + rng.nextDouble() * 170.0;
        if (!outsideBubbles(cx, cy, cz, SMALL_MARGIN + 30.0)) return NO_SMALL;
        if (nearRelic(cx, cy, cz, 40.0)) return NO_SMALL;

        Island[] islets = new Island[3];
        double base = rng.nextDouble() * Math.PI * 2.0;
        for (int i = 0; i < 3; i++) {
            double th = base + i * (Math.PI * 2.0 / 3.0) + (rng.nextDouble() - 0.5) * 0.7;
            double dist = 14.0 + rng.nextDouble() * 12.0;
            double ri = 5.5 + rng.nextDouble() * 5.5;
            double di = ri * (1.0 + 0.6 * rng.nextDouble());
            double ti = ri * (0.30 + 0.25 * rng.nextDouble());
            double yo = (rng.nextDouble() * 2.0 - 1.0) * 8.0;
            islets[i] = new Island(cx + dist * Math.cos(th), cy + yo, cz + dist * Math.sin(th), ri, ri * (0.8 + 0.4 * rng.nextDouble()), ti, di,
                    P_DOME, rng.nextDouble() * 1000.0, 0.0,
                    0.08, rng.nextDouble() * 6.283, 0.06, rng.nextDouble() * 6.283, 0.03, rng.nextDouble() * 6.283);
        }
        return new Small(true, islets);
    }

    private static boolean outsideBubbles(double x, double y, double z, double margin) {
        int ix = cellIndex(x, CELL), iz = cellIndex(z, CELL);
        for (int a = ix - 1; a <= ix + 1; a++) {
            for (int b = iz - 1; b <= iz + 1; b++) {
                for (Bubble s : group(a, b).bubbles) {
                    double k = s.k(x, y, z);
                    double dx = x - s.x, dy = y - s.y, dz = z - s.z;
                    double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    double dist = k <= 1.0e-9 ? -s.minAxis : len * (1.0 - 1.0 / k);
                    if (dist < margin) return false;
                }
            }
        }
        return true;
    }

    private static double spireHeight(Island is, double x, double z) {
        double best = 0.0;
        int cx = (int) Math.floor(x / 34.0), cz = (int) Math.floor(z / 34.0);
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                long hh = Double.doubleToLongBits(is.off) ^ ((cx + a) * 0x9E3779B97F4A7C15L) ^ ((cz + b) * 0xC2B2AE3D27D4EB4FL) ^ seed;
                if (hash01(hh) > 0.68) continue;
                double px = (cx + a + 0.2 + 0.6 * hash01(hh ^ 0x1111L)) * 34.0;
                double pz = (cz + b + 0.2 + 0.6 * hash01(hh ^ 0x2222L)) * 34.0;
                double hs = 0.30 + 0.70 * hash01(hh ^ 0x3333L);
                double rad = 5.0 + 8.0 * hash01(hh ^ 0x4444L);
                double d = Math.sqrt((x - px) * (x - px) + (z - pz) * (z - pz));
                if (d < rad) best = Math.max(best, hs * Math.pow(1.0 - d / rad, 0.9));
            }
        }
        return best;
    }

    private static double profileHeight(Island is, double rr, double s, double x, double z) {
        switch (is.profile) {
            case P_DOME:
                return Math.pow(Math.max(0.0, 1.0 - rr * rr), 0.75);
            case P_MESA:
                return Math.pow(Math.min(1.0, s / 0.14), 0.5);
            case P_TERRACE: {
                double n = Math.max(2.0, is.ex);
                double v = Math.pow(s, 0.85) * n;
                double k = Math.floor(v);
                double f = v - k;
                return Math.min(1.0, (k + smooth01((f - 0.6) / 0.4)) / n);
            }
            case P_ATOLL: {
                double q = (rr - 0.72) / 0.22;
                double ring = Math.exp(-q * q);
                return (0.12 + 1.00 * ring) * Math.pow(Math.min(1.0, s / 0.10), 0.5);
            }
            case P_MOUNTAIN: {
                double cone = Math.pow(s, 1.35);
                double r1 = ridge(vnoise2(is.off + 5.5, x * 0.022, z * 0.022));
                double r2 = ridge(vnoise2(is.off + 8.8, x * 0.05, z * 0.05));
                double r = 0.55 * r1 * r1 + 0.25 * r2 * r2;
                return Math.min(1.35, 0.16 * smooth01(s / 0.3) + cone * (0.55 + 0.9 * r));
            }
            case P_TWIN: {
                double ca = Math.cos(is.ex), sa = Math.sin(is.ex);
                double ex_ = (x - is.x) / is.rx, ez_ = (z - is.z) / is.rz;
                double u = ex_ * ca + ez_ * sa, v = -ex_ * sa + ez_ * ca;
                double d1 = Math.hypot(u - 0.42, v * 1.3), d2 = Math.hypot(u + 0.42, v * 1.3);
                double c1 = Math.pow(Math.max(0.0, 1.0 - d1 / 0.50), 1.4);
                double c2 = 0.8 * Math.pow(Math.max(0.0, 1.0 - d2 / 0.45), 1.4);
                double r1 = ridge(vnoise2(is.off + 6.6, x * 0.03, z * 0.03));
                return Math.min(1.3, 0.20 * smooth01(s / 0.3) + Math.max(c1, c2) * (0.70 + 0.60 * r1 * r1));
            }
            case P_VOLCANO: {
                double q = (rr - 0.30) / 0.26;
                double ringV = 1.15 * Math.exp(-q * q) * (1.0 - 0.65 * Math.exp(-(rr / 0.15) * (rr / 0.15)));
                double r1 = ridge(vnoise2(is.off + 7.7, x * 0.04, z * 0.04));
                return Math.min(1.35, 0.12 * smooth01(s / 0.3) + ringV * (0.85 + 0.3 * r1));
            }
            case P_ARCH:
                return heightArch(is, x, z);
            case P_CRESCENT:
                return heightCrescent(is, x, z);
            case P_HALO:
                return heightHalo(is, rr);
            case P_ORGAN:
                return heightOrgan(is, x, z);
            case P_LOTUS:
                return heightLotus(is, rr, x, z);
            case P_MAW:
                return heightMaw(is, rr, x, z);
            case P_COLONNADE:
                return heightColonnade(is, rr, x, z);
            case P_SPIRAL:
                return heightSpiral(is, rr, x, z);
            case P_PIT:
                return heightPit(is, rr);
            case P_RIBS:
                return heightRibs(is, x, z);
            default: {
                double sp = spireHeight(is, x, z);
                return 0.14 * smooth01(s / 0.25) + sp * Math.pow(Math.min(1.0, s * 1.6), 0.4);
            }
        }
    }

    private static double ih(Island is, int salt) {
        return hash01(Double.doubleToLongBits(is.off) * 0x9E3779B97F4A7C15L + salt * 0xC2B2AE3D27D4EB4FL + seed);
    }

    private static double cellRand(Island is, long a, long b) {
        return hash01(Double.doubleToLongBits(is.off) ^ (a * 0x9E3779B97F4A7C15L) ^ (b * 0xC2B2AE3D27D4EB4FL) ^ seed);
    }

    private static double sq(double v) { return v * v; }

    private static double angDiff(double a, double b) {
        double d = a - b;
        while (d > Math.PI) d -= TAU;
        while (d < -Math.PI) d += TAU;
        return d;
    }

    private static double ellipseDist(double a, double b, double px, double py) {
        double e = Math.sqrt(sq(px / a) + sq(py / b));
        if (e < 1.0e-6) return -Math.min(a, b);
        double gx = px / (a * a * e), gy = py / (b * b * e);
        double g = Math.sqrt(gx * gx + gy * gy);
        return (e - 1.0) / Math.max(g, 1.0e-9);
    }

    private static double pitRadius(Island is) {
        return clamp(0.26 * Math.sqrt(is.rx * is.rz), 9.0, 30.0);
    }

    private static boolean flatTop(int profile) {
        return profile == P_SPIRES || profile == P_ORGAN || profile == P_COLONNADE || profile == P_RIBS || profile == P_LOTUS || profile == P_SPIRAL;
    }

    private static double heightArch(Island is, double x, double z) {
        double nx = (x - is.x) / is.rx, nz = (z - is.z) / is.rz;
        double v = -nx * Math.sin(is.ex) + nz * Math.cos(is.ex);
        double f = clamp((0.26 - Math.abs(v)) / 0.11, 0.0, 1.0);
        double rg = 0.85 + 0.15 * ridge(vnoise2(is.off + 9.1, x * 0.03, z * 0.03));
        return 0.18 + 0.80 * smooth01(f) * rg;
    }

    private static double heightCrescent(Island is, double x, double z) {
        double nx = (x - is.x) / is.rx, nz = (z - is.z) / is.rz;
        double phi = ih(is, 1) * TAU;
        double dl = Math.abs(angDiff(Math.atan2(nz, nx), phi));
        double horn = Math.exp(-sq((dl - 0.98) / 0.42));
        double inner = smooth01((Math.hypot(nx, nz) - 0.35) / 0.35);
        double rg = ridge(vnoise2(is.off + 4.4, x * 0.03, z * 0.03));
        return 0.28 + 0.78 * horn * inner + 0.14 * rg;
    }

    private static double heightHalo(Island is, double rr) {
        double rav = Math.sqrt(is.rx * is.rz);
        double rt = clamp(0.12 * rav, 9.0, 20.0);
        double tower = smooth01((rt + 7.0 - rr * rav) / 14.0);
        double plate = 0.14 + 0.16 * smooth01((0.62 - rr) / 0.14);
        return Math.max(plate, 0.14 + 0.98 * tower);
    }

    private static double heightOrgan(Island is, double x, double z) {
        double rav = Math.sqrt(is.rx * is.rz);
        double size = clamp(0.075 * rav, 12.0, 22.0);
        double ca = Math.cos(is.ex), sa = Math.sin(is.ex);
        double dx = x - is.x, dz = z - is.z;
        double px = dx * ca + dz * sa, pz = -dx * sa + dz * ca;
        double qa = (0.5773502691896258 * px - pz / 3.0) / size;
        double qc = (2.0 / 3.0) * pz / size;
        double qb = -qa - qc;
        long ra = Math.round(qa), rb = Math.round(qb), rc = Math.round(qc);
        double da = Math.abs(ra - qa), db = Math.abs(rb - qb), dc = Math.abs(rc - qc);
        if (da > db && da > dc) ra = -rb - rc;
        else if (db > dc) rb = -ra - rc;
        else rc = -ra - rb;
        double hx = size * 1.7320508075688772 * (ra + rc / 2.0);
        double hz = size * 1.5 * rc;
        double lx = px - hx, lz = pz - hz;
        double apothem = size * 0.8660254037844386;
        double hexn = Math.max(Math.abs(lx), 0.5 * Math.abs(lx) + 0.8660254037844386 * Math.abs(lz));
        double t = clamp((apothem - 2.5 - hexn) / 2.0, 0.0, 1.0);
        double bx = hx * ca - hz * sa, bz = hx * sa + hz * ca;
        double rc2 = sq(bx / is.rx) + sq(bz / is.rz);
        double env = clamp(1.05 - 0.8 * rc2, 0.15, 1.0);
        double hc = 0.34 + 0.80 * (0.55 * env + 0.45 * cellRand(is, ra, rc));
        return 0.10 + (hc - 0.10) * t;
    }

    private static double heightLotus(Island is, double rr, double x, double z) {
        double th = Math.atan2((z - is.z) / is.rz, (x - is.x) / is.rx);
        int n = 6 + (int) (ih(is, 1) * 3.0);
        double ph = ih(is, 2) * TAU;
        double[] ring = {0.80, 0.57, 0.35};
        double[] peak = {0.50, 0.84, 1.14};
        double[] wid = {0.24, 0.21, 0.17};
        double h = 0.10;
        for (int j = 0; j < 3; j++) {
            double lobe = Math.pow(0.5 + 0.5 * Math.cos(n * th + ph + j * Math.PI), 3.0);
            double rad = smooth01(1.0 - Math.abs(rr - ring[j]) / wid[j]);
            h = Math.max(h, 0.10 + (peak[j] - 0.10) * lobe * rad);
        }
        return Math.max(h, 0.10 + 1.15 * smooth01(1.0 - rr / 0.17));
    }

    private static double heightMaw(Island is, double rr, double x, double z) {
        double rav = Math.sqrt(is.rx * is.rz);
        double th = Math.atan2((z - is.z) / is.rz, (x - is.x) / is.rx);
        int n = 11 + (int) (ih(is, 1) * 5.0);
        double ph = ih(is, 2) * TAU;
        double step = TAU / n;
        long k = Math.round((th - ph) / step);
        double thk = ph + k * step;
        double d = Math.hypot(rr - 0.66, (th - thk) * 0.66);
        int idx = Math.floorMod((int) k, n);
        double big = (idx & 1) == 0 ? 1.0 : 0.72;
        double hf = (0.80 + 0.45 * cellRand(is, idx, 7)) * big;
        double fr = clamp(0.17 * rav, 9.0, 24.0) / rav;
        double fang = hf * Math.pow(clamp(1.0 - d / fr, 0.0, 1.0), 0.9);
        double gum = 0.28 * Math.exp(-sq((rr - 0.66) / 0.08));
        double pit = -0.34 * smooth01(1.0 - rr / 0.34);
        return Math.max(0.12 + gum + pit, fang);
    }

    private static double pillarH(Island is, int idx) {
        double v = cellRand(is, idx, 11);
        if (v < 0.15) return 0.28 + v;
        return 0.75 + 0.45 * cellRand(is, idx, 13);
    }

    private static double heightColonnade(Island is, double rr, double x, double z) {
        double rav = Math.sqrt(is.rx * is.rz);
        double th = Math.atan2((z - is.z) / is.rz, (x - is.x) / is.rx);
        int n = 10 + (int) (ih(is, 1) * 5.0);
        double ph = ih(is, 2) * TAU;
        double step = TAU / n;
        long k = Math.round((th - ph) / step);
        double thk = ph + k * step;
        double rp = clamp(0.07 * rav, 6.0, 13.0);
        double d = Math.hypot((rr - 0.68) * rav, (th - thk) * 0.68 * rav);
        double pil = pillarH(is, Math.floorMod((int) k, n)) * smooth01((rp * 1.6 - d) / (rp * 0.6));
        double altar = 0.12 + 0.20 * smooth01((0.30 - rr) / 0.12);
        return Math.max(Math.max(0.12, altar), pil);
    }

    private static double heightSpiral(Island is, double rr, double x, double z) {
        double th = Math.atan2((z - is.z) / is.rz, (x - is.x) / is.rx);
        double n = 5.0 + Math.floor(ih(is, 1) * 3.0);
        double dir = ih(is, 3) < 0.5 ? 1.0 : -1.0;
        double v = (1.0 - rr) * n + dir * th / TAU;
        double f = v - Math.floor(v);
        double lvl = Math.floor(v) + smooth01((f - 0.45) / 0.25);
        double h = Math.min(1.0, lvl / n);
        return Math.max(0.10, h) + 0.20 * smooth01(1.0 - rr / 0.10);
    }

    private static double heightPit(Island is, double rr) {
        double rav = Math.sqrt(is.rx * is.rz);
        double hole = pitRadius(is) / rav;
        double mound = 0.50 * Math.pow(smooth01((1.0 - rr) / 0.45), 0.7);
        double rimC = hole * 1.45 + 0.07;
        double rim = 0.30 * Math.exp(-sq((rr - rimC) / 0.085));
        return 0.12 + mound + rim;
    }

    private static double heightRibs(Island is, double x, double z) {
        double nx = (x - is.x) / is.rx, nz = (z - is.z) / is.rz;
        double v = -nx * Math.sin(is.ex) + nz * Math.cos(is.ex);
        return 0.14 + 0.22 * smooth01((0.09 - Math.abs(v)) / 0.05);
    }

    private static double shape(Island is, double x, double y, double z, double dens, double nx, double nz, double rr) {
        switch (is.profile) {
            case P_ARCH:
                return Math.min(dens, archCut(is, y, nx, nz) / 9.0);
            case P_CRESCENT:
                return crescent(is, x, y, z, nx, nz, dens);
            case P_HALO:
                return Math.max(dens, haloRings(is, x, y, z));
            case P_COLONNADE:
                return Math.max(dens, lintels(is, y, rr, nx, nz));
            case P_PIT:
                return Math.min(dens, pitCut(is, x, y, z, nx, nz) / 9.0);
            case P_RIBS:
                return Math.max(dens, ribs(is, y, nx, nz));
            default:
                return dens;
        }
    }

    private static double archCut(Island is, double y, double nx, double nz) {
        double rav = Math.sqrt(is.rx * is.rz);
        double ub = (nx * Math.cos(is.ex) + nz * Math.sin(is.ex)) * rav;
        int n = ih(is, 1) < 0.5 ? 2 : 3;
        double f = is.y + 0.14 * is.top;
        double b = 0.58 * is.top;
        double best = 1.0e9;
        for (int k = 0; k < n; k++) {
            double pos = n == 2 ? (k == 0 ? -0.36 : 0.36) : (k - 1) * 0.56;
            double uc = (pos + (ih(is, 20 + k) - 0.5) * 0.12) * rav;
            double a = clamp(0.19 * rav * (0.85 + 0.30 * ih(is, 30 + k)), 18.0, 52.0);
            double hole = Math.max(ellipseDist(a, b, ub - uc, y - f), f - y);
            best = Math.min(best, hole);
        }
        return best;
    }

    private static double crescent(Island is, double x, double y, double z, double nx, double nz, double dens) {
        double rav = Math.sqrt(is.rx * is.rz);
        double phi = ih(is, 1) * TAU;
        double cxn = 0.34 * Math.cos(phi), czn = 0.34 * Math.sin(phi);
        double dn = Math.hypot(nx - cxn, nz - czn) - 0.86;
        double v = Math.min(dens, dn * rav / 9.0);
        double ox = is.x + cxn * is.rx, oz = is.z + czn * is.rz, oy = is.y + 0.78 * is.top;
        double rs = clamp(0.10 * rav, 7.0, 16.0);
        double d = Math.sqrt(sq(x - ox) + sq(y - oy) + sq(z - oz)) - rs;
        return Math.max(v, -d / 9.0);
    }

    private static double haloRings(Island is, double x, double y, double z) {
        double rav = Math.sqrt(is.rx * is.rz);
        double rmin = Math.min(is.rx, is.rz);
        double rm = clamp(0.045 * rav, 6.0, 12.0);
        double[] rn = {0.40, 0.62, 0.84};
        double[] yk = {0.62, 0.90, 0.44};
        double dx = x - is.x, dz = z - is.z;
        double rho = Math.hypot(dx, dz);
        double best = -1.0;
        for (int k = 0; k < 3; k++) {
            double rb = rn[k] * rmin;
            double tilt = (ih(is, 40 + k) * 2.0 - 1.0) * Math.min(0.20, 0.22 * is.top / rb);
            double al = ih(is, 50 + k) * TAU;
            double along = dx * Math.cos(al) + dz * Math.sin(al);
            double yc = is.y + yk[k] * is.top + tilt * along;
            double d = Math.hypot(rho - rb, y - yc) - rm;
            best = Math.max(best, -d / 9.0);
        }
        return best;
    }

    private static double lintels(Island is, double y, double rr, double nx, double nz) {
        double rav = Math.sqrt(is.rx * is.rz);
        double th = Math.atan2(nz, nx);
        int n = 10 + (int) (ih(is, 1) * 5.0);
        double ph = ih(is, 2) * TAU;
        double step = TAU / n;
        long k = Math.round((th - ph) / step);
        double dl = th - (ph + k * step);
        long kn = k + (dl >= 0.0 ? 1 : -1);
        int ia = Math.floorMod((int) k, n), ib = Math.floorMod((int) kn, n);
        double hm = Math.min(pillarH(is, ia), pillarH(is, ib));
        if (hm < 0.7) return -1.0;
        int pair = Math.floorMod((int) Math.min(k, kn), n);
        if (cellRand(is, pair, 17) > 0.6) return -1.0;
        double rp = clamp(0.07 * rav, 6.0, 13.0);
        double tl = clamp(rp * 1.8, 9.0, 16.0);
        double dyl = y - (is.y + hm * is.top);
        double d = Math.max(Math.abs((rr - 0.68) * rav) - rp * 0.95, Math.max(dyl, -tl - dyl));
        return -d / 9.0;
    }

    private static double pitCut(Island is, double x, double y, double z, double nx, double nz) {
        double rav = Math.sqrt(is.rx * is.rz);
        double wob = 1.0 + 0.22 * (vnoise2(is.off + 3.3, x * 0.045, z * 0.045) - 0.5) * 2.0;
        double rel = clamp((y - is.y) / Math.max(1.0, is.top), 0.0, 1.0);
        double rad = pitRadius(is) * wob * (1.0 + 0.45 * rel);
        return Math.hypot(nx, nz) * rav - rad;
    }

    private static double ribs(Island is, double y, double nx, double nz) {
        double rav = Math.sqrt(is.rx * is.rz);
        double ca = Math.cos(is.ex), sa = Math.sin(is.ex);
        double ub = (nx * ca + nz * sa) * rav, vb = (-nx * sa + nz * ca) * rav;
        double sp = clamp(0.16 * rav, 24.0, 40.0);
        double uc = (Math.floor(ub / sp) + 0.5) * sp;
        if (Math.abs(uc) > 0.72 * rav) return -1.0;
        double e = uc / (0.8 * rav);
        double hh = is.top * (1.08 - 0.50 * e * e);
        double bv = 0.46 * rav * (1.0 - 0.40 * e * e);
        double g = is.y + 0.14 * is.top;
        double tr = clamp(0.04 * rav, 5.5, 9.0);
        double d = Math.max(Math.max(Math.abs(ellipseDist(bv, hh, vb, y - g)) - tr, Math.abs(ub - uc) - tr), -(y - g) - 3.0);
        return -d / 9.0;
    }


    private static double islandDensity(Island is, double x, double y, double z) {
        if (y < is.by0 || y > is.by1 || x < is.bx0 || x > is.bx1 || z < is.bz0 || z > is.bz1) return -1.0;
        double dy = y - is.y;

        double wx = x + is.wA * (vnoise2(is.off + 61.1, y * 0.035, z * 0.02) - 0.5) * 2.0;
        double wz = z + is.wA * (vnoise2(is.off + 83.9, y * 0.035, x * 0.02) - 0.5) * 2.0;
        double ex_ = (wx - is.x) / is.rx, ez_ = (wz - is.z) / is.rz;
        double r0 = Math.sqrt(ex_ * ex_ + ez_ * ez_);
        if (r0 > 1.0 + is.warpMax + 0.05) return -1.0;
        double th = Math.atan2(ez_, ex_);
        double rr = r0 / is.outline(th, wx, wz);
        double dens;
        if (rr >= 1.0) {
            dens = Math.max(-1.0, -(rr - 1.0) * 3.0 - 0.02);
        } else {
            double s = 1.0 - rr;
            double h = profileHeight(is, rr, s, wx, wz);
            double hillA = Math.max(2.0, 0.07 * is.top);
            double hills = flatTop(is.profile) ? 0.0
                    : (hillA * (vnoise2(is.off + 13.1, wx * 0.014, wz * 0.014) - 0.5) * 2.0
                       + 2.2 * (vnoise2(is.off + 17.3, wx * 0.05, wz * 0.05) - 0.5) * 2.0) * smooth01(s / 0.25);
            double upper = is.top * h + hills;

            double nlow = vnoise2(is.off + 29.7, wx * 0.04, wz * 0.04);
            double spk = Math.max(0.0, ridge(vnoise2(is.off + 41.3, wx * 0.075, wz * 0.075)) - 0.50) / 0.50;
            double lower = -is.depth * Math.pow(s, is.pw) * (0.75 + 0.5 * nlow) - is.depth * 0.80 * spk * Math.sqrt(s);

            dens = Math.min(upper - dy, dy - lower) / 9.0;
        }
        if (is.profile >= P_ARCH) dens = shape(is, wx, y, wz, dens, ex_, ez_, rr);
        return clamp(dens, -1.0, 1.0);
    }

    private static final class Relic {
        final int kind;
        final double x, y, z, hx, ylo, yhi;
        final double[] p;
        final Island[] bases;

        Relic(int kind, double x, double y, double z, double hx, double ylo, double yhi, double[] p, Island[] bases) {
            this.kind = kind; this.x = x; this.y = y; this.z = z;
            this.hx = hx; this.ylo = ylo; this.yhi = yhi; this.p = p; this.bases = bases;
        }

        double radius() {
            return Math.sqrt(hx * hx + Math.max(ylo * ylo, yhi * yhi));
        }
    }

    private static final Relic NO_RELIC = new Relic(-1, 0, 0, 0, 0, 0, 0, new double[0], new Island[0]);
    private static final ConcurrentHashMap<Long, Relic> RELICS = new ConcurrentHashMap<>();

    private static Relic relic(int ix, int iz) {
        return RELICS.computeIfAbsent(key(ix, iz), k -> makeRelic(seed, ix, iz));
    }

    private static Island platform(SplittableRandom rng, double x, double y, double z, double r, double top, double depth) {
        return new Island(x, y, z, r, r * (0.9 + 0.2 * rng.nextDouble()), top, depth, P_DOME, rng.nextDouble() * 1000.0, 0.0,
                0.08, rng.nextDouble() * 6.283, 0.06, rng.nextDouble() * 6.283, 0.03, rng.nextDouble() * 6.283);
    }

    private static Relic makeRelic(long sd, int ix, int iz) {
        SplittableRandom rng = new SplittableRandom(cellHash(sd, ix, iz, 4));
        if (rng.nextDouble() > RELIC_CHANCE) return NO_RELIC;
        int kind = rng.nextInt(5);
        double x0 = cellMin(ix, RELIC_CELL), z0 = cellMin(iz, RELIC_CELL);
        for (int attempt = 0; attempt < 8; attempt++) {
            double cx = x0 + 66.0 + rng.nextDouble() * (RELIC_CELL - 132.0);
            double cz = z0 + 66.0 + rng.nextDouble() * (RELIC_CELL - 132.0);
            double cy = 60.0 + rng.nextDouble() * 120.0;
            Relic r = buildRelic(kind, rng, cx, cy, cz);
            if (r.y + r.ylo < 14.0 || r.y + r.yhi > 240.0) continue;
            if (!outsideBubbles(cx, cy, cz, r.radius() * 0.85 + 8.0)) continue;
            return r;
        }
        return NO_RELIC;
    }

    private static Relic buildRelic(int kind, SplittableRandom rng, double cx, double cy, double cz) {
        switch (kind) {
            case N_PORTAL: {
                double rb = 15.0 + 6.0 * rng.nextDouble();
                double rm = 6.0;
                double psi = rng.nextDouble() * Math.PI;
                Island base = platform(rng, cx, cy - rb - rm - 3.0, cz, 17.0, 9.0, 12.0);
                return new Relic(kind, cx, cy, cz, rb + rm + 6.0, -(rb + rm + 34.0), rb + rm + 2.0,
                        new double[]{psi, rb, rm}, new Island[]{base});
            }
            case N_BRIDGE: {
                double len = 2.0 * (22.0 + 5.0 * rng.nextDouble());
                double psi = rng.nextDouble() * TAU;
                double ux = Math.cos(psi), uz = Math.sin(psi);
                double ya = cy + (rng.nextDouble() - 0.5) * 10.0, yb = cy + (rng.nextDouble() - 0.5) * 10.0;
                double ax = cx - ux * len * 0.5, az = cz - uz * len * 0.5;
                double bx = cx + ux * len * 0.5, bz = cz + uz * len * 0.5;
                double rise = 8.0 + 6.0 * rng.nextDouble();
                Island ia = platform(rng, ax, ya - 3.0, az, 15.0 + 3.0 * rng.nextDouble(), 9.0, 12.0);
                Island ib = platform(rng, bx, yb - 3.0, bz, 15.0 + 3.0 * rng.nextDouble(), 9.0, 12.0);
                return new Relic(kind, cx, cy, cz, len * 0.5 + 26.0, -40.0, rise + 22.0,
                        new double[]{ax, az, ux, uz, len, ya, yb, rise, 8.5, 6.5}, new Island[]{ia, ib});
            }
            case N_OBELISK: {
                double h = 52.0 + 14.0 * rng.nextDouble();
                double b0 = 7.0 + 1.5 * rng.nextDouble();
                double lean = 0.10 + 0.14 * rng.nextDouble();
                double phi = rng.nextDouble() * TAU;
                double axx = Math.sin(lean) * Math.cos(phi), axy = Math.cos(lean), axz = Math.sin(lean) * Math.sin(phi);
                double e1x = -Math.sin(phi), e1z = Math.cos(phi);
                double e2x = axy * e1z, e2y = axz * e1x - axx * e1z, e2z = -axy * e1x;
                int n = 5 + rng.nextInt(2);
                double[] p = new double[17 + n * 4];
                double by = cy - h * 0.5;
                p[0] = cx; p[1] = by; p[2] = cz;
                p[3] = axx; p[4] = axy; p[5] = axz;
                p[6] = e1x; p[7] = 0.0; p[8] = e1z;
                p[9] = e2x; p[10] = e2y; p[11] = e2z;
                p[12] = h; p[13] = b0; p[14] = n;
                p[15] = 0.0; p[16] = 0.0;
                double base = rng.nextDouble() * TAU;
                for (int i = 0; i < n; i++) {
                    int o = 17 + i * 4;
                    p[o] = 24.0 + 4.0 * rng.nextDouble();
                    p[o + 1] = base + i * (TAU / n) + (rng.nextDouble() - 0.5) * 0.5;
                    p[o + 2] = (rng.nextDouble() * 0.7 - 0.3) * h;
                    p[o + 3] = 5.5 + 2.0 * rng.nextDouble();
                }
                Island plat = platform(rng, cx, by - 3.0, cz, 16.0, 9.0, 12.0);
                return new Relic(kind, cx, cy, cz, 44.0, -(h * 0.5 + 34.0), h * 0.5 + 16.0, p, new Island[]{plat});
            }
            case N_HELIX: {
                double rh = 17.0 + 3.0 * rng.nextDouble();
                double turns = 2.25 + 0.5 * rng.nextDouble();
                double pitch = 24.0;
                double rise = pitch * turns;
                double dir = rng.nextBoolean() ? 1.0 : -1.0;
                double th0 = rng.nextDouble() * TAU;
                return new Relic(kind, cx, cy, cz, rh + 14.0, -(rise * 0.5 + 8.0), rise * 0.5 + 8.0,
                        new double[]{rh, turns, pitch, dir, th0, cy - rise * 0.5, 6.0, 5.0, 12.0}, new Island[0]);
            }
            default: {
                double ro = 22.0 + 4.0 * rng.nextDouble();
                double ri = ro - 8.5;
                int no = 2 + rng.nextInt(2);
                int nc = 3 + rng.nextInt(3);
                double[] p = new double[3 + no * 4 + 1 + nc * 4];
                p[0] = ro; p[1] = ri; p[2] = no;
                double az = rng.nextDouble() * TAU;
                for (int i = 0; i < no; i++) {
                    double el = i == 0 ? 0.6 + 0.6 * rng.nextDouble() : (rng.nextDouble() * 0.9 - 0.1);
                    double a = az + i * 2.4 + (rng.nextDouble() - 0.5) * 0.8;
                    int o = 3 + i * 4;
                    p[o] = Math.cos(el) * Math.cos(a);
                    p[o + 1] = Math.sin(el);
                    p[o + 2] = Math.cos(el) * Math.sin(a);
                    p[o + 3] = 0.62 + 0.14 * rng.nextDouble();
                }
                int oc = 3 + no * 4;
                p[oc] = nc;
                for (int i = 0; i < nc; i++) {
                    double a = rng.nextDouble() * TAU;
                    double d = rng.nextDouble() * 0.55 * ri;
                    int o = oc + 1 + i * 4;
                    p[o] = d * Math.cos(a);
                    p[o + 1] = d * Math.sin(a);
                    p[o + 2] = 12.0 + 8.0 * rng.nextDouble();
                    p[o + 3] = 5.0 + 1.5 * rng.nextDouble();
                }
                return new Relic(N_GEODE, cx, cy, cz, ro + 3.0, -(ro + 3.0), ro + 3.0, p, new Island[0]);
            }
        }
    }

    private static boolean nearRelic(double x, double y, double z, double margin) {
        int ix = cellIndex(x, RELIC_CELL), iz = cellIndex(z, RELIC_CELL);
        for (int a = ix - 1; a <= ix + 1; a++) {
            for (int b = iz - 1; b <= iz + 1; b++) {
                Relic r = relic(a, b);
                if (r == NO_RELIC) continue;
                if (Math.abs(x - r.x) <= r.hx + margin && Math.abs(z - r.z) <= r.hx + margin
                        && y >= r.y + r.ylo - margin && y <= r.y + r.yhi + margin) return true;
            }
        }
        return false;
    }

    private static double relicDensity(Relic r, double x, double y, double z) {
        if (Math.abs(x - r.x) > r.hx || Math.abs(z - r.z) > r.hx || y < r.y + r.ylo || y > r.y + r.yhi) return -1.0;
        double best = -1.0;
        for (Island is : r.bases) best = Math.max(best, islandDensity(is, x, y, z));
        double[] p = r.p;
        switch (r.kind) {
            case N_PORTAL: {
                double dx = x - r.x, dz = z - r.z, dy = y - r.y;
                double a = -dx * Math.sin(p[0]) + dz * Math.cos(p[0]);
                double c = dx * Math.cos(p[0]) + dz * Math.sin(p[0]);
                double d = Math.hypot(Math.hypot(a, dy) - p[1], c) - p[2];
                return Math.max(best, -d / 9.0);
            }
            case N_BRIDGE: {
                double px = x - p[0], pz = z - p[1];
                double s = px * p[2] + pz * p[3];
                double w = Math.abs(-px * p[3] + pz * p[2]);
                double t = clamp(s / p[4], 0.0, 1.0);
                double deck = p[5] + (p[6] - p[5]) * t + p[7] * 4.0 * t * (1.0 - t) + 4.0;
                double over = Math.max(-s, s - p[4]);
                double d = Math.max(Math.max(w - p[8], Math.abs(y - deck) - p[9]), over);
                best = Math.max(best, -d / 9.0);
                double mx = p[0] + p[2] * p[4] * 0.5, mz = p[1] + p[3] * p[4] * 0.5;
                double below = ((p[5] + p[6]) * 0.5 + p[7] + 4.0 - p[9]) - y;
                double rad = 8.0 * (1.0 - clamp(below, 0.0, 28.0) / 28.0);
                double dp = Math.max(Math.max(Math.hypot(x - mx, z - mz) - rad, below - 28.0), -below - 2.0);
                return Math.max(best, -dp / 9.0);
            }
            case N_OBELISK: {
                double qx = x - p[0], qy = y - p[1], qz = z - p[2];
                double hh = qx * p[3] + qy * p[4] + qz * p[5];
                double a = qx * p[6] + qy * p[7] + qz * p[8];
                double c = qx * p[9] + qy * p[10] + qz * p[11];
                double h = p[12];
                double w = p[13] * (1.0 - 0.28 * clamp(hh / h, 0.0, 1.0));
                if (hh > 0.82 * h) w *= clamp((h - hh) / (0.18 * h), 0.0, 1.0);
                double d = Math.max(Math.max(Math.abs(a) - w, Math.abs(c) - w * 0.85), Math.max(-hh, hh - h));
                best = Math.max(best, -d / 9.0);
                int n = (int) p[14];
                for (int i = 0; i < n; i++) {
                    int o = 17 + i * 4;
                    double sx = r.x + p[o] * Math.cos(p[o + 1]);
                    double sz = r.z + p[o] * Math.sin(p[o + 1]);
                    double sy = r.y + p[o + 2];
                    double ds = (Math.abs(x - sx) + Math.abs(z - sz) + 0.6 * Math.abs(y - sy) - p[o + 3]) / 1.6;
                    best = Math.max(best, -ds / 9.0);
                }
                return best;
            }
            case N_HELIX: {
                double rh = p[0], turns = p[1], pitch = p[2], dir = p[3], th0 = p[4], ys = p[5];
                double dx = x - r.x, dz = z - r.z;
                double rho = Math.hypot(dx, dz);
                double phi = dir * (Math.atan2(dz, dx) - th0);
                phi -= TAU * Math.floor(phi / TAU);
                double u = phi / TAU;
                double m0 = Math.round((y - ys) / pitch - u);
                double d = 1.0e3;
                for (double m = m0 - 1.0; m <= m0 + 1.0; m += 1.0) {
                    double uu = m + u;
                    if (uu < 0.0 || uu > turns) continue;
                    double yc = ys + pitch * uu;
                    d = Math.min(d, Math.max(Math.abs(rho - rh) - p[6], Math.abs(y - yc) - p[7]));
                }
                best = Math.max(best, -d / 9.0);
                double ang1 = th0, ang2 = th0 + dir * TAU * turns;
                double d1 = Math.max(Math.hypot(x - (r.x + rh * Math.cos(ang1)), z - (r.z + rh * Math.sin(ang1))) - p[8], Math.abs(y - ys) - p[7]);
                double d2 = Math.max(Math.hypot(x - (r.x + rh * Math.cos(ang2)), z - (r.z + rh * Math.sin(ang2))) - p[8], Math.abs(y - (ys + pitch * turns)) - p[7]);
                double orb = Math.sqrt(dx * dx + dz * dz + sq(y - (ys + pitch * turns * 0.5))) - 7.0;
                return Math.max(best, -Math.min(Math.min(d1, d2), orb) / 9.0);
            }
            default: {
                double ro = p[0], ri = p[1];
                double dx = x - r.x, dy = y - r.y, dz = z - r.z;
                double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double shell = Math.abs(len - (ro + ri) * 0.5) - (ro - ri) * 0.5;
                int no = (int) p[2];
                for (int i = 0; i < no && len > 1.0e-6; i++) {
                    int o = 3 + i * 4;
                    double cs = clamp((dx * p[o] + dy * p[o + 1] + dz * p[o + 2]) / len, -1.0, 1.0);
                    double hole = (Math.acos(cs) - p[o + 3]) * len;
                    shell = Math.max(shell, -hole);
                }
                double floorY = r.y - 0.42 * ri;
                double v = Math.min(shell, Math.max(len - (ri + 1.0), y - floorY));
                int oc = 3 + no * 4;
                int nc = (int) p[oc];
                for (int i = 0; i < nc; i++) {
                    int o = oc + 1 + i * 4;
                    double hh = y - (floorY - 2.0);
                    double rad = p[o + 3] * (1.0 - clamp(hh / p[o + 2], 0.0, 1.0));
                    double dc = Math.max(Math.max(Math.hypot(x - (r.x + p[o]), z - (r.z + p[o + 1])) - rad, -hh), hh - p[o + 2]);
                    v = Math.min(v, dc);
                }
                return -v / 9.0;
            }
        }
    }

    public static double solid(double x, double y, double z) {
        double best = -1.0;
        Group g = group(cellIndex(x, CELL), cellIndex(z, CELL));
        for (Bubble b : g.bubbles) {
            double k = b.k(x, y, z);
            if (k >= 1.0) continue;
            double wall = (WALL - k) * b.minAxis / 5.0;
            for (Island is : b.islands) {
                double v = Math.min(islandDensity(is, x, y, z), wall);
                if (v > best) best = v;
            }
        }
        Small sm = small(cellIndex(x, SMALL_CELL), cellIndex(z, SMALL_CELL));
        if (sm.enabled) {
            for (Island is : sm.islets) best = Math.max(best, islandDensity(is, x, y, z));
        }
        Relic rl = relic(cellIndex(x, RELIC_CELL), cellIndex(z, RELIC_CELL));
        if (rl != NO_RELIC) best = Math.max(best, relicDensity(rl, x, y, z));
        return clamp(best, -1.0, 1.0);
    }

    private static Bubble containing(double x, double y, double z) {
        Group g = group(cellIndex(x, CELL), cellIndex(z, CELL));
        Bubble best = null;
        double bestK = 1.0;
        for (Bubble b : g.bubbles) {
            double k = b.k(x, y, z);
            if (k < bestK) { bestK = k; best = b; }
        }
        return best;
    }

    /** Description publique d'une bulle (utilisée par les fées). */
    public static final class BubbleInfo {
        public final long id;
        public final double x, y, z, ax, ay, az;
        public final int type;

        BubbleInfo(long id, double x, double y, double z, double ax, double ay, double az, int type) {
            this.id = id; this.x = x; this.y = y; this.z = z; this.ax = ax; this.ay = ay; this.az = az; this.type = type;
        }

        /** 0 au centre, 1 à la surface de la bulle. */
        public double k(double px, double py, double pz) {
            double dx = (px - x) / ax, dy = (py - y) / ay, dz = (pz - z) / az;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    /** La bulle qui contient ce point, ou null s'il est hors de toute bulle. */
    public static BubbleInfo bubbleAt(double x, double y, double z) {
        Bubble b = containing(x, y, z);
        return b == null ? null : new BubbleInfo(b.id, b.x, b.y, b.z, b.ax, b.ay, b.az, b.type);
    }

    public static int typeAt(double x, double y, double z) {
        Bubble b = containing(x, y, z);
        return b == null ? 0 : b.type;
    }

    public static int biomeRollAt(double x, double y, double z) {
        Bubble b = containing(x, y, z);
        if (b != null) return b.roll;
        int sx = cellIndex(x, SMALL_CELL);
        int sz = cellIndex(z, SMALL_CELL);
        return smallFlavor(seed, sx, sz) + 2 * (int) (cellHash(seed, sx, sz, 5) >>> 40);
    }
    public static int flavorAt(double x, double y, double z) {
        Bubble b = containing(x, y, z);
        if (b != null) return b.flavor;
        return smallFlavor(seed, cellIndex(x, SMALL_CELL), cellIndex(z, SMALL_CELL));
    }

    public static double noise(double off, double x, double z) {
        return vnoise2(off, x, z);
    }

    public static int archAt(double x, double y, double z) {
        Bubble b = containing(x, y, z);
        return b == null ? -1 : b.arch;
    }

    public static double lakeLevel(double x, double z) {
        Group g = group(cellIndex(x, CELL), cellIndex(z, CELL));
        for (Bubble b : g.bubbles) {
            if (b.lake != null && b.lake.rr(x, z) < b.lakeR) return b.lakeY;
        }
        return Double.NaN;
    }

    public static List<double[]> fallsInChunk(int x0, int z0) {
        List<double[]> out = new ArrayList<>();
        Group g = group(cellIndex(x0 + 8, CELL), cellIndex(z0 + 8, CELL));
        for (Bubble b : g.bubbles) {
            for (double[] f : b.falls) {
                if (f[0] >= x0 && f[0] < x0 + 16 && f[1] >= z0 && f[1] < z0 + 16) out.add(f);
            }
        }
        return out;
    }

    public static int fillSpheres(double camX, double camY, double camZ, double range, float[] out, float[] axes) {
        Arrays.fill(out, 0.0f);
        Arrays.fill(axes, 0.0f);
        int max = out.length / 4;
        int x0 = cellIndex(camX - range, CELL), x1 = cellIndex(camX + range, CELL);
        int z0 = cellIndex(camZ - range, CELL), z1 = cellIndex(camZ + range, CELL);
        List<Object[]> found = new ArrayList<>();
        for (int a = x0; a <= x1; a++) {
            for (int b = z0; b <= z1; b++) {
                for (Bubble s : group(a, b).bubbles) {
                    double dx = camX - s.x, dy = camY - s.y, dz = camZ - s.z;
                    double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    double k = s.k(camX, camY, camZ);
                    double wallDist = k <= 1.0e-9 ? -s.minAxis : len * (1.0 - 1.0 / k);
                    if (wallDist > range) continue;
                    found.add(new Object[]{s, wallDist});
                }
            }
        }
        found.sort((p, q) -> Double.compare((Double) p[1], (Double) q[1]));
        int n = Math.min(max, found.size());
        for (int i = 0; i < n; i++) {
            Bubble s = (Bubble) found.get(i)[0];
            out[i * 4] = (float) s.x;
            out[i * 4 + 1] = (float) s.y;
            out[i * 4 + 2] = (float) s.z;
            out[i * 4 + 3] = (float) (s.type * s.maxAxis);
            axes[i * 4] = (float) s.ax;
            axes[i * 4 + 1] = (float) s.ay;
            axes[i * 4 + 2] = (float) s.az;
        }
        return n;
    }
}
