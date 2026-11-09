package net.mcreator.underworldupdate.iles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.SelectMusicEvent;

import net.mcreator.underworldupdate.entity.InfinitdarkEntity;
import net.mcreator.underworldupdate.entity.PhoenixEntity;
import net.mcreator.underworldupdate.entity.VillagerbossEntity;
import net.mcreator.underworldupdate.init.UnderworldUpdateModSounds;

/**
 * Force la musique infinit_dark dans la dimension underworld:underworld,
 * y compris en creatif (ou Minecraft impose sinon sa musique creative).
 * Pres d'un boss, on coupe la musique d'ambiance pour laisser la musique de boss.
 */
@EventBusSubscriber(modid = VoidLightClient.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class UnderworldMusic {
    private UnderworldMusic() {}

    private static final ResourceKey<Level> UNDERWORLD =
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(VoidLightClient.NAMESPACE, "underworld"));

    @SubscribeEvent
    public static void onSelectMusic(SelectMusicEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null || level.dimension() != UNDERWORLD) return;

        AABB zone = player.getBoundingBox().inflate(30.0);
        boolean boss = !level.getEntitiesOfClass(PhoenixEntity.class, zone).isEmpty()
                || !level.getEntitiesOfClass(InfinitdarkEntity.class, zone).isEmpty()
                || !level.getEntitiesOfClass(VillagerbossEntity.class, zone).isEmpty();
        if (boss) {
            event.setMusic(null);
            return;
        }
        event.setMusic(new Music(UnderworldUpdateModSounds.INFINIT_DARK, 0, 0, true));
    }
}
