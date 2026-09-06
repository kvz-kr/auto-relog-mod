package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Auto-Relog Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int startY = this.height / 2 - 60;

        // Toggle Enabled
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Enabled: " + ModConfig.INSTANCE.enabled),
            button -> {
                ModConfig.INSTANCE.enabled = !ModConfig.INSTANCE.enabled;
                button.setMessage(Text.literal("Enabled: " + ModConfig.INSTANCE.enabled));
                ModConfig.save();
            })
            .dimensions(this.width / 2 - 100, startY, 200, 20)
            .build()
        );

        // Auto Equip Slot Button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Auto Equip Slot: " + (ModConfig.INSTANCE.selectedSlot + 1)),
            button -> {
                ModConfig.INSTANCE.selectedSlot = (ModConfig.INSTANCE.selectedSlot + 1) % 9;
                button.setMessage(Text.literal("Auto Equip Slot: " + (ModConfig.INSTANCE.selectedSlot + 1)));
                ModConfig.save();
            })
            .dimensions(this.width / 2 - 100, startY + 25, 200, 20)
            .build()
        );

        // Reconnect Speed / Delay Selector (1 - 20 Ticks)
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Reconnect Delay: " + ModConfig.INSTANCE.reconnectDelayTicks + " Ticks (" + (ModConfig.INSTANCE.reconnectDelayTicks * 50) + "ms)"),
            button -> {
                ModConfig.INSTANCE.reconnectDelayTicks = (ModConfig.INSTANCE.reconnectDelayTicks % 20) + 1;
                button.setMessage(Text.literal("Reconnect Delay: " + ModConfig.INSTANCE.reconnectDelayTicks + " Ticks (" + (ModConfig.INSTANCE.reconnectDelayTicks * 50) + "ms)"));
                ModConfig.save();
            })
            .dimensions(this.width / 2 - 100, startY + 50, 200, 20)
            .build()
        );

        // Done Button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            button -> {
                ModConfig.save();
                this.client.setScreen(this.parent);
            })
            .dimensions(this.width / 2 - 100, startY + 85, 200, 20)
            .build()
        );
    }
}
