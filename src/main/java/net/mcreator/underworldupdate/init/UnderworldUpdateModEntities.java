/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworldupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;

import net.mcreator.underworldupdate.entity.*;
import net.mcreator.underworldupdate.UnderworldUpdateMod;

@EventBusSubscriber
public class UnderworldUpdateModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, UnderworldUpdateMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<TeteEntity>> TETE = register("tete",
			EntityType.Builder.<TeteEntity>of(TeteEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.1f, 0.1f));
	public static final DeferredHolder<EntityType<?>, EntityType<TrouEntity>> TROU = register("trou",
			EntityType.Builder.<TrouEntity>of(TrouEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.2f, 0.2f));
	public static final DeferredHolder<EntityType<?>, EntityType<PhoenixEntity>> PHOENIX = register("phoenix",
			EntityType.Builder.<PhoenixEntity>of(PhoenixEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(0.8f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<InfinitdarkEntity>> INFINITDARK = register("infinitdark",
			EntityType.Builder.<InfinitdarkEntity>of(InfinitdarkEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(4.5f, 0.4f));
	public static final DeferredHolder<EntityType<?>, EntityType<InfinitdarkEntityProjectile>> INFINITDARK_PROJECTILE = register("projectile_infinitdark",
			EntityType.Builder.<InfinitdarkEntityProjectile>of(InfinitdarkEntityProjectile::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<VillagerbossEntity>> VILLAGERBOSS = register("villagerboss",
			EntityType.Builder.<VillagerbossEntity>of(VillagerbossEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(0.7f, 2.4f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(RegisterSpawnPlacementsEvent event) {
		PhoenixEntity.init(event);
		InfinitdarkEntity.init(event);
		VillagerbossEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(PHOENIX.get(), PhoenixEntity.createAttributes().build());
		event.put(INFINITDARK.get(), InfinitdarkEntity.createAttributes().build());
		event.put(VILLAGERBOSS.get(), VillagerbossEntity.createAttributes().build());
	}
}