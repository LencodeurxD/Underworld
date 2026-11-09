package net.mcreator.underworldupdate.procedures;

import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.underworldupdate.network.UnderworldUpdateModVariables;
import net.mcreator.underworldupdate.entity.VillagerbossEntity;
import net.mcreator.underworldupdate.UnderworldUpdateMod;

import javax.annotation.Nullable;

@EventBusSubscriber
public class AttakProcedure {
	@SubscribeEvent
	public static void onEntitySetsAttackTarget(LivingChangeTargetEvent event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getOriginalAboutToBeSetTarget(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		execute(null, world, x, y, z, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		Vec3 pas = Vec3.ZERO;
		Vec3 h = Vec3.ZERO;
		if (sourceentity instanceof VillagerbossEntity) {
			if (!entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).music) {
				{
					Entity _ent = entity;
					if (!_ent.level().isClientSide() && _ent.getServer() != null) {
						_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
								_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "stopsound @s music");
					}
				}
				{
					Entity _ent = entity;
					if (!_ent.level().isClientSide() && _ent.getServer() != null) {
						_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
								_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "playsound underworld_update:infinit_run music @s ~ ~ ~ 0.05");
					}
				}
				{
					UnderworldUpdateModVariables.PlayerVariables _vars = entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES);
					_vars.music = true;
					_vars.markSyncDirty();
				}
			}
			if (3 == Mth.nextInt(RandomSource.create(), 1, 40)) {
				if (sourceentity instanceof VillagerbossEntity _datEntSetL)
					_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_main, true);
				sourceentity.push((((entity.position()).subtract((sourceentity.position()))).x() / 2), (((entity.position()).subtract((sourceentity.position()))).y() / 2), (((entity.position()).subtract((sourceentity.position()))).z() / 2));
				UnderworldUpdateMod.queueServerWork(40, () -> {
					if (sourceentity instanceof VillagerbossEntity _datEntSetL)
						_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_main, false);
				});
			}
			if (7 == Mth.nextInt(RandomSource.create(), 1, 50)) {
				entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
			}
			if (3 == Mth.nextInt(RandomSource.create(), 1, 40)) {
				if (sourceentity instanceof VillagerbossEntity _datEntSetL)
					_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_tp, true);
				UnderworldUpdateMod.queueServerWork(30, () -> {
					{
						Entity _ent = sourceentity;
						double _tx = (x + sourceentity.getLookAngle().x * 3);
						double _ty = (y + sourceentity.getLookAngle().y * 3);
						double _tz = (z + sourceentity.getLookAngle().z * 3);
						_ent.teleportTo(_tx, _ty, _tz);
						if (_ent instanceof ServerPlayer _serverPlayer)
							_serverPlayer.connection.teleport(_tx, _ty, _tz, _ent.getYRot(), _ent.getXRot());
					}
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.GLOW, x, y, z, 15, 2, 2, 2, 1);
					if (sourceentity instanceof VillagerbossEntity _datEntSetL)
						_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_tp, false);
				});
			}
			if (5 == Mth.nextInt(RandomSource.create(), 1, 50)) {
				if (sourceentity instanceof VillagerbossEntity _datEntSetL)
					_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_fleche, true);
				sourceentity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
				for (int _i1 = 0; _i1 < Mth.nextInt(RandomSource.create(), 4, 7); _i1++) {
					UnderworldUpdateMod.queueServerWork(5, () -> {
						{
							Entity _shootFrom = sourceentity;
							Level projectileLevel = _shootFrom.level();
							if (!projectileLevel.isClientSide()) {
								Projectile _entityToSpawn = initArrowProjectile(new Arrow(projectileLevel, 0, 0, 0, new Arrow(EntityType.ARROW, projectileLevel).getPickupItemStackOrigin(), createArrowWeaponItemStack(projectileLevel, 2, (byte) 8)),
										null, 3, false, true, false, AbstractArrow.Pickup.DISALLOWED);
								_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
								_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, 2, 1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
					});
					continue;
				}
				UnderworldUpdateMod.queueServerWork(50, () -> {
					if (sourceentity instanceof VillagerbossEntity _datEntSetL)
						_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_fleche, false);
				});
			}
			if (4 == Mth.nextInt(RandomSource.create(), 1, 300)) {
				if (sourceentity instanceof VillagerbossEntity _datEntSetL)
					_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_invoc, true);
				for (int _i1 = 0; _i1 < 3; _i1++) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = EntityType.VEX.spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
				}
				sourceentity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
				UnderworldUpdateMod.queueServerWork(50, () -> {
					if (sourceentity instanceof VillagerbossEntity _datEntSetL)
						_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_invoc, false);
				});
			}
			if (6 == Mth.nextInt(RandomSource.create(), 1, 200)) {
				if (sourceentity instanceof VillagerbossEntity _datEntSetL)
					_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_beam, true);
				h = ((entity.position()).subtract((new Vec3(x, y, z)))).normalize();
				pas = h.scale(1.0 / 16);
				while (((entity.position()).subtract((new Vec3(x, y, z)))).length() + 0.5 > h.length()) {
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.warden.attack_impact")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.warden.attack_impact")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.SONIC_BOOM, (x + h.x()), (y + h.y() + 1.6), (z + h.z()), 2, 0, 0, 0, 0);
					if (Math.round(x + h.x()) == Math.round(entity.getX()) && Math.round(y + h.y()) == Math.round(entity.getY()) && Math.round(z + h.z()) == Math.round(entity.getZ())) {
						entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 3);
					}
					h = h.add(pas);
				}
				UnderworldUpdateMod.queueServerWork(30, () -> {
					if (sourceentity instanceof VillagerbossEntity _datEntSetL)
						_datEntSetL.getEntityData().set(VillagerbossEntity.DATA_beam, false);
				});
			}
		}
	}

	private static AbstractArrow initArrowProjectile(AbstractArrow entityToSpawn, Entity shooter, float damage, boolean silent, boolean fire, boolean particles, AbstractArrow.Pickup pickup) {
		entityToSpawn.setOwner(shooter);
		entityToSpawn.setBaseDamage(damage);
		if (silent)
			entityToSpawn.setSilent(true);
		if (fire)
			entityToSpawn.igniteForSeconds(100);
		if (particles)
			entityToSpawn.setCritArrow(true);
		entityToSpawn.pickup = pickup;
		return entityToSpawn;
	}

	private static ItemStack createArrowWeaponItemStack(Level level, int knockback, byte piercing) {
		ItemStack weapon = new ItemStack(Items.ARROW);
		if (knockback > 0)
			weapon.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.KNOCKBACK), knockback);
		if (piercing > 0)
			weapon.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.PIERCING), piercing);
		return weapon;
	}
}