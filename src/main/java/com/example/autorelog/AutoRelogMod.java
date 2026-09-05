package com.example.autorelog;

import com.example.autorelog.gui.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AutoRelogMod implements ClientModInitializer {
    public static final String MOD_ID = "autorelog";

    public static final KeyBinding.Category AUTORELOG_CATEGORY = 
        KeyBinding.Category.create(Identifier.of(MOD_ID, "category"));

    public static KeyBinding configKeyBinding;
    
    // Safety flag to prevent log-in loops
    private boolean hasArmed = false;

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
                // Reset state on disconnect so you aren't immediately kicked upon joining
                hasArmed = false;
                return;
            }

            while (configKeyBinding.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }

            if (ModConfig.INSTANCE.enabled) {
                double currentY = client.player.getY();

                // Only arm the trigger once you have been safely above the threshold
                if (currentY > ModConfig.INSTANCE.yThreshold) {
                    hasArmed = true;
                } 
                // Only disconnect if the trigger was previously armed
                else if (currentY <= ModConfig.INSTANCE.yThreshold && hasArmed) {
                    hasArmed = false;
                    triggerRelog(client);
                }
            }
        });
    }

    private void triggerRelog(MinecraftClient client) {
        if (client.getNetworkHandler() != null && client.getNetworkHandler().getConnection() != null) {
            client.getNetworkHandler().getConnection().disconnect(Text.literal("Auto-Relog Triggered (Safety Disconnect)"));
        }
    }
}

