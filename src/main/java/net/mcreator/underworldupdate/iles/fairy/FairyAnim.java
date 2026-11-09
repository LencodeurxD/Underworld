package net.mcreator.underworldupdate.iles.fairy;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * État d'animation côté client, lissé image par image (indépendant du nombre d'images par seconde).
 * Les vitesses, humeurs et angles sont intégrés avec le temps réel écoulé : aucun saut.
 * L'anneau est un ressort suiveur : il traîne derrière la fée, rebondit quand son humeur change
 * ou quand elle prend la parole.
 * Coordonnées du modèle : x = monde x, y = -monde y (le haut est négatif), z = -monde z.
 */
public final class FairyAnim {
    private static final float SHELL_K = 11.0F;
    private static final float SHELL_MAX = 1.6F;
    private static final float W = 0.28F, Z = 0.32F;
    private static final float WS = 0.40F, ZS = 0.25F;

    private float lastAge = -1.0F;
    private int lastState = -1;
    private int lastUtter = -1;

    public float vx, vy, vz;
    public float wChat, wBond, wFlee, wChase, wRun, wRest, wPrank, wHelp, wShow;
    public float spin, t, hop, shellT, ringSpin, ringPrec;
    /** 1 quand la fée vient de parler, retombe vite. */
    public float speak;

    public float sx, sy, sz, svx, svy, svz;
    public float ss = 1.0F, svs;

    public float gaze, gazeTarget;

    private static float ease(float dt, float tau) {
        return 1.0F - (float) Math.exp(-dt / tau);
    }

    public void update(FairyEntity e, float age) {
        boolean first = lastAge < 0.0F || age < lastAge;
        float dt = first ? 0.0F : Mth.clamp(age - lastAge, 0.0F, 3.0F);
        lastAge = age;

        Vec3 m = e.getDeltaMovement();
        int st = e.getState();
        float tChat = st == FairyEntity.ST_CHAT ? 1.0F : 0.0F;
        float tBond = st == FairyEntity.ST_BONDED ? 1.0F : 0.0F;
        float tFlee = st == FairyEntity.ST_FLEE ? 1.0F : 0.0F;
        float tChase = st == FairyEntity.ST_CHASE ? 1.0F : 0.0F;
        float tRun = st == FairyEntity.ST_RUN ? 1.0F : 0.0F;
        float tRest = st == FairyEntity.ST_REST ? 1.0F : 0.0F;
        float tPrank = st == FairyEntity.ST_PRANK ? 1.0F : 0.0F;
        float tHelp = st == FairyEntity.ST_HELP ? 1.0F : 0.0F;
        float tShow = st == FairyEntity.ST_SHOW ? 1.0F : 0.0F;
        int utter = e.getUtter();

        if (first) {
            vx = (float) m.x; vy = (float) m.y; vz = (float) m.z;
            wChat = tChat; wBond = tBond; wFlee = tFlee; wChase = tChase; wRun = tRun;
            wRest = tRest; wPrank = tPrank; wHelp = tHelp; wShow = tShow;
            gaze = gazeTarget;
        } else {
            float kv = ease(dt, 4.0F);
            vx += ((float) m.x - vx) * kv;
            vy += ((float) m.y - vy) * kv;
            vz += ((float) m.z - vz) * kv;
            float kw = ease(dt, 6.0F);
            wChat += (tChat - wChat) * kw;
            wBond += (tBond - wBond) * kw;
            wChase += (tChase - wChase) * kw;
            wRun += (tRun - wRun) * kw;
            wRest += (tRest - wRest) * ease(dt, 20.0F);   // s'endort et se réveille doucement
            wPrank += (tPrank - wPrank) * kw;
            wHelp += (tHelp - wHelp) * kw;
            wShow += (tShow - wShow) * kw;
            wFlee += (tFlee - wFlee) * ease(dt, 3.0F);
            gaze += (gazeTarget - gaze) * ease(dt, 4.0F);
            speak *= (float) Math.exp(-dt / 12.0F);
        }

        // changement d'humeur ou prise de parole : coup dans le ressort de l'anneau
        if (lastState >= 0 && st != lastState) {
            svy -= 0.30F;
            svx += 0.20F * Mth.sin(t * 7.0F + st);
            svz += 0.20F * Mth.cos(t * 5.0F + st);
            svs += 0.14F;
        }
        lastState = st;
        if (lastUtter >= 0 && utter != lastUtter) {
            speak = 1.0F;
            svy -= 0.40F;
            svs += 0.20F;
        }
        lastUtter = utter;

        float tx = -vx * SHELL_K, ty = vy * SHELL_K, tz = vz * SHELL_K;
        float len = Mth.sqrt(tx * tx + ty * ty + tz * tz);
        if (len > SHELL_MAX) {
            float k = SHELL_MAX / len;
            tx *= k; ty *= k; tz *= k;
        }
        int n = Math.max(1, (int) Math.ceil(dt / 0.5F));
        float h = dt / n;
        for (int i = 0; i < n && dt > 0.0F; i++) {
            svx += (W * W * (tx - sx) - 2.0F * Z * W * svx) * h;
            svy += (W * W * (ty - sy) - 2.0F * Z * W * svy) * h;
            svz += (W * W * (tz - sz) - 2.0F * Z * W * svz) * h;
            sx += svx * h; sy += svy * h; sz += svz * h;
            svs += (WS * WS * (1.0F - ss) - 2.0F * ZS * WS * svs) * h;
            ss += svs * h;
        }

        float p = e.personality();
        float calm = 1.0F - 0.85F * wRest;
        spin += dt * calm * (0.03F + 0.05F * p + wBond * 0.08F + wFlee * 0.45F + wChase * 0.30F + wRun * 0.15F + wPrank * 0.20F + wShow * 0.35F);
        t += dt * (1.0F - 0.7F * wRest) * (0.10F + 0.06F * p + gaze * 0.05F + wRun * 0.10F);
        hop += dt * 0.28F;
        shellT += dt * 0.045F;
        ringSpin += dt * (1.0F - 0.8F * wRest) * (0.12F + 0.10F * p + wChase * 0.50F + wRun * 0.25F + wFlee * 0.40F + speak * 0.30F + gaze * 0.10F + wPrank * 0.35F + wShow * 0.45F + wHelp * 0.06F);
        ringPrec += dt * 0.03F;
        if (t > 100000.0F) { t = 0.0F; hop = 0.0F; spin = 0.0F; shellT = 0.0F; ringSpin = 0.0F; ringPrec = 0.0F; }
    }
}
