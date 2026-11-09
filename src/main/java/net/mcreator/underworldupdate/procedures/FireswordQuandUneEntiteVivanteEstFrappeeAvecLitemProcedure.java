package net.mcreator.underworldupdate.procedures;

import net.minecraft.world.entity.Entity;

public class FireswordQuandUneEntiteVivanteEstFrappeeAvecLitemProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.igniteForSeconds(5);
	}
}