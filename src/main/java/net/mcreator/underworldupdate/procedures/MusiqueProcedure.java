package net.mcreator.underworldupdate.procedures;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.underworldupdate.network.UnderworldUpdateModVariables;
import net.mcreator.underworldupdate.entity.VillagerbossEntity;
import net.mcreator.underworldupdate.entity.PhoenixEntity;
import net.mcreator.underworldupdate.entity.InfinitdarkEntity;
import net.mcreator.underworldupdate.UnderworldUpdateMod;

import javax.annotation.Nullable;

@EventBusSubscriber
public class MusiqueProcedure {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).music) {
			if (!(!world.getEntitiesOfClass(PhoenixEntity.class, new AABB(Vec3.ZERO, Vec3.ZERO).move(new Vec3(x, y, z)).inflate(60 / 2d), e -> true).isEmpty()
					|| !world.getEntitiesOfClass(InfinitdarkEntity.class, new AABB(Vec3.ZERO, Vec3.ZERO).move(new Vec3(x, y, z)).inflate(60 / 2d), e -> true).isEmpty()
					|| !world.getEntitiesOfClass(VillagerbossEntity.class, new AABB(Vec3.ZERO, Vec3.ZERO).move(new Vec3(x, y, z)).inflate(60 / 2d), e -> true).isEmpty())) {
				{
					Entity _ent = entity;
					if (!_ent.level().isClientSide() && _ent.getServer() != null) {
						_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
								_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "stopsound @s music");
					}
				}
				UnderworldUpdateMod.queueServerWork(2, () -> {
					{
						Entity _ent = entity;
						if (!_ent.level().isClientSide() && _ent.getServer() != null) {
							_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
									_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), "playsound minecraft:music.game music @s");
						}
					}
				});
				{
					UnderworldUpdateModVariables.PlayerVariables _vars = entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES);
					_vars.music = false;
					_vars.markSyncDirty();
				}
			}
		}
	}
}