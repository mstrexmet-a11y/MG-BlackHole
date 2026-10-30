package com.mgblackhole.client;

import com.mgblackhole.MGBlackHole;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = MGBlackHole.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BlackHoleClient {
    private static final KeyMapping TOGGLE_KEY = new KeyMapping("key.mgblackhole.toggle", GLFW.GLFW_KEY_B, "key.categories.mgblackhole");
    private static boolean enabled = false;
    private static int ticks = 0;
    private BlackHoleClient() { }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) { event.register(TOGGLE_KEY); }

    @Mod.EventBusSubscriber(modid = MGBlackHole.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeEvents {
        private ForgeEvents() { }
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            while (TOGGLE_KEY.consumeClick()) {
                enabled = !enabled;
                if (mc.player != null) mc.player.displayClientMessage(net.minecraft.network.chat.Component.translatable(enabled ? "message.mgblackhole.enabled" : "message.mgblackhole.disabled"), true);
            }
            if (!enabled || mc.level == null || mc.player == null) return;
            ticks++;
            Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
            Vec3 forward = mc.gameRenderer.getMainCamera().getLookVector();
            Vec3 center = camera.add(forward.scale(7.0));
            double rotation = ticks * 0.075;
            for (int i = 0; i < 10; i++) {
                double angle = rotation + (Math.PI * 2.0 * i / 10.0);
                double radius = 0.72 + 0.08 * Math.sin(ticks * 0.12 + i);
                double x = Math.cos(angle) * radius;
                double y = Math.sin(angle) * radius * 0.34;
                double z = Math.sin(angle) * radius;
                mc.level.addParticle(ParticleTypes.PORTAL, center.x + x, center.y + y, center.z + z, -x * 0.018, 0.005, -z * 0.018);
                if (i % 2 == 0) mc.level.addParticle(ParticleTypes.END_ROD, center.x + x * 1.12, center.y + y * 1.12, center.z + z * 1.12, 0.0, 0.0, 0.0);
            }
            for (int i = 0; i < 3; i++) {
                double angle = rotation * 1.7 + i * (Math.PI * 2.0 / 3.0);
                double radius = 0.35 + (i * 0.12);
                mc.level.addParticle(ParticleTypes.REVERSE_PORTAL, center.x + Math.cos(angle) * radius, center.y + Math.sin(angle) * radius * 0.45, center.z + Math.sin(angle) * radius, 0.0, 0.0, 0.0);
            }
        }
    }
}
