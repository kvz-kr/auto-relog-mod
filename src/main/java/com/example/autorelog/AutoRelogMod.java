package com.example.autorelog;

import com.example.autorelog.gui.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
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
    
    private boolean hasArmed = false;
    private ServerInfo lastServer = null;

    @Override
    public void onInitializeClient() {
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autorelog.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            AUTORELOG_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Handle Disconnected State
            if (client.player == null || client.world == null) {
                hasArmed = false;

                // Fire auto-reconnect ONLY ONCE per disconnect event
                if (ModConfig.INSTANCE.autoReconnect 
                    && !ModConfig.INSTANCE.isReconnecting 
                    && client.currentScreen instanceof DisconnectedScreen 
                    && lastServer != null) {
                    
                    ModConfig.INSTANCE.isReconnecting = true;
                    ModConfig.INSTANCE.shouldLookUpOnJoin = true;

                    ServerInfo targetServer = lastServer;
                    client.execute(() -> {
                        ConnectScreen.connect(new TitleScreen(), client, ServerAddress.parse(targetServer.address), targetServer, false, null);
                    });
                }
                return;
            }

            // Player is in-game: reset reconnecting state
            ModConfig.INSTANCE.isReconnecting = false;

            // Apply auto-look up if returning from a relog
            if (ModConfig.INSTANCE.shouldLookUpOnJoin) {
                client.player.setPitch(-90.0F);
                ModConfig.INSTANCE.shouldLookUpOnJoin = false;
            }

            // Save active server details
            if (client.getCurrentServerEntry() != null) {
                lastServer = client.getCurrentServerEntry();
            }

            // Open config menu via keybind
            while (configKeyBinding.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }

            // Threshold logic
            if (ModConfig.INSTANCE.enabled) {
                double currentY = client.player.getY();

                // Arm trigger when safely above threshold
                if (currentY > ModConfig.INSTANCE.yThreshold) {
                    hasArmed = true;
                } 
                // Disconnect when falling below threshold
                else if (currentY <= ModConfig.INSTANCE.yThreshold && hasArmed) {
                    hasArmed = false;

                    // Instantly snap player pitch straight up
                    client.player.setPitch(-90.0F);

                    triggerRelog(client);
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
