package net.mcreator.underworldupdate.swap;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client -> serveur : « échange-moi avec l'entité d'id targetId ». */
public record SwapRequestPayload(int targetId) implements CustomPacketPayload {
    public static final Type<SwapRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(UnderworldUpdateMod.MODID, "swap_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SwapRequestPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, SwapRequestPayload::targetId, SwapRequestPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
