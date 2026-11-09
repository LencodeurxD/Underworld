package net.mcreator.underworldupdate.iles.fairy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Mémorise quel joueur a fait peur aux fées de quelle bulle, et jusqu'à quand (10 minutes). */
public final class FairyFear extends SavedData {
    public static final long DURATION = 12000L;
    private static final String NAME = "underworld_update_fairy_fear";

    private final Map<String, Long> until = new HashMap<>();

    public static FairyFear get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(FairyFear::new, FairyFear::load, DataFixTypes.LEVEL), NAME);
    }

    private static String key(UUID player, long bubble) {
        return player + "|" + bubble;
    }

    private static FairyFear load(CompoundTag tag, HolderLookup.Provider lookup) {
        FairyFear f = new FairyFear();
        ListTag list = tag.getList("fear", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag e = list.getCompound(i);
            f.until.put(e.getString("k"), e.getLong("t"));
        }
        return f;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        ListTag list = new ListTag();
        for (Map.Entry<String, Long> en : until.entrySet()) {
            CompoundTag e = new CompoundTag();
            e.putString("k", en.getKey());
            e.putLong("t", en.getValue());
            list.add(e);
        }
        tag.put("fear", list);
        return tag;
    }

    public void scare(UUID player, long bubble, long untilTick) {
        until.put(key(player, bubble), untilTick);
        if (until.size() > 256) until.values().removeIf(t -> t < untilTick - DURATION);
        setDirty();
    }

    public boolean isScared(UUID player, long bubble, long now) {
        Long t = until.get(key(player, bubble));
        return t != null && t > now;
    }
}
