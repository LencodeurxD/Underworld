package net.mcreator.underworldupdate.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.Comparator;

public class SolarbeamEvenementAuClicDroitDansLairProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		Vec3 laser = Vec3.ZERO;
		Vec3 pas = Vec3.ZERO;
		if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z)) && world instanceof Level _lvl1 && _lvl1.isDay()) {
			laser = entity.getLookAngle();
			pas = laser.scale(1.0 / 8);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			while (((entity.getLookAngle()).scale(30)).length() + 0.5 > laser.length() && !world.getBlockState(BlockPos.containing(x + laser.x(), y + laser.y() + 1.6, z + laser.z())).canOcclude()) {
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.FLAME, (x + laser.x()), (y + laser.y() + 1.6), (z + laser.z()), 2, 0, 0, 0, 0);
				laser = laser.add(pas);
				if (!((findEntityInWorldRange(world, LivingEntity.class, (x + laser.x()), (y + laser.y()), (z + laser.z()), 2)) == null)
						&& !((findEntityInWorldRange(world, LivingEntity.class, (x + laser.x()), (y + laser.y()), (z + laser.z()), 2)) == entity)) {
					(findEntityInWorldRange(world, LivingEntity.class, (x + laser.x()), (y + laser.y()), (z + laser.z()), 2)).igniteForSeconds(3);
				}
			}
			if (entity instanceof Player _player)
				_player.getCooldowns().addCooldown(itemstack.getItem(), 20);
			if (world instanceof Level _level && !_level.isClientSide())
				_level.explode(null, (x + laser.x()), (y + laser.y() + 1.6), (z + laser.z()), 1, Level.ExplosionInteraction.TNT);
			if (world instanceof ServerLevel _level) {
				itemstack.hurtAndBreak(1, _level, null, _stkprov -> {
				});
			}
		}
	}

	private static Entity findEntityInWorldRange(LevelAccessor world, Class<? extends Entity> clazz, double x, double y, double z, double range) {
		return (Entity) world.getEntitiesOfClass(clazz, AABB.ofSize(new Vec3(x, y, z), range, range, range), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z))).findFirst().orElse(null);
	}
}