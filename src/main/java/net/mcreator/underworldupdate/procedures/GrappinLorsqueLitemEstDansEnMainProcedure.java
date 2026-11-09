package net.mcreator.underworldupdate.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;

import net.mcreator.underworldupdate.network.UnderworldUpdateModVariables;

public class GrappinLorsqueLitemEstDansEnMainProcedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("t")) {
			{
				UnderworldUpdateModVariables.PlayerVariables _vars = entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES);
				_vars.v = (((entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).xyz.subtract((entity.position()))).normalize()).scale(0.35)).add((((entity.getDeltaMovement()).normalize()).scale(0.05)));
				_vars.markSyncDirty();
			}
			entity.push((entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).v.x()), (entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).v.y()), (entity.getData(UnderworldUpdateModVariables.PLAYER_VARIABLES).v.z()));
		}
	}
}