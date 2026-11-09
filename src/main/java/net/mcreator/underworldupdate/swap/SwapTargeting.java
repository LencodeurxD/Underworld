package net.mcreator.underworldupdate.swap;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Trouve l'entité vivante que le joueur regarde (à longue portée, bloquée par les blocs). */
public final class SwapTargeting {
    public static final double RANGE = 64.0;

    private SwapTargeting() {}

    @Nullable
    public static LivingEntity findTarget(Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(RANGE));

        BlockHitResult block = player.level().clip(
                new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double maxDist = block.getType() == HitResult.Type.MISS ? RANGE : block.getLocation().distanceTo(eye);

        Vec3 limit = eye.add(look.scale(maxDist));
        AABB box = player.getBoundingBox().expandTowards(look.scale(maxDist)).inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(player, eye, limit, box,
                e -> e instanceof LivingEntity && e != player && !e.isSpectator() && e.isPickable(),
                maxDist * maxDist);

        return hit == null ? null : (LivingEntity) hit.getEntity();
    }
}
