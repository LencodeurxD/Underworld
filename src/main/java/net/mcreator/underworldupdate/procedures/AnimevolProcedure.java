package net.mcreator.underworldupdate.procedures;

import net.minecraft.world.entity.Entity;

public class AnimevolProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if (!entity.onGround()) {
			return true;
		}
		return false;
	}
}