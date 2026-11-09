package net.mcreator.underworldupdate.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.underworldupdate.entity.VillagerbossEntity;

public class LanceProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if (entity instanceof VillagerbossEntity _datEntL0 && _datEntL0.getEntityData().get(VillagerbossEntity.DATA_fleche)) {
			return true;
		}
		return false;
	}
}