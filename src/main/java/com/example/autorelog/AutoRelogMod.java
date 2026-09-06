package com.example.autorelog;

import com.example.autorelog.gui.ConfigScreen;
import com.example.autorelog.gui.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AutoRelogMod implements ClientModInitializer {
    public static KeyBinding configKeyBinding;
    private static ServerInfo lastServer;
    private static boolean hasArmed = false;
    private static int reconnectTicksRemaining = 0;
    private static int lookUpTicksRemaining = 0;

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autorelog.open_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyBinding.Category.create(Identifier.of("autorelog", "title"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModuleManager.INSTANCE.onTick();

            if (client.player == null || client.world == null) {
                hasArmed = false;

                if (ModConfig.INSTANCE.autoReconnect 
                    && !ModConfig.INSTANCE.isReconnecting 
                    && client.currentScreen instanceof DisconnectedScreen 
                    && lastServer != null) {
                    
                    if (reconnectTicksRemaining <= 0) {
                        reconnectTicksRemaining = ModConfig.INSTANCE.reconnectDelayTicks; 
                    }

                    if (reconnectTicksRemaining > 0) {
                        reconnectTicksRemaining--;
                        if (reconnectTicksRemaining == 0) {
                            ModConfig.INSTANCE.isReconnecting = true;
                            ModConfig.INSTANCE.shouldChangeSlotOnJoin = true;
                            lookUpTicksRemaining = 10;

                            ServerInfo targetServer = lastServer;
                            client.execute(() -> {
                                ConnectScreen.connect(new TitleScreen(), client, ServerAddress.parse(targetServer.address), targetServer, false, null);
                            });
                        }
                    }
                }
                return;
            }

            reconnectTicksRemaining = 0;
            ModConfig.INSTANCE.isReconnecting = false;

            if (ModConfig.INSTANCE.shouldChangeSlotOnJoin) {
                int targetSlot = ModConfig.INSTANCE.selectedSlot;
                client.player.getInventory().setSelectedSlot(targetSlot);
                
                if (client.getNetworkHandler() != null) {
                    client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(targetSlot));
                }
                ModConfig.INSTANCE.shouldChangeSlotOnJoin = false;
            }

            if (lookUpTicksRemaining > 0) {
                client.player.setPitch(-90.0F);

                if (client.getNetworkHandler() != null) {
                    client.getNetworkHandler().sendPacket(
                        new PlayerMoveC2SPacket.Full(
                            client.player.getX(),
                            client.player.getY(),
                            client.player.getZ(),
                            client.player.getYaw(),
                            -90.0F,
                            client.player.isOnGround(),
                            client.player.horizontalCollision
                        )
                    );
                }
                lookUpTicksRemaining--;
            }

            if (client.getCurrentServerEntry() != null) {
                lastServer = client.getCurrentServerEntry();
            }

            while (configKeyBinding.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }

            if (ModConfig.INSTANCE.enabled) {
                double currentY = client.player.getY();

                if (currentY > (ModConfig.INSTANCE.yThreshold + 2.0)) {
                    hasArmed = true;
                } 
                else if (currentY < ModConfig.INSTANCE.yThreshold && hasArmed) {
                    hasArmed = false;
                    client.player.setPitch(-90.0F);
                    triggerRelog(client);
                }
            }
        });
    }

    private void triggerRelog(net.minecraft.client.MinecraftClient client) {
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().getConnection().disconnect(net.minecraft.text.Text.literal("Auto-Relog Triggered"));
        }
    }
}
