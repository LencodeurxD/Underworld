package net.mcreator.underworldupdate.swap;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Identifie l'item « Switchenderperle » (reste déclaré dans MCreator, mais sans procédure). */
public final class SwapItem {
    // Nom de registre de l'item MCreator : à adapter si le tien est différent.
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(UnderworldUpdateMod.MODID, "switchenderperle");

    private SwapItem() {}

    public static boolean is(ItemStack stack) {
        return !stack.isEmpty() && ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }
}
