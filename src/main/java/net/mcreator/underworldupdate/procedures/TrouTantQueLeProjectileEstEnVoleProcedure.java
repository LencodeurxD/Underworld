package net.mcreator.underworldupdate.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.underworldupdate.UnderworldUpdateMod;

import java.util.Comparator;

public class TrouTantQueLeProjectileEstEnVoleProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
		if (immediatesourceentity == null)
			return;
		UnderworldUpdateMod.queueServerWork(10, () -> {
			{
				final Vec3 _center = new Vec3(x, y, z);
				for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(15 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
					entityiterator.push(((((immediatesourceentity.position()).subtract((entityiterator.position()))).normalize()).x()), ((((immediatesourceentity.position()).subtract((entityiterator.position()))).normalize()).y()),
							((((immediatesourceentity.position()).subtract((entityiterator.position()))).normalize()).z()));
					if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 30, 1));
				}
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 10, 1, 1, 1, 0);
		});
	}
}