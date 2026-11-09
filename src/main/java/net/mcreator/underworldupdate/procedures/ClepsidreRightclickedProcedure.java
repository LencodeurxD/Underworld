package net.mcreator.underworldupdate.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.underworldupdate.network.UnderworldUpdateModVariables;
import net.mcreator.underworldupdate.UnderworldUpdateMod;

public class ClepsidreRightclickedProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double restore_inv = 0;
		if (entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posx == 0) {
			UnderworldUpdateMod.LOGGER.info("tu nas pas de coordon\u00E9es de mort !");
		} else {
			{
				Entity _ent = entity;
				double _tx = entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posx;
				double _ty = entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).pozy;
				double _tz = entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posz;
				_ent.teleportTo(_tx, _ty, _tz);
				if (_ent instanceof ServerPlayer _serverPlayer)
					_serverPlayer.connection.teleport(_tx, _ty, _tz, _ent.getYRot(), _ent.getXRot());
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posx, entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).pozy,
							entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posz), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.beacon.activate")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posx, entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).pozy, entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posz,
							BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.beacon.activate")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.HEART, entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posx, entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).pozy,
						entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).posz, 1000, 3, 3, 3, 1);
			{
				UnderworldUpdateModVariables.PlayerVariables _vars = entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES);
				_vars.posx = 0;
				_vars.pozy = 0;
				_vars.posz = 0;
				_vars.markSyncDirty();
			}
		}
	}
}