package net.mcreator.underworldupdate.iles;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class BubbleGen {
    private BubbleGen() {}

    @EventBusSubscriber(modid = VoidLightClient.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            event.register(Registries.FEATURE, helper ->
                    helper.register(ResourceLocation.fromNamespaceAndPath(VoidLightClient.NAMESPACE, "bubble_decor"), new BubbleDecorFeature()));
            event.register(Registries.BIOME_SOURCE, helper ->
                    helper.register(ResourceLocation.fromNamespaceAndPath(VoidLightClient.NAMESPACE, "bubble"), BubbleBiomeSource.CODEC));
            event.register(Registries.DENSITY_FUNCTION_TYPE, helper -> {
                helper.register(ResourceLocation.fromNamespaceAndPath(VoidLightClient.NAMESPACE, "bubble_solid"), BubbleDensity.SOLID_CODEC.codec());
                helper.register(ResourceLocation.fromNamespaceAndPath(VoidLightClient.NAMESPACE, "bubble_type"), BubbleDensity.TYPE_CODEC.codec());
                helper.register(ResourceLocation.fromNamespaceAndPath(VoidLightClient.NAMESPACE, "bubble_flavor"), BubbleDensity.FLAVOR_CODEC.codec());
            });
        }
    }

    @EventBusSubscriber(modid = VoidLightClient.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static final class ServerHooks {
        @SubscribeEvent
        public static void onAboutToStart(ServerAboutToStartEvent event) {
            long seed = event.getServer().getWorldData().worldGenOptions().seed();
            BubbleField.setSeed(seed);
        }
    }
}
