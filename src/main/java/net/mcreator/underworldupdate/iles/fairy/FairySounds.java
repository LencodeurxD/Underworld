package net.mcreator.underworldupdate.iles.fairy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Locale;

/**
 * Les sons des fées : 11 types, chacun en version lumière et ténèbres (fichiers dans
 * assets/underworld/sounds/fairy/, déclarés dans assets/underworld/sounds.json avec plusieurs variantes chacun).
 * Ils s'enregistrent tout seuls (comme FairyEntities).
 */
public final class FairySounds {
    private FairySounds() {}

    public enum Kind {
        /** Une réplique de conversation (langage de cristal : le rythme et l'intonation d'une voix, sans mots). */
        VOICE,
        /** Scintillement discret d'ambiance. */
        TWINKLE,
        /** Se lie à un joueur : arpège joyeux. */
        BOND,
        /** « Eep ! » : peur, sursaut. */
        SCARE,
        /** Petits rires sautillants : jeu de chat, taquinerie. */
        GIGGLE,
        /** Victoire : gamme rapide et note tenue. */
        WIN,
        /** Bouderie : « aww » qui retombe. */
        SULK,
        /** Sieste : souffle de cristal lent. */
        SLEEP,
        /** Apparition : swell inversé puis cloche. */
        APPEAR,
        /** Appel « hé, par ici ! » en deux temps. */
        CALL,
        /** Battement d'ailes : vol rapide, looping. */
        FLUTTER;

        public final SoundEvent light;
        public final SoundEvent dark;

        Kind() {
            String id = name().toLowerCase(Locale.ROOT);
            this.light = make("fairy." + id + ".light");
            this.dark = make("fairy." + id + ".dark");
        }

        private static SoundEvent make(String key) {
            return SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FairyEntities.NAMESPACE, key));
        }
    }

    @EventBusSubscriber(modid = FairyEntities.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            event.register(Registries.SOUND_EVENT, helper -> {
                for (Kind k : Kind.values()) {
                    helper.register(k.light.getLocation(), k.light);
                    helper.register(k.dark.getLocation(), k.dark);
                }
            });
        }
    }
}
