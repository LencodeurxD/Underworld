package net.mcreator.underworldupdate.swap;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Machine à états côté client :
 *   IDLE   -> clic droit -> SELECT : une onde part du joueur et passe le monde en noir + contours
 *   SELECT -> clic droit sur une entité visée -> SWAP : portail sous les deux entités, échange,
 *             puis une onde de retour (centrée sur le joueur, à sa nouvelle position) rend le monde normal
 *   SELECT -> clic droit dans le vide / changement d'item -> onde de retour
 */
@EventBusSubscriber(modid = UnderworldUpdateMod.MODID, value = Dist.CLIENT)
public final class SwapClient {
    private enum Mode { IDLE, SELECT, SWAP }

    // ---- Chronologie (en ticks, 20 ticks = 1 s) ----
    public static final int WAVE_TICKS = 28;                          // durée d'une onde
    public static final float WAVE_MAX = 260f;                        // rayon final d'une onde (blocs)
    private static final float WAVE_INF = 1.0e6f;                     // « onde terminée » : tout est couvert
    private static final int SWAP_AT = SwapServer.SWAP_DELAY_TICKS;   // l'échange a lieu ici
    private static final int EXIT_AT = SWAP_AT + 8;                   // départ de l'onde de retour
    private static final int SWAP_END = 46;                           // fin des portails
    private static final float HOLE_RADIUS = 2.6f;                    // taille des portails (blocs)
    private static final int MAX_SELECT_TICKS = 20 * 30;              // annule la sélection après 30 s

    public static int ticks;

    private static Mode mode = Mode.IDLE;
    private static int lastUseTick = -100;
    private static int selectStart;
    private static int swapTick;
    private static int targetId = -1;
    private static int enterTick = -1;   // >= 0 : l'effet est actif (compte depuis le départ de l'onde sortante)
    private static int exitTick = -1;    // >= 0 : l'onde de retour est partie
    private static Vec3 waveCenter = Vec3.ZERO;
    private static Vec3 holeA = Vec3.ZERO;
    private static Vec3 holeB = Vec3.ZERO;

    private SwapClient() {}

    // ------------------------------------------------------------------ clics

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem e) { intercept(e); }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) { intercept(e); }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract e) { intercept(e); }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific e) { intercept(e); }

    /** Avale le clic droit (pas d'ouverture de GUI de villageois, de porte, etc.) quand on tient l'item. */
    private static <T extends PlayerInteractEvent & ICancellableEvent> void intercept(T event) {
        if (!event.getLevel().isClientSide() || event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!SwapItem.is(event.getItemStack())) return;
        event.setCanceled(true);
        onUse(event.getItemStack());
    }

    private static void onUse(ItemStack stack) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // Clic maintenu : Minecraft répète l'action toutes les 4 ticks, on ne compte qu'un seul clic.
        boolean held = ticks - lastUseTick <= 5;
        lastUseTick = ticks;
        if (held || mode == Mode.SWAP) return;

        if (mode == Mode.IDLE) {
            if (enterTick >= 0) return;   // l'onde de retour est encore en cours
            if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
            mode = Mode.SELECT;
            selectStart = ticks;
            enterTick = 0;
            exitTick = -1;
            waveCenter = player.position();
            return;
        }

        // Mode.SELECT : second clic
        LivingEntity target = SwapTargeting.findTarget(player);
        if (target == null) {
            cancel(player);
        } else {
            startSwap(player, target);
        }
    }

    private static void startSwap(Player player, LivingEntity target) {
        mode = Mode.SWAP;
        swapTick = 0;
        targetId = target.getId();
        holeA = player.position();
        holeB = target.position();
        PacketDistributor.sendToServer(new SwapRequestPayload(target.getId()));
    }

    private static void cancel(Player player) {
        mode = Mode.IDLE;
        startExit(player.position());
    }

    /** Lance l'onde de retour depuis une position donnée (l'onde sortante est considérée terminée). */
    private static void startExit(Vec3 center) {
        if (enterTick < 0 || exitTick >= 0) return;
        enterTick = WAVE_TICKS;
        exitTick = 0;
        waveCenter = center;
    }

    private static void reset() {
        mode = Mode.IDLE;
        enterTick = -1;
        exitTick = -1;
    }

    // ------------------------------------------------------------------ tick

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) return;
        ticks++;

        if (mc.player == null || mc.level == null) {
            reset();
            return;
        }

        if (enterTick >= 0 && enterTick < WAVE_TICKS) enterTick++;
        if (exitTick >= 0 && ++exitTick >= WAVE_TICKS) {
            reset();   // le monde est entièrement revenu à la normale
            return;
        }

        if (mode == Mode.SELECT) {
            if (!SwapItem.is(mc.player.getMainHandItem()) || ticks - selectStart > MAX_SELECT_TICKS) cancel(mc.player);
        } else if (mode == Mode.SWAP) {
            swapTick++;
            if (swapTick == EXIT_AT) startExit(mc.player.position());
            if (swapTick >= SWAP_END) mode = Mode.IDLE;
        }
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (mode == Mode.SWAP && swapTick < SWAP_AT + 6) {
            event.getInput().forwardImpulse = 0f;
            event.getInput().leftImpulse = 0f;
            event.getInput().jumping = false;
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (enterTick >= 5 && exitTick < 6) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        if (isActive()) event.setCanceled(true);
    }

    // ------------------------------------------------------- valeurs pour le rendu

    public static boolean isActive() {
        return enterTick >= 0;
    }

    /** Rayon de l'onde sortante : au-delà, le monde est encore normal ; en deçà, il est noir + contours. */
    public static float outerRadius(float pt) {
        return enterTick < 0 ? 0f : waveRadius(enterTick, pt);
    }

    /** Rayon de l'onde de retour : en deçà, le monde est redevenu normal. */
    public static float innerRadius(float pt) {
        return exitTick < 0 ? 0f : waveRadius(exitTick, pt);
    }

    public static Vec3 waveCenter() { return waveCenter; }

    private static float waveRadius(int tick, float pt) {
        float x = (tick + pt) / WAVE_TICKS;
        if (x >= 1f) return WAVE_INF;
        return WAVE_MAX * x * (1.5f - 0.5f * x);   // part vite puis ralentit légèrement
    }

    /** Rayon des portails : ils s'ouvrent, grossissent un peu, puis se referment après l'échange. */
    public static float holeRadius(float pt) {
        if (mode != Mode.SWAP) return 0f;
        float t = swapTick + pt;
        if (t < 12f) return HOLE_RADIUS * easeOut(t / 12f);
        if (t < SWAP_AT) return Mth.lerp(smooth((t - 12f) / (SWAP_AT - 12f)), HOLE_RADIUS, HOLE_RADIUS * 1.25f);
        return HOLE_RADIUS * 1.25f * (1f - easeOut((t - SWAP_AT) / 20f));
    }

    /** Les portails suivent les entités jusqu'à l'échange, puis restent à leur ancienne place. */
    public static void updateHoles(float pt) {
        if (mode != Mode.SWAP || swapTick + pt >= SWAP_AT) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        holeA = mc.player.getPosition(pt);
        Entity target = mc.level.getEntity(targetId);
        if (target != null) holeB = target.getPosition(pt);
    }

    public static Vec3 holeA() { return holeA; }

    public static Vec3 holeB() { return holeB; }

    private static float smooth(float x) {
        x = Mth.clamp(x, 0f, 1f);
        return x * x * (3f - 2f * x);
    }

    private static float easeOut(float x) {
        float i = 1f - Mth.clamp(x, 0f, 1f);
        return 1f - i * i * i;
    }
}