package net.mcreator.underworldupdate.client;

import java.io.IOException;

import net.mcreator.underworldupdate.UnderworldUpdateMod;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

@EventBusSubscriber(modid = UnderworldUpdateMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ClientShaderHandler {
    public static ShaderInstance lightGrassShader;
    public static ShaderInstance darkGrassShader;

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(UnderworldUpdateMod.MODID, "lightgrass_shader"),
                DefaultVertexFormat.BLOCK
            ),
            shader -> lightGrassShader = shader
        );
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(UnderworldUpdateMod.MODID, "darkgrass_shader"),
                DefaultVertexFormat.BLOCK
            ),
            shader -> darkGrassShader = shader
        );
    }
}