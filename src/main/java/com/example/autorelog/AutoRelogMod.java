package com.example.autorelog;

import com.example.autorelog.config.ModConfig;
import com.example.autorelog.gui.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AutoRelogMod implements ClientModInitializer {
    private static ModConfig config;
    private static KeyBinding toggleKeyBinding;
    private static KeyBinding openGuiKeyBinding;

    private static boolean isRelogging = false;
    private static long lastRelogTime = 0;

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();

        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autorelog.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.autorelog"
        ));

        openGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autorelog.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.autorelog"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            while (toggleKeyBinding.wasPressed()) {
                config.enabled = !config.enabled;
                config.save();
                client.player.sendMessage(Text.literal("[Auto Relog] " + (config.enabled ? "§aEnabled" : "§cDisabled")), true);
            }

            while (openGuiKeyBinding.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }

            if (!config.enabled || isRelogging) return;
            if (client.isInSingleplayer()) return;

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastRelogTime) < (config.cooldownSeconds * 1000L)) return;

            if (client.player.getY() <= config.yThreshold) {
                executeRelog(client);
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (config.showHud && client.player != null && !client.options.hudHidden) {
                renderHud(drawContext, client);
            }
        });
    }

    private static void executeRelog(MinecraftClient client) {
        ServerInfo currentServer = client.getCurrentServerEntry();
        if (currentServer == null) return;

        isRelogging = true;
        lastRelogTime = System.currentTimeMillis();

        if (client.world != null) {
            client.world.disconnect();
        }

        client.disconnect();

        ServerAddress address = ServerAddress.parse(currentServer.address);
        ConnectScreen.connect(
                new MultiplayerScreen(new TitleScreen()),
                client,
                address,
                currentServer,
                false,
                null
        );

        isRelogging = false;
    }

    private void renderHud(DrawContext context, MinecraftClient client) {
        String status = "Auto-Relog: " + (config.enabled ? "§aON" : "§cOFF");
        String yLevel = String.format("Y-Level: %.1f (Trigger: %.1f)", client.player.getY(), config.yThreshold);

        context.drawTextWithShadow(client.textRenderer, status, 5, 5, 0xFFFFFF);
        context.drawTextWithShadow(client.textRenderer, yLevel, 5, 15, 0xFFFFFF);
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static void resetRelogState() {
        isRelogging = false;
    }
}
