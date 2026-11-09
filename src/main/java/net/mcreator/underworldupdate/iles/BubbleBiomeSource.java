package net.mcreator.underworldupdate.iles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public final class BubbleBiomeSource extends BiomeSource {
    public static final MapCodec<BubbleBiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Biome.LIST_CODEC.fieldOf("light").forGetter(s -> s.light),
            Biome.LIST_CODEC.fieldOf("dark").forGetter(s -> s.dark),
            Biome.LIST_CODEC.fieldOf("neutral").forGetter(s -> s.neutral)
    ).apply(i, BubbleBiomeSource::new));

    private final HolderSet<Biome> light;
    private final HolderSet<Biome> dark;
    private final HolderSet<Biome> neutral;

    public BubbleBiomeSource(HolderSet<Biome> light, HolderSet<Biome> dark, HolderSet<Biome> neutral) {
        this.light = light;
        this.dark = dark;
        this.neutral = neutral;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(light, dark, neutral).flatMap(HolderSet::stream);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        double bx = QuartPos.toBlock(x) + 2;
        double by = QuartPos.toBlock(y) + 2;
        double bz = QuartPos.toBlock(z) + 2;
        int type = BubbleField.typeAt(bx, by, bz);
        HolderSet<Biome> set = type > 0 ? light : type < 0 ? dark : neutral;
        if (set.size() == 0) set = firstNotEmpty();
        return set.get(Math.floorMod(BubbleField.biomeRollAt(bx, by, bz), set.size()));
    }

    private HolderSet<Biome> firstNotEmpty() {
        if (neutral.size() > 0) return neutral;
        if (light.size() > 0) return light;
        if (dark.size() > 0) return dark;
        throw new IllegalStateException("[UnderWorld] les tags underworld:light, underworld:dark et underworld:neutral sont tous vides");
    }
}
