package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Auto-Relog Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Toggle Mod State
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Auto-Relog: " + (ModConfig.INSTANCE.enabled ? "ENABLED" : "DISABLED")),
            button -> {
                ModConfig.INSTANCE.enabled = !ModConfig.INSTANCE.enabled;
                button.setMessage(Text.literal("Auto-Relog: " + (ModConfig.INSTANCE.enabled ? "ENABLED" : "DISABLED")));
                ModConfig.save();
            })
            .dimensions(centerX - 100, centerY - 50, 200, 20)
            .build()
        );

        // Reconnect Delay
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Reconnect Delay: " + ModConfig.INSTANCE.reconnectDelayTicks + " Ticks"),
            button -> {
                ModConfig.INSTANCE.reconnectDelayTicks = (ModConfig.INSTANCE.reconnectDelayTicks % 20) + 1;
                button.setMessage(Text.literal("Reconnect Delay: " + ModConfig.INSTANCE.reconnectDelayTicks + " Ticks"));
                ModConfig.save();
            })
            .dimensions(centerX - 100, centerY - 25, 200, 20)
            .build()
        );

        // Cycle Theme Mode (LUNAR -> STAR -> ECLIPSE)
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Theme: " + ModConfig.INSTANCE.theme),
            button -> {
                if ("LUNAR".equals(ModConfig.INSTANCE.theme)) ModConfig.INSTANCE.theme = "STAR";
                else if ("STAR".equals(ModConfig.INSTANCE.theme)) ModConfig.INSTANCE.theme = "ECLIPSE";
                else ModConfig.INSTANCE.theme = "LUNAR";

                button.setMessage(Text.literal("Theme: " + ModConfig.INSTANCE.theme));
                ModConfig.save();
            })
            .dimensions(centerX - 100, centerY, 200, 20)
            .build()
        );

        // Cycle Accent Color
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Accent Color: Switch"),
            button -> {
                if (ModConfig.INSTANCE.accentColor == 0xFF3B82F6) ModConfig.INSTANCE.accentColor = 0xFFEF4444; // Red
                else if (ModConfig.INSTANCE.accentColor == 0xFFEF4444) ModConfig.INSTANCE.accentColor = 0xFFA855F7; // Purple
                else ModConfig.INSTANCE.accentColor = 0xFF3B82F6; // Blue

                ModConfig.save();
            })
            .dimensions(centerX - 100, centerY + 25, 200, 20)
            .build()
        );

        // Close Screen
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            button -> {
                ModConfig.save();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            })
            .dimensions(centerX - 100, centerY + 60, 200, 20)
            .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw dark background tint directly to prevent renderBackground crashes
        context.fill(0, 0, this.width, this.height, 0x88000000);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Theme Background
        int panelColor = 0xEE111111;
        if ("STAR".equals(ModConfig.INSTANCE.theme)) {
            panelColor = 0xEE0D0814;
        } else if ("ECLIPSE".equals(ModConfig.INSTANCE.theme)) {
            panelColor = 0xEE050505;
        }

        // Main Panel Box
        context.fill(centerX - 110, centerY - 70, centerX + 110, centerY + 90, panelColor);

        // Accent Top Border
        context.fill(centerX - 110, centerY - 70, centerX + 110, centerY - 67, ModConfig.INSTANCE.accentColor);

        // Title Header
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, centerY - 62, 0xFFFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
