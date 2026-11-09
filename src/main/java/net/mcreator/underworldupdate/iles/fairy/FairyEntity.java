package net.mcreator.underworldupdate.iles.fairy;

import net.mcreator.underworldupdate.iles.BubbleField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Une fée : un petit cube qui vole vite dans sa bulle. Pacifique, immortelle.
 *
 * Chaque fée a un TEMPÉRAMENT stable (énergie, sociabilité, espièglerie, gentillesse, curiosité, audace, altitude
 * préférée, voix) tiré de son UUID, une ÉNERGIE qui se vide en volant et se recharge en dormant, et un REPAIRE
 * où elle va faire la sieste. Elle choisit ses BUTS par tirage pondéré selon son tempérament et le contexte :
 *  - seule : vols en S, vrilles, loopings, bourdonnement, sieste, inspection d'objets au sol ;
 *  - avec des amies : vol en formation, conversations à tour de rôle, jeu de chat, course, farandole ;
 *  - avec un joueur : curiosité, taquineries (espiègles), guide et chute rattrapée (gentilles),
 *    numéro d'acrobatie (audacieuses) ou fuite pudique (timides) quand on les regarde ;
 *  - liée à un joueur : l'accompagne sur l'épaule, explore, et peut le taquiner.
 * Elles volent à hauteur de sol (selon leur altitude préférée) au lieu de camper au ciel de la bulle.
 */
public class FairyEntity extends PathfinderMob {
    public static final int ST_WANDER = 0, ST_CHAT = 1, ST_BONDED = 2, ST_FLEE = 3, ST_CHASE = 4, ST_RUN = 5,
            ST_REST = 6, ST_PRANK = 7, ST_HELP = 8, ST_SHOW = 9;

