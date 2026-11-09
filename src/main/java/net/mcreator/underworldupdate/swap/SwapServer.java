package net.mcreator.underworldupdate.swap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Côté serveur : valide la demande, attend la fin de l'animation, puis échange les positions. */
@EventBusSubscriber(modid = UnderworldUpdateMod.MODID)
public final class SwapServer {
    /** Délai avant l'échange : doit correspondre au moment où l'écran est englouti côté client. */
    public static final int SWAP_DELAY_TICKS = 24;
    private static final int COOLDOWN_TICKS = 60;
    private static final double MAX_RANGE = SwapTargeting.RANGE + 4.0;

    private static final class Pending {
        final UUID playerId;
        final UUID targetId;
        int ticksLeft = SWAP_DELAY_TICKS;

        Pending(UUID playerId, UUID targetId) {
            this.playerId = playerId;
            this.targetId = targetId;
        }
    }

    private static final List<Pending> PENDING = new ArrayList<>();

    private SwapServer() {}

    public static void request(ServerPlayer player, int targetEntityId) {
        if (!SwapItem.is(player.getMainHandItem())) return;
        for (Pending p : PENDING) {
            if (p.playerId.equals(player.getUUID())) return;
        }
        if (player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) return;

        Entity e = player.serverLevel().getEntity(targetEntityId);
        if (!(e instanceof LivingEntity target) || target == player || !target.isAlive() || target.isSpectator()) return;
        if (player.distanceToSqr(target) > MAX_RANGE * MAX_RANGE || !player.hasLineOfSight(target)) return;

        PENDING.add(new Pending(player.getUUID(), target.getUUID()));
        player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), COOLDOWN_TICKS);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (PENDING.isEmpty()) return;
        Iterator<Pending> it = PENDING.iterator();
        while (it.hasNext()) {
            Pending p = it.next();
            ServerPlayer player = event.getServer().getPlayerList().getPlayer(p.playerId);
            Entity target = player == null ? null : player.serverLevel().getEntity(p.targetId);
            if (player == null || !player.isAlive() || target == null || !target.isAlive()
                    || target.level() != player.level()) {
                it.remove();
                continue;
            }
            if (target instanceof Mob mob) mob.getNavigation().stop();   // la cible reste en place pendant l'animation
            if (--p.ticksLeft > 0) continue;
            it.remove();
            swap(player, target);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        PENDING.clear();
    }

    private static void swap(Entity a, Entity b) {
        Vec3 posA = a.position();
        Vec3 posB = b.position();
        moveTo(a, posB);
        moveTo(b, posA);
    }

    private static void moveTo(Entity entity, Vec3 pos) {
        if (entity.isPassenger()) entity.stopRiding();
        entity.teleportTo(pos.x, pos.y, pos.z);
        if (entity instanceof ServerPlayer sp) {
            sp.connection.teleport(pos.x, pos.y, pos.z, sp.getYRot(), sp.getXRot());
        }
        entity.resetFallDistance();
        entity.setDeltaMovement(Vec3.ZERO);
    }
}
