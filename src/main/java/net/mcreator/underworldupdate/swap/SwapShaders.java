package net.mcreator.underworldupdate.swap;

import java.io.IOException;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid = UnderworldUpdateMod.MODID, value = Dist.CLIENT)
public final class SwapShaders {
    public static ShaderInstance swapWorld;

    private SwapShaders() {}

    @SubscribeEvent
    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(UnderworldUpdateMod.MODID, "swap_world"),
                        DefaultVertexFormat.POSITION),
                shader -> swapWorld = shader);
    }
}