    private static final int A_ZIP = 0, A_SPIRAL = 1, A_LOOP = 2, A_BUZZ = 3, A_CHAT = 4, A_FOLLOW = 5, A_REST = 6,
            A_INVESTIGATE = 7, A_CURIOUS = 8, A_ANNOY = 9, A_GUIDE = 10, A_ESCORT = 11, A_SHOW = 12, A_SHY = 13,
            A_RACE = 14, A_CONGA = 15;
    private static final int N_ACTS = 16;

    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);
    /** Compteur de répliques : le client le voit changer et fait « parler » la fée (bond + rebond de l'anneau). */
    private static final EntityDataAccessor<Integer> UTTER =
            SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.INT);

    private static final double K_SOFT = 0.74;
    private static final double FEAR_RADIUS = 20.0;
    /** Petits tintements très discrets quand on est tout près d'une fée (désactivable). */
    private static final boolean AMBIENT_SOUNDS = true;
    /** Les fées espiègles peuvent taquiner le joueur (se mettre devant le réticule, tourner autour de sa tête...). */
    private static final boolean PRANKS = true;

    private long homeId;
    private boolean hasHome;
    private double hx, hy, hz, hax = 1.0, hay = 1.0, haz = 1.0;
    private Vec3 anchor;
    private long groupId;
    private UUID bondedTo;
    /** Repaire où elle fait la sieste (mémorisé). */
    private Vec3 den;

    // but en cours
    private int act = A_ZIP, actTicks, actVar, pause;
    private Vec3 actTarget, actCenter = Vec3.ZERO, actDir = Vec3.ZERO;
    private double actParam;
    private UUID actPlayer;
    private int actEntity = -1;
    private double energy;
    private long prankCool, helpCool, showCool, shyCool, raceCool;

    // course
    private Vec3 raceTarget;
    private UUID raceWinner;

    // guide
    private Vec3 guideDir = Vec3.ZERO, guidePv = Vec3.ZERO;

    // jeu de chat
    private long tagUntil, tagCool, tagImmuneUntil, tagFreezeUntil;
    private UUID tagIt;
    private Vec3 tagCenter;

    // conversation
    private long lastUtterSlot = -1;
    private int utterCount;

    private int panicTicks;
    private Player scary;
    private List<FairyEntity> friends = List.of();
    private Vec3 separation = Vec3.ZERO;
    private Vec3 avoid = Vec3.ZERO;
    private Vec3 altPull = Vec3.ZERO;
    private int avoidCooldown;
    private Vec3 bondGoal;
    private Vec3 bondPv = Vec3.ZERO;
    private double orbitAng, orbitRate = 0.012;
    private int scoutTimer = 300, scoutLeft, bondPrank;
    private Vec3 scoutOffset = Vec3.ZERO;

    private double[] traits;
    private UUID traitsFor;

    // sorties de la décision de l'étape (lues par la boucle principale)
    private Vec3 oDesired = Vec3.ZERO;
    private double oCap = 0.08, oAgility = 0.14;
    private int oState = ST_WANDER;
    private boolean oContain = true;

    /** État d'animation lissé (utilisé seulement côté client par le modèle). */
    public final FairyAnim anim = new FairyAnim();

    public FairyEntity(EntityType<? extends FairyEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.energy = 0.6 + 0.4 * this.random.nextDouble();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE, ST_WANDER);
        builder.define(UTTER, 0);
    }

    // ------------------------------------------------------------------ API

    public boolean isDark() {
        return this.getType() == FairyEntities.DARK;
    }

    public int getState() {
        return this.entityData.get(STATE);
    }

    public int getUtter() {
        return this.entityData.get(UTTER);
    }

    /** Valeur stable entre 0 et 1, propre à cette fée (variations d'animation). */
    public float personality() {
        return (this.getUUID().hashCode() & 0xFFFF) / 65535.0F;
    }

    /** Deuxième valeur stable (0..1), indépendante de la première : nuance de couleur. */
    public float personality2() {
        return ((this.getUUID().hashCode() >>> 16) & 0xFFFF) / 65535.0F;
    }

    /**
     * Tempérament stable, 0..1 : 0 énergie, 1 sociabilité, 2 espièglerie, 3 gentillesse,
     * 4 curiosité, 5 audace, 6 altitude préférée, 7 voix.
     */
    private double trait(int i) {
        UUID id = this.getUUID();
        if (traits == null || !id.equals(traitsFor)) {
            traits = new double[8];
            long h = id.getMostSignificantBits() * 0x9E3779B97F4A7C15L ^ id.getLeastSignificantBits();
            for (int k = 0; k < 8; k++) {
                h ^= h >>> 30; h *= 0xBF58476D1CE4E5B9L;
                h ^= h >>> 27; h *= 0x94D049BB133111EBL;
                h ^= h >>> 31;
                traits[k] = ((h >>> 11) & 0x1FFFFFL) / (double) 0x1FFFFFL;
            }
            traitsFor = id;
        }
        return traits[i];
    }

    /**
     * Joue un son de fée (voir FairySounds). Chaque fée a sa propre voix : sa hauteur est tirée de son tempérament,
     * avec un léger hasard à chaque fois. Les fées de ténèbres ont leurs propres sons, plus graves et plus creux.
     */
    private void fx(FairySounds.Kind kind, float volume) {
        if (!AMBIENT_SOUNDS || this.isSilent()) return;
        float voice = 0.85F + 0.4F * (float) trait(7);
        this.playSound(isDark() ? kind.dark : kind.light, volume, voice + (random.nextFloat() - 0.5F) * 0.12F);
    }

    public long getHomeId() { return homeId; }

    public UUID getBondedTo() { return bondedTo; }

    public void bondTo(UUID player) {
        this.bondedTo = player;
        this.bondGoal = null;
        this.scoutLeft = 0;
        this.bondPrank = 0;
        this.scoutTimer = 400 + random.nextInt(400);
        endAct();
        fx(FairySounds.Kind.BOND, 0.7F);
    }

    public void setHome(BubbleField.BubbleInfo info, long group) {
        this.hasHome = true;
        this.homeId = info.id;
        this.hx = info.x; this.hy = info.y; this.hz = info.z;
        this.hax = info.ax; this.hay = info.ay; this.haz = info.az;
        this.groupId = group;
    }

    // ------------------------------------------------------------ immortelle

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(source, amount);
        }
        if (this.level() instanceof ServerLevel sl && source.getEntity() instanceof ServerPlayer player) {
            FairyFear.get(sl).scare(player.getUUID(), homeId, sl.getGameTime() + FairyFear.DURATION);
            if (player.getUUID().equals(bondedTo)) bondedTo = null;
            panicTicks = 80;
            scary = player;
            endAct();
            fx(FairySounds.Kind.SCARE, 0.8F);
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean fireImmune() { return true; }

    @Override
    public boolean isNoGravity() { return true; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public boolean isPushedByFluid() { return false; }

    @Override
    protected void pushEntities() {}

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }

    // ------------------------------------------------------------ mouvement

    @Override
    public void travel(Vec3 input) {
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) return;
        boolean dark = isDark();
        int st = getState();
        if (st != ST_REST && this.random.nextInt(45) == 0) {
            this.level().addParticle(dark ? ParticleTypes.WITCH : ParticleTypes.END_ROD,
                    getX() + (random.nextDouble() - 0.5) * 0.4, getY() + 0.22 + (random.nextDouble() - 0.5) * 0.4,
                    getZ() + (random.nextDouble() - 0.5) * 0.4, 0.0, dark ? 0.0 : -0.01, 0.0);
        }
        // traînée de lumière : quand elle file, et surtout quand elle accompagne un joueur qui se déplace
        double sp = this.getDeltaMovement().lengthSqr();
        boolean bonded = st == ST_BONDED;
        if ((sp > 0.04 && this.random.nextInt(5) == 0) || (bonded && sp > 0.003 && this.random.nextInt(2) == 0)) {
            this.level().addParticle(dark ? ParticleTypes.WITCH : ParticleTypes.END_ROD, getX(), getY() + 0.22, getZ(), 0.0, 0.0, 0.0);
        }
        // sieste : de temps en temps une petite bulle de rêve qui monte
        if (st == ST_REST && this.random.nextInt(60) == 0) {
            this.level().addParticle(dark ? ParticleTypes.WITCH : ParticleTypes.END_ROD, getX(), getY() + 0.4, getZ(), 0.0, 0.02, 0.0);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!(this.level() instanceof ServerLevel sl)) return;
        long now = sl.getGameTime();

        // le cube garde toujours la même orientation : l'inclinaison vient du modèle
        this.setYRot(0.0F);
        this.setXRot(0.0F);
        this.yBodyRot = 0.0F;
        this.yHeadRot = 0.0F;

        if (!hasHome && anchor == null) initFreeHome(sl);
        int phase = this.tickCount + this.getId();
        if (phase % 5 == 0) refreshScare(sl, now);
        if (phase % 20 == 0) { refreshFriends(sl); checkBond(sl); }
        if (phase % 20 == 7) refreshAltitude(sl);
        if (phase % 2 == 0) refreshSeparation(sl);

        Vec3 pos = this.position();
        Vec3 vel = this.getDeltaMovement();
        double p = personality();
        oDesired = Vec3.ZERO;
        oCap = 0.08;
        oAgility = 0.14;
        oState = ST_WANDER;
        oContain = true;

        Player bonded = bondedTo == null ? null : sl.getPlayerByUUID(bondedTo);
        if (tagUntil != 0L && now >= tagUntil) endTag();

        if (scary != null && (panicTicks > 0 || scary.distanceToSqr(this) < FEAR_RADIUS * FEAR_RADIUS)) {
            fleeStep(pos, p);
        } else if (bonded != null && bonded.isAlive()) {
            bondStep(sl, now, pos, p, bonded);
        } else if (tagUntil > now && tagStep(sl, now, pos, p)) {
            // jeu de chat en cours
        } else {
            brainStep(sl, now, pos, p);
        }
        if (oState != ST_BONDED) bondGoal = null;

        // petit balancement vertical permanent, séparation, évitement, retour à hauteur de vol et confinement dans la bulle
        Vec3 desired = oDesired.add(0.0, Math.sin((this.tickCount + this.getId() * 13) * 0.11) * 0.006, 0.0);
        desired = desired.add(separation).add(avoid);
        if (oState == ST_WANDER || oState == ST_CHAT) desired = desired.add(altPull);
        if (oContain) desired = desired.add(containment(pos));
        avoid = avoid.scale(0.93);

        vel = vel.add(desired.subtract(vel).scale(oAgility));
        double sp = vel.length();
        if (sp > oCap) vel = vel.scale(oCap / sp);

        // l'énergie se vide en volant vite (la sieste la recharge)
        if (oState != ST_REST) energy = Math.max(0.0, energy - 0.0001 - sp * 0.0006);

        // évite les blocs en se décalant doucement vers la première direction libre
        if (avoidCooldown > 0) avoidCooldown--;
        if (oState != ST_BONDED && sp > 0.012) {
            Vec3 dir = vel.scale(1.0 / sp);
            double reach = Math.max(1.2, sp * 9.0);
            if (!sl.noCollision(this, this.getBoundingBox().move(dir.scale(reach)))) {
                Vec3 up = new Vec3(0.0, 1.0, 0.0);
                Vec3 left = new Vec3(-dir.z, 0.0, dir.x);
                Vec3 right = new Vec3(dir.z, 0.0, -dir.x);
                Vec3[] tries = {up, left.add(up.scale(0.5)).normalize(), right.add(up.scale(0.5)).normalize(), left, right,
                        dir.scale(-1.0).add(up).normalize()};
                for (Vec3 c : tries) {
                    if (sl.noCollision(this, this.getBoundingBox().move(c.scale(reach)))) {
                        avoid = avoid.add(c.scale(0.03));
                        break;
                    }
                }
                vel = vel.scale(0.92);
                if (avoidCooldown <= 0) {
                    if (act == A_ZIP || act == A_SPIRAL || act == A_LOOP) endAct();
                    avoidCooldown = 30;
                }
            }
        }

        this.setDeltaMovement(vel);
        if (this.getState() != oState) this.entityData.set(STATE, oState);
        if (this.tickCount == 2) fx(FairySounds.Kind.APPEAR, 0.45F);
        if (oState == ST_REST) { if (this.random.nextInt(240) == 0) fx(FairySounds.Kind.SLEEP, 0.3F); }
        else if (this.random.nextInt(360) == 0) fx(FairySounds.Kind.TWINKLE, 0.35F);
    }

    // ------------------------------------------------------------ fuite et compagnon

    /** FUITE : s'éloigne du joueur qui a attaqué. */
    private void fleeStep(Vec3 pos, double p) {
        oState = ST_FLEE;
        Vec3 away = pos.subtract(scary.position().add(0.0, scary.getBbHeight() * 0.5, 0.0));
        double d = away.length();
        Vec3 dir = d > 1.0e-3 ? away.scale(1.0 / d) : new Vec3(random.nextDouble() - 0.5, 0.2, random.nextDouble() - 0.5).normalize();
        Vec3 side = new Vec3(-dir.z, 0.0, dir.x).scale((p - 0.5) * 0.8);
        oDesired = dir.add(side).normalize().scale(0.40);
        oCap = 0.44;
        oAgility = 0.30;
        if (panicTicks > 0) panicTicks--;
    }

    private static Vec3 rightOf(Vec3 look) {
        Vec3 f = new Vec3(look.x, 0.0, look.z);
        double l = f.length();
        if (l < 1.0e-4) return new Vec3(1.0, 0.0, 0.0);
        f = f.scale(1.0 / l);
        return new Vec3(-f.z, 0.0, f.x);
    }

    /** LIÉE : sur l'épaule du joueur, jamais devant le réticule (sauf quand elle le taquine), rattrape ses chutes. */
    private void bondStep(ServerLevel sl, long now, Vec3 pos, double p, Player bonded) {
        oState = ST_BONDED;
        oContain = false;
        Vec3 rawPv = new Vec3(bonded.getX() - bonded.xo, bonded.getY() - bonded.yo, bonded.getZ() - bonded.zo);
        bondPv = bondPv.add(rawPv.subtract(bondPv).scale(0.2));
        Vec3 look = bonded.getLookAngle();
        Vec3 right = rightOf(look);
        Vec3 fwd = new Vec3(right.z, 0.0, -right.x);
        double side = (p < 0.5 ? 1.0 : -1.0) * (0.75 + 0.2 * Math.sin(this.tickCount * 0.04 + p * 5.0));
        double back = -0.35 + 0.25 * Math.sin(this.tickCount * 0.07 + p * 3.0);
        double up = bonded.getBbHeight() + 0.2 + 0.15 * Math.sin(this.tickCount * 0.11 + p * 6.0);
        Vec3 rawGoal = bonded.position().add(right.scale(side)).add(fwd.scale(back)).add(0.0, up, 0.0);
        boolean falling = bonded.fallDistance > 3.0F && !bonded.onGround() && !bonded.isFallFlying();
        if (falling) {
            // chute : elle plonge sous le joueur, comme pour le rattraper
            rawGoal = bonded.position().add(0.0, -2.0, 0.0);
            oState = ST_HELP;
        } else if (PRANKS && bondPrank > 0) {
            // taquinerie : elle se met un instant juste devant le réticule, puis file
            bondPrank--;
            double a = this.tickCount * 0.1;
            rawGoal = bonded.getEyePosition().add(look.scale(1.9 + 0.3 * Math.sin(a))).add(right.scale(0.25 * Math.sin(a * 2.3)));
            oState = ST_PRANK;
            if (bondPrank == 0) { fx(FairySounds.Kind.GIGGLE, 0.5F); scoutTimer = 100; }
        } else if (scoutLeft > 0) {
            rawGoal = bonded.position().add(scoutOffset);
            if (--scoutLeft == 0) scoutTimer = 500 + random.nextInt(600);
        } else if (--scoutTimer <= 0 && bondPv.lengthSqr() < 0.0009) {
            if (PRANKS && trait(2) > 0.6 && now >= prankCool && random.nextInt(3) != 0) {
                bondPrank = 70;
                prankCool = now + 1800L + random.nextInt(1800);
                fx(FairySounds.Kind.GIGGLE, 0.5F);
            } else {
                startScout(sl, bonded);
            }
        }
        Vec3 rel = rawGoal.subtract(bonded.getEyePosition());
        double rl = rel.length();
        if (oState == ST_BONDED && rl > 0.3 && rl < 5.0 && rel.scale(1.0 / rl).dot(look) > 0.9) {
            rawGoal = rawGoal.add(right.scale(side * 1.2)).add(0.0, 0.3, 0.0);
        }
        bondGoal = bondGoal == null ? rawGoal : bondGoal.add(rawGoal.subtract(bondGoal).scale(oState == ST_PRANK || falling ? 0.4 : 0.25));
        Vec3 to = bondGoal.subtract(pos);
        if (to.lengthSqr() > 32.0 * 32.0) { teleportNear(sl, bonded); bondGoal = null; }
        oDesired = to.scale(0.18).add(bondPv);
        oCap = falling ? 0.8 : 0.55;
        oAgility = falling ? 0.4 : 0.20;
    }

    // ------------------------------------------------------------ cerveau : choix et exécution des buts

    private void brainStep(ServerLevel sl, long now, Vec3 pos, double p) {
        if (pause > 0) {
            pause--;
            oCap = 0.06;
            return;
        }
        if ((this.tickCount + this.getId()) % 5 == 0) reactToPlayer(sl, now, pos);
        if (actTicks <= 0) {
            decide(sl, now, pos);
            if (actTicks <= 0) return;
        }
        runAct(sl, now, pos, p);
    }

    private static final int A_TAG = 16;

    private int pick(double[] w) {
        double sum = 0.0;
        for (double x : w) sum += Math.max(0.0, x);
        if (sum <= 0.0) return A_ZIP;
        double r = random.nextDouble() * sum;
        for (int i = 0; i < w.length; i++) {
            r -= Math.max(0.0, w[i]);
            if (r <= 0.0) return i;
        }
        return A_ZIP;
    }

    /** Choisit un nouveau but par tirage pondéré : le tempérament et le contexte changent les poids. */
    private void decide(ServerLevel sl, long now, Vec3 pos) {
        double e = trait(0), s = trait(1), m = trait(2), k = trait(3), c = trait(4), b = trait(5);
        FairyEntity lead = leader();
        boolean follower = lead != this;
        boolean group = !friends.isEmpty();
        if (follower && random.nextDouble() < 0.45 + 0.45 * s) {
            startAct(sl, now, pos, A_FOLLOW, null);
            return;
        }
        Player pl = nearestFriendlyPlayer(sl, 40.0);
        ItemEntity item = nearestItem(sl, 20.0);
        double[] w = new double[A_TAG + 1];
        w[A_ZIP] = 30.0 * (0.6 + 0.8 * e);
        w[A_SPIRAL] = 8.0 * (0.5 + e);
        w[A_LOOP] = 8.0 * (0.5 + e);
        w[A_BUZZ] = 10.0 * (1.5 - e);
        w[A_REST] = energy < 0.3 ? 80.0 : (energy < 0.55 ? 10.0 : 1.0) * (1.8 - e);
        w[A_INVESTIGATE] = 8.0 * (0.3 + c) * (item != null ? 3.0 : 1.0);
        if (group && !follower) {
            w[A_CHAT] = 14.0 * s;
            if (now >= tagCool) w[A_TAG] = 12.0 * (0.5 + e);
            if (now >= raceCool) w[A_RACE] = 8.0 * (0.5 + e);
            if (groupMembers().size() >= 3) w[A_CONGA] = 6.0 * s;
        }
        if (pl != null) {
            boolean moving = Math.abs(pl.getX() - pl.xo) + Math.abs(pl.getZ() - pl.zo) > 0.05;
            w[A_CURIOUS] = 10.0 * c * (0.5 + b);
            if (PRANKS && now >= prankCool) w[A_ANNOY] = 18.0 * m * (0.3 + b);
            if (now >= helpCool) w[A_GUIDE] = 14.0 * k * (moving ? 1.0 : 0.2);
        }
        startAct(sl, now, pos, pick(w), pl);
    }

    /** Réactions immédiates : fuite pudique ou numéro d'acrobatie quand on la regarde, rattrape une chute. */
    private void reactToPlayer(ServerLevel sl, long now, Vec3 pos) {
        if (act == A_CHAT || act == A_RACE || act == A_CONGA || act == A_SHOW || act == A_SHY || act == A_ESCORT || act == A_ANNOY) return;
        Player pl = nearestFriendlyPlayer(sl, 14.0);
        if (pl == null) return;
        double k = trait(3), b = trait(5), e = trait(0);
        if (k > 0.45 && now >= helpCool && pl.fallDistance > 4.0F && !pl.onGround() && !pl.isFallFlying()) {
            startAct(sl, now, pos, A_ESCORT, pl);
            return;
        }
        Vec3 to = pos.add(0.0, 0.2, 0.0).subtract(pl.getEyePosition());
        double d = to.length();
        if (d > 0.6 && d < 14.0 && to.scale(1.0 / d).dot(pl.getLookAngle()) > 0.985) {
            if (b < 0.3 && now >= shyCool) startAct(sl, now, pos, A_SHY, pl);
            else if (b > 0.6 && e > 0.4 && now >= showCool) startAct(sl, now, pos, A_SHOW, pl);
        }
    }

    private void endAct() {
        act = A_ZIP;
        actTicks = 0;
        actTarget = null;
        actParam = 0.0;
        actVar = 0;
        actEntity = -1;
        actPlayer = null;
    }

    private void startAct(ServerLevel sl, long now, Vec3 pos, int a, Player pl) {
        if (a == A_TAG) { startTag(sl, now, pos); return; }
        if (a == A_CHAT || a == A_RACE || a == A_CONGA) { startGroupAct(sl, now, pos, a); return; }
        act = a;
        actTarget = null;
        actParam = 0.0;
        actVar = 0;
        actEntity = -1;
        actPlayer = pl != null ? pl.getUUID() : null;
        switch (a) {
            case A_SPIRAL:
                actCenter = pickWanderTarget(sl, pos, 4.0, 14.0);
                actTicks = 120;
                break;
            case A_LOOP: {
                fx(FairySounds.Kind.FLUTTER, 0.3F);
                actCenter = pos;
                double an = random.nextDouble() * Math.PI * 2.0;
                actDir = new Vec3(Math.cos(an), 0.0, Math.sin(an));
                actTicks = 80;
                break;
            }
            case A_BUZZ:
                actCenter = pos;
                actTicks = 50 + random.nextInt(60);
                break;
            case A_FOLLOW:
                actTicks = 120 + random.nextInt(140);
                break;
            case A_REST: {
                if (den == null || random.nextInt(8) == 0) {
                    Vec3 d = pickDen(sl, pos);
                    if (d != null) den = d;
                }
                if (den == null) { act = A_BUZZ; actCenter = pos; actTicks = 60; break; }
                actTarget = den;
                actTicks = 300 + random.nextInt(400);
                break;
            }
            case A_INVESTIGATE: {
                ItemEntity it = nearestItem(sl, 20.0);
                if (it != null) {
                    actEntity = it.getId();
                } else {
                    Vec3 t = pickWanderTarget(sl, pos, 4.0, 16.0);
                    double gy = groundY(sl, t.x, t.y + 3.0, t.z, 30);
                    actTarget = Double.isNaN(gy) ? t : new Vec3(t.x, gy + 0.4, t.z);
                }
                actTicks = 150 + random.nextInt(150);
                break;
            }
            case A_CURIOUS:
                fx(FairySounds.Kind.VOICE, 0.35F);
                actTicks = 200 + random.nextInt(200);
                break;
            case A_ANNOY:
                actVar = random.nextInt(3);
                actTicks = actVar == 2 ? 100 : 90 + random.nextInt(70);
                prankCool = now + 1500L + random.nextInt(1800);
                fx(FairySounds.Kind.GIGGLE, 0.55F);
                break;
            case A_GUIDE:
                fx(FairySounds.Kind.CALL, 0.6F);
                guideDir = Vec3.ZERO;
                guidePv = Vec3.ZERO;
                actTicks = 240 + random.nextInt(240);
                helpCool = now + 1500L + random.nextInt(1500);
                break;
            case A_ESCORT:
                fx(FairySounds.Kind.CALL, 0.7F);
                actTicks = 140;
                helpCool = now + 600L;
                break;
            case A_SHOW:
                actTicks = 220;
                showCool = now + 1200L + random.nextInt(1200);
                break;
            case A_SHY: {
                shyCool = now + 900L;
                Vec3 away = pl == null ? new Vec3(random.nextDouble() - 0.5, 0.0, random.nextDouble() - 0.5) : pos.subtract(pl.position());
                away = new Vec3(away.x, 0.0, away.z);
                double l = away.length();
                away = l > 1.0e-3 ? away.scale(1.0 / l) : new Vec3(1.0, 0.0, 0.0);
                Vec3 t = pos.add(away.scale(9.0 + random.nextDouble() * 5.0));
                double gy = groundY(sl, t.x, pos.y + 4.0, t.z, 30);
                t = new Vec3(t.x, Double.isNaN(gy) ? pos.y : gy + 2.0, t.z);
                if (kAt(t) > 0.85 || !sl.noCollision(AABB.ofSize(t, 1.0, 1.0, 1.0))) t = pickWanderTarget(sl, pos, 6.0, 14.0);
                actTarget = t;
                actTicks = 100;
                break;
            }
            default:
                act = A_ZIP;
                actTicks = 160;
                break;
        }
    }

    /** But de groupe lancé par le chef : tous les membres chargés le rejoignent. */
    private void startGroupAct(ServerLevel sl, long now, Vec3 pos, int a) {
        List<FairyEntity> members = groupMembers();
        if (members.size() < 2) {
            startAct(sl, now, pos, A_ZIP, null);
            return;
        }
        Vec3 target = a == A_RACE ? pickWanderTarget(sl, pos, 22.0, 30.0) : null;
        int dur = a == A_CHAT ? 240 + random.nextInt(160) : a == A_RACE ? 400 : 300;
        long cool = now + 2400L + random.nextInt(2400);
        for (FairyEntity f : members) {
            f.act = a;
            f.actTicks = dur;
            f.actTarget = null;
            f.actParam = 0.0;
            f.actEntity = -1;
            f.actPlayer = null;
            f.pause = 0;
            f.actCenter = pos;
            f.actVar = (a == A_CONGA && f == members.get(0)) ? 1 : 0;
            if (a == A_RACE) { f.raceTarget = target; f.raceWinner = null; f.raceCool = cool; }
        }
    }

    private void runAct(ServerLevel sl, long now, Vec3 pos, double p) {
        switch (act) {
            case A_SPIRAL: {
                // vrille : tourne vite autour d'un point en montant
                actParam += 0.14;
                double r = 2.5 + 1.5 * p;
                Vec3 goal = actCenter.add(Math.cos(actParam) * r, 0.5 * Math.sin(actParam * 0.5) + actParam * 0.15, Math.sin(actParam) * r);
                oDesired = goal.subtract(pos).scale(0.4);
                oCap = 0.42;
                oAgility = 0.30;
                if (actParam > 4.0 * Math.PI) actTicks = 0; else actTicks--;
                break;
            }
            case A_LOOP: {
                // looping : un tour complet dans un plan vertical en avançant
                double r = 2.2 + p;
                actParam += 0.13;
                Vec3 goal = actCenter.add(actDir.scale(r * Math.sin(actParam) + actParam * r * 0.6)).add(0.0, r * (1.0 - Math.cos(actParam)), 0.0);
                oDesired = goal.subtract(pos).scale(0.5);
                oCap = 0.50;
                oAgility = 0.32;
                if (actParam > 2.0 * Math.PI) actTicks = 0; else actTicks--;
                break;
            }
            case A_BUZZ: {
                // bourdonnement : plein de petits bonds nerveux sur place
                if (actTicks % 9 == 0 || actTarget == null) {
                    actTarget = actCenter.add((random.nextDouble() - 0.5) * 2.4, (random.nextDouble() - 0.5) * 1.6, (random.nextDouble() - 0.5) * 2.4);
                }
                oDesired = actTarget.subtract(pos).scale(0.25);
                oCap = 0.26;
                oAgility = 0.30;
                actTicks--;
                break;
            }
            case A_FOLLOW: {
                // vol en formation autour du chef
                FairyEntity lead = leader();
                if (lead == this) { endAct(); break; }
                orbitRate += (0.03 - orbitRate) * 0.05;
                orbitAng += orbitRate;
                double ang = p * 6.283 + orbitAng + this.getId();
                double r = 1.6 + 1.4 * p;
                Vec3 goal = lead.position().add(Math.cos(ang) * r, 0.8 * Math.sin(this.tickCount * 0.08 + p * 9.0), Math.sin(ang) * r);
                Vec3 to = goal.subtract(pos);
                oDesired = to.scale(0.28);
                oCap = Math.min(0.55, 0.14 + to.length() * 0.07);
                oAgility = 0.22;
                actTicks--;
                break;
            }
            case A_CHAT: {
                if (friends.isEmpty()) { endAct(); break; }
                chatStep(sl, now, pos, p, actCenter);
                actTicks--;
                break;
            }
            case A_REST: {
                // sieste dans son repaire : elle se pose près du sol, s'éteint doucement, se réveille si on s'approche
                if (actTarget == null) { endAct(); break; }
                Vec3 to = actTarget.subtract(pos);
                double dist = to.length();
                Player near = nearestFriendlyPlayer(sl, 3.0);
                if (near != null && dist < 2.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.18, 0.0));
                    fx(FairySounds.Kind.SCARE, 0.4F);
                    energy = Math.max(energy, 0.5);
                    endAct();
                    pause = 10;
                    break;
                }
                if (dist > 1.2) {
                    oDesired = to.scale(1.0 / dist).scale(Math.min(0.22, dist * 0.08 + 0.02));
                    oCap = 0.25;
                    oAgility = 0.18;
                } else {
                    oState = ST_REST;
                    oDesired = to.scale(0.05);
                    oCap = 0.02;
                    oAgility = 0.10;
                    energy = Math.min(1.0, energy + 0.0015);
                    if (energy >= 0.95) actTicks = 0;
                }
                actTicks--;
                break;
            }
            case A_INVESTIGATE: {
                // inspecte un objet au sol (ou un coin du terrain) en tournant autour
                Vec3 c;
                if (actEntity >= 0) {
                    Entity en = sl.getEntity(actEntity);
                    if (en == null || !en.isAlive()) { endAct(); break; }
                    c = en.position().add(0.0, 0.5, 0.0);
                } else {
                    if (actTarget == null) { endAct(); break; }
                    c = actTarget;
                }
                actParam += 0.11;
                double r = 0.9 + 0.2 * Math.sin(actParam * 0.5);
                Vec3 goal = c.add(Math.cos(actParam) * r, 0.5 + 0.25 * Math.sin(actParam * 1.3), Math.sin(actParam) * r);
                Vec3 to = goal.subtract(pos);
                oDesired = to.scale(0.25);
                oCap = Math.min(0.40, 0.08 + to.length() * 0.06);
                oAgility = 0.22;
                actTicks--;
                break;
            }
            case A_CURIOUS: {
                // s'approche du joueur et tourne autour à distance
                Player pl = actPlayerEntity(sl);
                if (pl == null) { endAct(); break; }
                double b = trait(5);
                actParam += 0.04 + 0.02 * b;
                double r = 4.5 + 3.0 * (1.0 - b);
                Vec3 goal = pl.position().add(Math.cos(actParam) * r, 1.0 + 0.5 * Math.sin(actParam * 2.0), Math.sin(actParam) * r);
                Vec3 to = goal.subtract(pos);
                oDesired = to.scale(0.18);
                oCap = Math.min(0.35, 0.06 + to.length() * 0.05);
                oAgility = 0.18;
                actTicks--;
                break;
            }
            case A_ANNOY: {
                // taquinerie (espiègle) : tourne autour de la tête, se met devant le réticule, ou fait coucou par surprise
                Player pl = actPlayerEntity(sl);
                if (pl == null) { endAct(); break; }
                oState = ST_PRANK;
                Vec3 eye = pl.getEyePosition();
                Vec3 look = pl.getLookAngle();
                Vec3 right = rightOf(look);
                Vec3 goal;
                if (actVar == 0) {
                    actParam += 0.35;
                    goal = eye.add(Math.cos(actParam) * 1.2, Math.sin(actParam * 1.7) * 0.3, Math.sin(actParam) * 1.2);
                    oCap = 0.55;
                } else if (actVar == 1) {
                    actParam += 0.1;
                    goal = eye.add(look.scale(2.0 + 0.4 * Math.sin(actParam))).add(right.scale(0.25 * Math.sin(actParam * 2.3)))
                            .add(0.0, 0.1 * Math.sin(actParam * 1.7), 0.0);
                    oCap = 0.50;
                } else {
                    actParam += 1.0;
                    if (actParam < 40.0) goal = eye.subtract(look.scale(2.2)).add(0.0, -0.3, 0.0);          // se cache derrière lui
                    else if (actParam < 62.0) goal = eye.add(look.scale(1.6)).add(right.scale(0.4));       // surgit devant !
                    else { actTicks = 0; break; }
                    oCap = 0.60;
                }
                oDesired = goal.subtract(pos).scale(0.4);
                oAgility = 0.35;
                if (--actTicks <= 0) fx(FairySounds.Kind.GIGGLE, 0.5F);
                break;
            }
            case A_GUIDE: {
                // éclaireuse : vole devant le joueur, au prochain endroit où se poser, et l'attend en tournant s'il s'arrête
                Player pl = actPlayerEntity(sl);
                if (pl == null) { endAct(); break; }
                oState = ST_HELP;
                Vec3 rawPv = new Vec3(pl.getX() - pl.xo, 0.0, pl.getZ() - pl.zo);
                guidePv = guidePv.add(rawPv.subtract(guidePv).scale(0.1));
                double spd = guidePv.length();
                if (spd > 0.04) guideDir = guidePv.scale(1.0 / spd);
                if (actTarget == null || this.tickCount % 5 == 0) {
                    if (guideDir.lengthSqr() > 0.01 && spd > 0.02) {
                        Vec3 gx = pl.position().add(guideDir.scale(9.0));
                        double gy = groundY(sl, gx.x, pl.getY() + 6.0, gx.z, 30);
                        actTarget = new Vec3(gx.x, Double.isNaN(gy) ? pl.getY() + 1.8 : gy + 2.2, gx.z);
                    } else {
                        actParam += 0.25;
                        actTarget = pl.position().add(Math.cos(actParam) * 2.5, 1.6 + 0.3 * Math.sin(actParam * 2.0), Math.sin(actParam) * 2.5);
                    }
                }
                oDesired = actTarget.subtract(pos).scale(0.15);
                oCap = 0.40;
                oAgility = 0.20;
                actTicks--;
                break;
            }
            case A_ESCORT: {
                // le joueur tombe : elle plonge sous lui, comme pour le rattraper
                Player pl = actPlayerEntity(sl);
                if (pl == null || pl.onGround() || actTicks <= 0) { endAct(); break; }
                oState = ST_HELP;
                oDesired = pl.position().add(0.0, -2.0, 0.0).subtract(pos).scale(0.5);
                oCap = 0.70;
                oAgility = 0.40;
                actTicks--;
                break;
            }
            case A_SHOW: {
                // numéro d'acrobatie devant le joueur qui la regarde : se place, fait un looping, salue
                Player pl = actPlayerEntity(sl);
                if (pl == null) { endAct(); break; }
                oState = ST_SHOW;
                if (actVar == 0) {
                    Vec3 look = pl.getLookAngle();
                    Vec3 right = rightOf(look);
                    actCenter = pl.getEyePosition().add(look.scale(4.5)).subtract(right.scale(2.5));
                    actDir = right;
                    Vec3 to = actCenter.subtract(pos);
                    oDesired = to.scale(0.3);
                    oCap = 0.50;
                    oAgility = 0.30;
                    if (to.lengthSqr() < 2.25) { actVar = 1; actParam = 0.0; }
                } else if (actVar == 1) {
                    double r = 2.4;
                    actParam += 0.13;
                    Vec3 goal = actCenter.add(actDir.scale(r * Math.sin(actParam) + actParam * r * 0.9)).add(0.0, r * (1.0 - Math.cos(actParam)), 0.0);
                    oDesired = goal.subtract(pos).scale(0.5);
                    oCap = 0.50;
                    oAgility = 0.32;
                    if (actParam > 2.0 * Math.PI) { actVar = 2; actTicks = Math.min(actTicks, 40); fx(FairySounds.Kind.WIN, 0.6F); }
                } else {
                    oCap = 0.05;
                }
                actTicks--;
                break;
            }
            case A_SHY: {
                // timide : détale hors du regard du joueur
                if (actTarget == null) { endAct(); break; }
                Vec3 to = actTarget.subtract(pos);
                double dist = to.length();
                if (dist > 1.0e-3) oDesired = to.scale(1.0 / dist).scale(0.45);
                oCap = 0.48;
                oAgility = 0.30;
                if (dist < 1.5 || --actTicks <= 0) { endAct(); pause = 20 + random.nextInt(30); }
                break;
            }
            case A_RACE: {
                // course vers un point : la première arrivée fait un looping de victoire, les autres boudent
                if (raceTarget == null) { endAct(); break; }
                UUID me = this.getUUID();
                if (raceWinner != null && !raceWinner.equals(me)) {
                    raceTarget = null;
                    startAct(sl, now, pos, A_BUZZ, null);
                    fx(FairySounds.Kind.SULK, 0.5F);
                    break;
                }
                Vec3 to = raceTarget.subtract(pos);
                double dist = to.length();
                double spd = 0.36 + 0.22 * trait(0);
                if (dist > 1.0e-3) oDesired = to.scale(1.0 / dist).scale(Math.min(spd, dist * 0.14 + 0.03));
                oCap = spd * 1.1;
                oAgility = 0.26;
                if (dist < 2.0 && raceWinner == null) {
                    raceWinner = me;
                    for (FairyEntity f : friends) f.raceWinner = me;
                    raceTarget = null;
                    fx(FairySounds.Kind.WIN, 0.7F);
                    sl.sendParticles(isDark() ? ParticleTypes.WITCH : ParticleTypes.END_ROD, getX(), getY() + 0.3, getZ(), 12, 0.2, 0.2, 0.2, 0.05);
                    startAct(sl, now, pos, A_LOOP, null);
                    break;
                }
                if (--actTicks <= 0) { raceTarget = null; endAct(); }
                break;
            }
            case A_CONGA: {
                // farandole : la tête serpente, chacune suit celle qui la précède
                List<FairyEntity> m = groupMembers();
                int idx = m.indexOf(this);
                if (m.size() < 3 || actTicks <= 0) { endAct(); break; }
                if (idx <= 0) {
                    if (actTarget == null || pos.distanceToSqr(actTarget) < 4.0) actTarget = pickWanderTarget(sl, pos, 6.0, 14.0);
                    Vec3 to = actTarget.subtract(pos);
                    double dist = to.length();
                    if (dist > 1.0e-3) {
                        Vec3 dir = to.scale(1.0 / dist);
                        Vec3 side = new Vec3(-dir.z, 0.0, dir.x).scale(Math.sin(this.tickCount * 0.12) * 0.12);
                        oDesired = dir.scale(0.30).add(side);
                    }
                    oCap = 0.34;
                    oAgility = 0.22;
                } else {
                    FairyEntity pred = m.get(idx - 1);
                    Vec3 goal = pred.position();
                    Vec3 pv = pred.getDeltaMovement();
                    if (pv.lengthSqr() > 1.0e-4) goal = goal.subtract(pv.normalize().scale(1.4));
                    Vec3 to = goal.subtract(pos);
                    oDesired = to.scale(0.3);
                    oCap = Math.min(0.5, 0.1 + to.length() * 0.1);
                    oAgility = 0.25;
                }
                actTicks--;
                break;
            }
            default: {
                // trait de vitesse en S vers un point (à hauteur de vol), puis éventuellement un petit temps d'arrêt
                if (actTarget == null) {
                    actTarget = pickWanderTarget(sl, pos);
                    if (random.nextInt(5) == 0) fx(FairySounds.Kind.FLUTTER, 0.3F);
                }
                Vec3 to = actTarget.subtract(pos);
                double dist = to.length();
                double spd = 0.34 + 0.16 * p;
                if (dist > 1.0e-3) {
                    Vec3 dir = to.scale(1.0 / dist);
                    Vec3 side = new Vec3(-dir.z, 0.0, dir.x).scale(Math.sin(this.tickCount * 0.15 + p * 6.0) * 0.10 * Math.min(1.0, dist / 6.0));
                    oDesired = dir.scale(Math.min(spd, dist * 0.14 + 0.03)).add(side);
                }
                oCap = spd * 1.15;
                oAgility = 0.22;
                if (dist < 1.5 || --actTicks <= 0) {
                    endAct();
                    pause = random.nextInt(3) == 0 ? 6 + random.nextInt(18) : 0;
                }
                break;
            }
        }
    }

    private List<FairyEntity> groupMembers() {
        List<FairyEntity> m = new ArrayList<>(friends.size() + 1);
        m.add(this);
        for (FairyEntity f : friends) {
            if (f.isAlive() && f.bondedTo == null) m.add(f);
        }
        m.sort(Comparator.comparing(FairyEntity::getUUID));
        return m;
    }

    /** Conversation : le groupe fait cercle, chacune parle à son tour (bond, tintement, étincelles). */
    private void chatStep(ServerLevel sl, long now, Vec3 pos, double p, Vec3 center) {
        List<FairyEntity> m = groupMembers();
        int n = Math.max(1, m.size());
        int idx = Math.max(0, m.indexOf(this));
        orbitRate += (0.035 - orbitRate) * 0.05;
        orbitAng += orbitRate;
        double ang = orbitAng + Math.PI * 2.0 * idx / n;
        double r = 1.5 + 0.3 * Math.sin(this.tickCount * 0.05 + p * 6.0);
        Vec3 goal = center.add(Math.cos(ang) * r, 0.25 * Math.sin(this.tickCount * 0.07 + idx), Math.sin(ang) * r);
        oDesired = goal.subtract(pos).scale(0.2);
        oCap = 0.2;
        oAgility = 0.2;
        oState = ST_CHAT;
        long slot = now / 50L;
        if (m.get((int) (slot % n)) == this && lastUtterSlot != slot) {
            lastUtterSlot = slot;
            if (random.nextInt(5) != 0) utter(sl);
        }
    }

    private void utter(ServerLevel sl) {
        this.entityData.set(UTTER, ++utterCount);
        fx(FairySounds.Kind.VOICE, 0.6F);
        sl.sendParticles(isDark() ? ParticleTypes.WITCH : ParticleTypes.END_ROD, getX(), getY() + 0.4, getZ(), 4, 0.1, 0.1, 0.1, 0.02);
    }

    // ------------------------------------------------------------ jeu de chat

    private void startTag(ServerLevel sl, long now, Vec3 pos) {
        List<FairyEntity> members = groupMembers();
        if (members.size() < 2) return;
        UUID it = members.get(random.nextInt(members.size())).getUUID();
        long until = now + 500L + random.nextInt(500);
        long cool = now + 1800L + random.nextInt(1800);
        for (FairyEntity f : members) {
            f.tagUntil = until;
            f.tagIt = it;
            f.tagCenter = pos;
            f.tagImmuneUntil = 0L;
            f.tagFreezeUntil = now + 30L;
            f.tagCool = cool;
            f.endAct();
        }
    }

    private void endTag() {
        tagUntil = 0L;
        tagIt = null;
        tagCenter = null;
        endAct();
    }

    /** Un tour du jeu de chat. Renvoie false si la partie est invalide (alors elle s'arrête pour cette fée). */
    private boolean tagStep(ServerLevel sl, long now, Vec3 pos, double p) {
        boolean chaser = this.getUUID().equals(tagIt);
        FairyEntity chaserEntity = null;
        if (!chaser) {
            for (FairyEntity f : friends) {
                if (f.getUUID().equals(tagIt) && f.isAlive() && f.bondedTo == null && f.tagUntil > now) { chaserEntity = f; break; }
            }
            if (chaserEntity == null) { endTag(); return false; }
        }
        Vec3 center = tagCenter != null ? tagCenter : pos;
        if (chaser) {
            oState = ST_CHASE;
            if (now < tagFreezeUntil) {            // il compte jusqu'à trois en vibrant sur place
                oCap = 0.06;
                return true;
            }
            FairyEntity prey = null;
            double bd = 1.0e18;
            for (FairyEntity f : friends) {
                if (!f.isAlive() || f.bondedTo != null || f.tagUntil <= now || f.tagImmuneUntil > now) continue;
                double d = f.distanceToSqr(this);
                if (d < bd) { bd = d; prey = f; }
            }
            if (prey == null) { oCap = 0.06; return true; }
            Vec3 lead = prey.position().add(prey.getDeltaMovement().scale(6.0));
            Vec3 to = lead.subtract(pos);
            double dist = to.length();
            if (dist > 1.0e-3) oDesired = to.scale(1.0 / dist).scale(0.50);
            oCap = 0.52;
            oAgility = 0.30;
            if (bd < 0.9 * 0.9) tagTouch(sl, now, prey);
            return true;
        }
        oState = ST_RUN;
        Vec3 away = pos.subtract(chaserEntity.position());
        double d = away.length();
        Vec3 dir = d > 1.0e-3 ? away.scale(1.0 / d) : new Vec3(random.nextDouble() - 0.5, 0.0, random.nextDouble() - 0.5).normalize();
        if (d < 14.0) {
            // fuite en zigzag, avec des feintes en hauteur, sans trop s'éloigner du terrain de jeu
            Vec3 side = new Vec3(-dir.z, 0.0, dir.x).scale(Math.sin(this.tickCount * 0.33 + p * 6.0) * 0.6);
            Vec3 juke = new Vec3(0.0, Math.sin(this.tickCount * 0.21 + p * 4.0) * 0.35, 0.0);
            Vec3 toCenter = center.subtract(pos);
            double cd = toCenter.length();
            Vec3 back = cd > 18.0 ? toCenter.scale(1.0 / cd).scale(0.8) : Vec3.ZERO;
            oDesired = dir.add(side).add(juke).add(back).normalize().scale(0.44);
            oCap = 0.48;
            oAgility = 0.28;
        } else {
            // elle nargue le chasseur : petit cercle rapide autour du centre
            double a = this.tickCount * 0.09 + p * 6.0;
            Vec3 goal = center.add(Math.cos(a) * 6.0, Math.sin(a * 0.7) * 2.0, Math.sin(a) * 6.0);
            oDesired = goal.subtract(pos).scale(0.12);
            oCap = 0.25;
            oAgility = 0.20;
        }
        return true;
    }

    /** Touché ! la proie devient chasseur, l'ancien chasseur est tranquille un moment. */
    private void tagTouch(ServerLevel sl, long now, FairyEntity prey) {
        UUID newIt = prey.getUUID();
        this.tagImmuneUntil = now + 60L;
        prey.tagFreezeUntil = now + 30L;
        this.tagIt = newIt;
        for (FairyEntity f : friends) f.tagIt = newIt;
        sl.sendParticles(isDark() ? ParticleTypes.WITCH : ParticleTypes.END_ROD, prey.getX(), prey.getY() + 0.22, prey.getZ(), 12, 0.2, 0.2, 0.2, 0.04);
        fx(FairySounds.Kind.GIGGLE, 0.6F);
        Vec3 kick = prey.position().subtract(this.position());
        if (kick.lengthSqr() > 1.0e-6) prey.setDeltaMovement(prey.getDeltaMovement().add(kick.normalize().scale(0.3)));
    }

    // ------------------------------------------------------------ altitude, repaire, cibles

    private double hoverPref() {
        double t = trait(6);
        return 1.3 + 5.5 * t * t;
    }

    /** Hauteur du premier bloc solide sous (x, y, z), ou NaN (rien sous la fée, ou zone non chargée). */
    private double groundY(ServerLevel sl, double x, double y, double z, int range) {
        int bx = Mth.floor(x), bz = Mth.floor(z), top = Mth.floor(y);
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        for (int i = 0; i <= range; i++) {
            int by = top - i;
            if (by < sl.getMinBuildHeight()) break;
            mp.set(bx, by, bz);
            if (!sl.hasChunkAt(mp)) return Double.NaN;
            BlockState st = sl.getBlockState(mp);
            if (!st.getCollisionShape(sl, mp).isEmpty()) return by + 1.0;
        }
        return Double.NaN;
    }

    /** Les fées ne campent pas au ciel de la bulle : une douce force les ramène vers leur hauteur de vol préférée. */
    private void refreshAltitude(ServerLevel sl) {
        Vec3 pos = this.position();
        double g = groundY(sl, pos.x, pos.y, pos.z, 60);
        if (Double.isNaN(g)) {
            altPull = (hasHome && pos.y > hy) ? new Vec3(0.0, -0.012, 0.0) : Vec3.ZERO;
            return;
        }
        double h = pos.y - g;
        double hover = hoverPref();
        if (h > hover + 14.0) altPull = new Vec3(0.0, -0.05, 0.0);
        else if (h > hover + 8.0) altPull = new Vec3(0.0, -0.02, 0.0);
        else if (h < 0.8) altPull = new Vec3(0.0, 0.03, 0.0);
        else altPull = Vec3.ZERO;
    }

    private Vec3 pickDen(ServerLevel sl, Vec3 pos) {
        for (int i = 0; i < 10; i++) {
            Vec3 t = pickWanderTarget(sl, pos, 6.0, 30.0);
            double gy = groundY(sl, t.x, t.y + 3.0, t.z, 30);
            if (Double.isNaN(gy)) continue;
            Vec3 d = new Vec3(t.x, gy + 0.45, t.z);
            if (kAt(d) < 0.8 && sl.noCollision(AABB.ofSize(d, 0.6, 0.6, 0.6))) return d;
        }
        return null;
    }

    private ItemEntity nearestItem(ServerLevel sl, double range) {
        ItemEntity best = null;
        double bd = range * range;
        for (ItemEntity it : sl.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(range), i -> i.isAlive())) {
            double d = it.distanceToSqr(this);
            if (d < bd && kAt(it.position()) < 0.95) { bd = d; best = it; }
        }
        return best;
    }

    private Player actPlayerEntity(ServerLevel sl) {
        if (actPlayer == null) return null;
        Player pl = sl.getPlayerByUUID(actPlayer);
        if (pl == null || !pl.isAlive() || pl.isSpectator()) return null;
        if (FairyFear.get(sl).isScared(pl.getUUID(), homeId, sl.getGameTime())) return null;
        if (pl.distanceToSqr(this) > 60.0 * 60.0) return null;
        return pl;
    }

    private Vec3 pickWanderTarget(ServerLevel sl, Vec3 pos) {
        return pickWanderTarget(sl, pos, 4.0, 26.0);
    }

    /** Un point libre à hauteur de vol au-dessus du sol (altitude propre à chaque fée, parfois très haut). */
    private Vec3 pickWanderTarget(ServerLevel sl, Vec3 pos, double rMin, double rMax) {
        double hover = hoverPref();
        for (int i = 0; i < 8; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = rMin + random.nextDouble() * (rMax - rMin);
            double tx = pos.x + Math.cos(a) * r, tz = pos.z + Math.sin(a) * r;
            double gy = groundY(sl, tx, pos.y + 4.0, tz, 40);
            double ty;
            if (!Double.isNaN(gy)) {
                double h = hover * (0.7 + 0.6 * random.nextDouble());
                if (random.nextInt(12) == 0) h += 6.0 + random.nextDouble() * 10.0;
                ty = gy + h;
            } else {
                ty = pos.y - 2.0 - random.nextDouble() * 5.0;
            }
            Vec3 t = new Vec3(tx, ty, tz);
            if (kAt(t) > 0.8) continue;
            if (scary != null && t.distanceToSqr(scary.position()) < 26.0 * 26.0) continue;
            if (!sl.hasChunkAt(BlockPos.containing(t.x, t.y, t.z))) continue;
            if (!sl.noCollision(AABB.ofSize(t, 1.0, 1.0, 1.0))) continue;
            return t;
        }
        return pos.add((random.nextDouble() - 0.5) * 4.0, -0.5, (random.nextDouble() - 0.5) * 4.0);
    }

    // ------------------------------------------------------------ helpers

    private void initFreeHome(ServerLevel sl) {
        BubbleField.BubbleInfo info = BubbleField.isSeedKnown() ? BubbleField.bubbleAt(getX(), getY(), getZ()) : null;
        if (info != null) setHome(info, 0L);
        else anchor = this.position();
    }

    private double kAt(Vec3 p) {
        if (hasHome) {
            double dx = (p.x - hx) / hax, dy = (p.y - hy) / hay, dz = (p.z - hz) / haz;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        if (anchor != null) return p.distanceTo(anchor) / 48.0;
        return 0.0;
    }

    private Vec3 containment(Vec3 pos) {
        double k = kAt(pos);
        if (k <= K_SOFT) return Vec3.ZERO;
        Vec3 center = hasHome ? new Vec3(hx, hy, hz) : anchor;
        if (center == null) return Vec3.ZERO;
        Vec3 in = center.subtract(pos);
        double l = in.length();
        if (l < 1.0e-3) return Vec3.ZERO;
        return in.scale(1.0 / l).scale(Math.min(0.30, (k - K_SOFT) * 1.6));
    }

    private void refreshScare(ServerLevel sl, long now) {
        FairyFear fear = FairyFear.get(sl);
        Player best = null;
        double bd = 48.0 * 48.0;
        for (ServerPlayer pl : sl.players()) {
            if (pl.isSpectator() || !pl.isAlive()) continue;
            double d = pl.distanceToSqr(this);
            if (d >= bd) continue;
            if (fear.isScared(pl.getUUID(), homeId, now)) { best = pl; bd = d; }
        }
        scary = best;
    }

    private void refreshFriends(ServerLevel sl) {
        if (groupId == 0L) { friends = List.of(); return; }
        friends = sl.getEntitiesOfClass(FairyEntity.class, this.getBoundingBox().inflate(32.0),
                f -> f != this && f.groupId == groupId && f.homeId == homeId && f.isAlive());
    }

    private FairyEntity leader() {
        FairyEntity best = this;
        if (bondedTo != null) return this;
        for (FairyEntity f : friends) {
            if (f.isAlive() && f.bondedTo == null && f.getUUID().compareTo(best.getUUID()) < 0) best = f;
        }
        return best;
    }

    private void checkBond(ServerLevel sl) {
        if (bondedTo == null) return;
        Player pl = sl.getPlayerByUUID(bondedTo);
        boolean ok = pl != null && pl.isAlive() && !pl.isSpectator();
        if (ok && hasHome) {
            BubbleField.BubbleInfo b = BubbleField.bubbleAt(pl.getX(), pl.getY(), pl.getZ());
            ok = b != null && b.id == homeId;
        }
        if (!ok) bondedTo = null;
    }

    private void refreshSeparation(ServerLevel sl) {
        Vec3 push = Vec3.ZERO;
        Vec3 me = this.position();
        for (FairyEntity o : sl.getEntitiesOfClass(FairyEntity.class, this.getBoundingBox().inflate(0.9), f -> f != this)) {
            Vec3 d = me.subtract(o.position());
            double l = d.length();
            if (l < 1.0e-3) {
                push = push.add((random.nextDouble() - 0.5) * 0.02, 0.01, (random.nextDouble() - 0.5) * 0.02);
            } else {
                push = push.add(d.scale(1.0 / l).scale(0.03 * (0.9 - Math.min(l, 0.9)) / 0.9));
            }
        }
        separation = push;
    }

    private void startScout(ServerLevel sl, Player pl) {
        for (int i = 0; i < 6; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = 4.0 + random.nextDouble() * 5.0;
            Vec3 off = new Vec3(Math.cos(a) * r, 0.5 + random.nextDouble() * 2.5, Math.sin(a) * r);
            Vec3 g = pl.position().add(off);
            if (kAt(g) > 0.9) continue;
            if (!sl.hasChunkAt(BlockPos.containing(g.x, g.y, g.z))) continue;
            if (!sl.noCollision(AABB.ofSize(g, 0.8, 0.8, 0.8))) continue;
            scoutOffset = off;
            scoutLeft = 90 + random.nextInt(60);
            return;
        }
        scoutTimer = 200;
    }

    private void teleportNear(ServerLevel sl, Player pl) {
        for (int i = 0; i < 6; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            Vec3 t = pl.position().add(Math.cos(a) * 1.5, pl.getBbHeight() + 0.3 + random.nextDouble() * 0.6, Math.sin(a) * 1.5);
            if (sl.noCollision(AABB.ofSize(t, 0.6, 0.6, 0.6))) {
                this.moveTo(t.x, t.y, t.z, 0.0F, 0.0F);
                this.setDeltaMovement(Vec3.ZERO);
                return;
            }
        }
    }

    private Player nearestFriendlyPlayer(ServerLevel sl, double range) {
        FairyFear fear = FairyFear.get(sl);
        long now = sl.getGameTime();
        Player best = null;
        double bd = range * range;
        for (ServerPlayer pl : sl.players()) {
            if (pl.isSpectator() || !pl.isAlive()) continue;
            double d = pl.distanceToSqr(this);
            if (d >= bd || fear.isScared(pl.getUUID(), homeId, now)) continue;
            if (kAt(pl.position()) >= 1.0) continue;
            best = pl;
            bd = d;
        }
        return best;
    }

    // ------------------------------------------------------------ sauvegarde

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasHome", hasHome);
        tag.putLong("HomeId", homeId);
        tag.putDouble("Hx", hx); tag.putDouble("Hy", hy); tag.putDouble("Hz", hz);
        tag.putDouble("Hax", hax); tag.putDouble("Hay", hay); tag.putDouble("Haz", haz);
        tag.putLong("GroupId", groupId);
        if (bondedTo != null) tag.putUUID("Bond", bondedTo);
        if (den != null) {
            tag.putBoolean("HasDen", true);
            tag.putDouble("Dx", den.x); tag.putDouble("Dy", den.y); tag.putDouble("Dz", den.z);
        }
        if (anchor != null) {
            tag.putBoolean("HasAnchor", true);
            tag.putDouble("Ax", anchor.x); tag.putDouble("Ay", anchor.y); tag.putDouble("Az", anchor.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hasHome = tag.getBoolean("HasHome");
        homeId = tag.getLong("HomeId");
        hx = tag.getDouble("Hx"); hy = tag.getDouble("Hy"); hz = tag.getDouble("Hz");
        hax = tag.getDouble("Hax"); hay = tag.getDouble("Hay"); haz = tag.getDouble("Haz");
        if (hax == 0.0) hax = 1.0;
        if (hay == 0.0) hay = 1.0;
        if (haz == 0.0) haz = 1.0;
        groupId = tag.getLong("GroupId");
        bondedTo = tag.hasUUID("Bond") ? tag.getUUID("Bond") : null;
        if (tag.getBoolean("HasDen")) den = new Vec3(tag.getDouble("Dx"), tag.getDouble("Dy"), tag.getDouble("Dz"));
        if (tag.getBoolean("HasAnchor")) anchor = new Vec3(tag.getDouble("Ax"), tag.getDouble("Ay"), tag.getDouble("Az"));
    }
}
