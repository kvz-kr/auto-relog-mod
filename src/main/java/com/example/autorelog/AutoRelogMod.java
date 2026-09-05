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
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
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
    private int lookUpTicksRemaining = 0;

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
                hasArmed = false;

                if (ModConfig.INSTANCE.autoReconnect 
                    && !ModConfig.INSTANCE.isReconnecting 
                    && client.currentScreen instanceof DisconnectedScreen 
                    && lastServer != null) {
                    
                    ModConfig.INSTANCE.isReconnecting = true;
                    ModConfig.INSTANCE.shouldChangeSlotOnJoin = true;
                    lookUpTicksRemaining = 10;

                    ServerInfo targetServer = lastServer;
                    client.execute(() -> {
                        ConnectScreen.connect(new TitleScreen(), client, ServerAddress.parse(targetServer.address), targetServer, false, null);
                    });
                }
                return;
            }

            ModConfig.INSTANCE.isReconnecting = false;

            // Auto Equip Hotbar Slot & Look Up on Join
            if (ModConfig.INSTANCE.shouldChangeSlotOnJoin) {
                client.player.getInventory().selectedSlot = ModConfig.INSTANCE.selectedSlot;
                if (client.getNetworkHandler() != null) {
                    client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(ModConfig.INSTANCE.selectedSlot));
                }
                ModConfig.INSTANCE.shouldChangeSlotOnJoin = false;
            }

            if (lookUpTicksRemaining > 0) {
                client.player.setPitch(-90.0F);
                client.player.prevPitch = -90.0F;

                if (client.getNetworkHandler() != null) {
                    client.getNetworkHandler().sendPacket(
                        new PlayerMoveC2SPacket.LookAndOnGround(
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

                if (currentY > ModConfig.INSTANCE.yThreshold) {
                    hasArmed = true;
                } 
                else if (currentY <= ModConfig.INSTANCE.yThreshold && hasArmed) {
                    hasArmed = false;
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
