package net.mcreator.underworldupdate.iles.fairy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Les deux types de fées : underworld:light_fairy (jaune) et underworld:dark_fairy (mauve).
 * Elles s'enregistrent toutes seules (comme BubbleGen), rien à ajouter dans UnderworldUpdateMod.
 */
public final class FairyEntities {
    public static final String MOD_ID = "underworld_update";
    public static final String NAMESPACE = "underworld";
    /** Dimension où vivent les fées. */
    public static final String DIMENSION_NAMESPACE = "underworld";
    public static final String DIMENSION_PATH = "underworld";

    public static final EntityType<FairyEntity> LIGHT = build("light_fairy");
    public static final EntityType<FairyEntity> DARK = build("dark_fairy");

    private FairyEntities() {}

    private static EntityType<FairyEntity> build(String id) {
        return EntityType.Builder.<FairyEntity>of(FairyEntity::new, MobCategory.CREATURE)
                .setShouldReceiveVelocityUpdates(true).setTrackingRange(48).setUpdateInterval(1)
                .fireImmune().sized(0.45f, 0.45f).build(id);
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            event.register(Registries.ENTITY_TYPE, helper -> {
                helper.register(ResourceLocation.fromNamespaceAndPath(NAMESPACE, "light_fairy"), LIGHT);
                helper.register(ResourceLocation.fromNamespaceAndPath(NAMESPACE, "dark_fairy"), DARK);
            });
        }

        @SubscribeEvent
        public static void onAttributes(EntityAttributeCreationEvent event) {
            event.put(LIGHT, FairyEntity.createAttributes().build());
            event.put(DARK, FairyEntity.createAttributes().build());
        }
    }
}
