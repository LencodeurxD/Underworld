package net.mcreator.underworldupdate.procedures;

import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.underworldupdate.network.UnderworldUpdateModVariables;
import net.mcreator.underworldupdate.entity.PhoenixEntity;

import javax.annotation.Nullable;

@EventBusSubscriber
public class AtaakProcedure {
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
		if (sourceentity instanceof PhoenixEntity) {
			if (2 == Mth.nextInt(RandomSource.create(), 1, 25)) {
				h = ((entity.position()).subtract((new Vec3(x, y, z)))).normalize();
				pas = h.scale(1.0 / 16);
				while (((entity.position()).subtract((new Vec3(x, y, z)))).length() + 0.5 > h.length()) {
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.FLAME, (x + h.x()), (y + h.y() + 1.6), (z + h.z()), 2, 0, 0, 0, 0);
					if (Math.round(x + h.x()) == Math.round(entity.getX()) && Math.round(y + h.y()) == Math.round(entity.getY()) && Math.round(z + h.z()) == Math.round(entity.getZ())) {
						entity.igniteForSeconds(2);
						entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 2);
					}
					h = h.add(pas);
				}
			}
			if (3 == Mth.nextInt(RandomSource.create(), 1, 30)) {
				sourceentity.push((sourceentity.getLookAngle().x), (sourceentity.getLookAngle().y), (sourceentity.getLookAngle().z));
			}
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
								_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "playsound underworld_update:bosslight music");
					}
				}
				{
					UnderworldUpdateModVariables.PlayerVariables _vars = entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES);
					_vars.music = true;
					_vars.markSyncDirty();
				}
			}
		}
	}
}