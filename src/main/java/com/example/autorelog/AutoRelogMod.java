package com.example.autorelog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AutoRelogMod implements ClientModInitializer {
    public static final String MOD_ID = "autorelog";

    // Use KeyBinding.Category record instead of a raw String
    public static final KeyBinding.Category AUTORELOG_CATEGORY = 
        KeyBinding.Category.create(net.minecraft.util.Identifier.of(MOD_ID, "category"));

    public static KeyBinding configKeyBinding;
    private boolean fellBelowThreshold = false;

    @Override
    public void onInitializeClient() {
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autorelog.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            AUTORELOG_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                fellBelowThreshold = false;
                return;
            }

            // Check keybind pressed to open config
            while (configKeyBinding.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }

            // Check Y-level threshold logic
            if (ModConfig.INSTANCE.enabled) {
                double currentY = client.player.getY();
                if (currentY <= ModConfig.INSTANCE.yThreshold) {
                    if (!fellBelowThreshold) {
                        fellBelowThreshold = true;
                        triggerRelog(client);
                    }
                } else {
                    fellBelowThreshold = false;
                }
            }
        });
    }

    private void triggerRelog(MinecraftClient client) {
        if (client.getNetworkHandler() != null && client.getNetworkHandler().getConnection() != null) {
            client.getNetworkHandler().getConnection().disconnect(Text.literal("Auto-Relog Triggered"));
        }
    }
}
