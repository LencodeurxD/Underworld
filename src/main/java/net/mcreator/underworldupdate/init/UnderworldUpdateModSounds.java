/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.underworldupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.underworldupdate.UnderworldUpdateMod;

public class UnderworldUpdateModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, UnderworldUpdateMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> INFINIT_RUN = REGISTRY.register("infinit_run", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("underworld_update", "infinit_run")));
	public static final DeferredHolder<SoundEvent, SoundEvent> GRAPPIN = REGISTRY.register("grappin", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("underworld_update", "grappin")));
	public static final DeferredHolder<SoundEvent, SoundEvent> INFINIT_LIGHT = REGISTRY.register("infinit_light", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("underworld_update", "infinit_light")));
	public static final DeferredHolder<SoundEvent, SoundEvent> INFINIT_DARK = REGISTRY.register("infinit_dark", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("underworld_update", "infinit_dark")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BOSSLIGHT = REGISTRY.register("bosslight", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("underworld_update", "bosslight")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BOSSDARK = REGISTRY.register("bossdark", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("underworld_update", "bossdark")));
}