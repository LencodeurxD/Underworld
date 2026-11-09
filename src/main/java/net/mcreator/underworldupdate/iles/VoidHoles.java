package net.mcreator.underworldupdate.iles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class VoidHoles extends SavedData {
    private static final String NAME = "underworld_update_void_holes";

    private final Set<Long> holes = new HashSet<>();
    private final Set<UUID> visited = new HashSet<>();

    public static VoidHoles get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(new SavedData.Factory<>(VoidHoles::new, VoidHoles::load, DataFixTypes.LEVEL), NAME);
    }

    private static VoidHoles load(CompoundTag tag, HolderLookup.Provider lookup) {
        VoidHoles h = new VoidHoles();
        for (long l : tag.getLongArray("holes")) h.holes.add(l);
        ListTag list = tag.getList("visited", Tag.TAG_INT_ARRAY);
        for (int i = 0; i < list.size(); i++) h.visited.add(NbtUtils.loadUUID(list.get(i)));
        return h;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        long[] arr = new long[holes.size()];
        int i = 0;
        for (long l : holes) arr[i++] = l;
        tag.putLongArray("holes", arr);
        ListTag list = new ListTag();
        for (UUID u : visited) list.add(NbtUtils.createUUID(u));
        tag.put("visited", list);
        return tag;
    }

    public boolean add(int x, int z) {
        boolean added = holes.add(BlockPos.asLong(x, 0, z));
        if (added) setDirty();
        return added;
    }

    public void remove(int x, int z) {
        if (holes.remove(BlockPos.asLong(x, 0, z))) setDirty();
    }

    public boolean has(int x, int z) {
        return holes.contains(BlockPos.asLong(x, 0, z));
    }

    public List<int[]> list() {
        List<int[]> out = new ArrayList<>(holes.size());
        for (long l : holes) out.add(new int[]{BlockPos.getX(l), BlockPos.getZ(l)});
        return out;
    }

    public boolean firstVisit(UUID id) {
        if (visited.contains(id)) return false;
        visited.add(id);
        setDirty();
        return true;
    }
}
