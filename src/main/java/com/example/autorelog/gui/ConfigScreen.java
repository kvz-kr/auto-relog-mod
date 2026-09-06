package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Client Configuration"));
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
            .dimensions(centerX - 100, centerY - 60, 200, 20)
            .build()
        );

        // Reconnect Speed / Delay
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Reconnect Delay: " + ModConfig.INSTANCE.reconnectDelayTicks + " Ticks (" + (ModConfig.INSTANCE.reconnectDelayTicks * 50) + "ms)"),
            button -> {
                ModConfig.INSTANCE.reconnectDelayTicks = (ModConfig.INSTANCE.reconnectDelayTicks % 20) + 1;
                button.setMessage(Text.literal("Reconnect Delay: " + ModConfig.INSTANCE.reconnectDelayTicks + " Ticks (" + (ModConfig.INSTANCE.reconnectDelayTicks * 50) + "ms)"));
                ModConfig.save();
            })
            .dimensions(centerX - 100, centerY - 35, 200, 20)
            .build()
        );

        // Cycle Themes (LUNAR -> STAR -> ECLIPSE)
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Theme Mode: " + ModConfig.INSTANCE.theme),
            button -> {
                if (ModConfig.INSTANCE.theme.equals("LUNAR")) ModConfig.INSTANCE.theme = "STAR";
                else if (ModConfig.INSTANCE.theme.equals("STAR")) ModConfig.INSTANCE.theme = "ECLIPSE";
                else ModConfig.INSTANCE.theme = "LUNAR";
                
                button.setMessage(Text.literal("Theme Mode: " + ModConfig.INSTANCE.theme));
                ModConfig.save();
            })
            .dimensions(centerX - 100, centerY - 10, 200, 20)
            .build()
        );

        // Cycle Accent Colors (Blue -> Red -> Purple)
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Accent Color: Change"),
            button -> {
                if (ModConfig.INSTANCE.accentColor == 0xFF3B82F6) ModConfig.INSTANCE.accentColor = 0xFFEF4444; // Red
                else if (ModConfig.INSTANCE.accentColor == 0xFFEF4444) ModConfig.INSTANCE.accentColor = 0xFFA855F7; // Purple
                else ModConfig.INSTANCE.accentColor = 0xFF3B82F6; // Blue
                
                ModConfig.save();
            })
            .dimensions(centerX - 100, centerY + 15, 200, 20)
            .build()
        );

        // Done / Close
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            button -> {
                ModConfig.save();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            })
            .dimensions(centerX - 100, centerY + 50, 200, 20)
            .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Select Theme Background Color
        int bgColor = 0xDD111111; // Default Dark
        if ("STAR".equals(ModConfig.INSTANCE.theme)) {
            bgColor = 0xDD0D0814; // Cosmic Purple
        } else if ("ECLIPSE".equals(ModConfig.INSTANCE.theme)) {
            bgColor = 0xDD050505; // OLED Black
        }

        // Draw Frame Panel & Accent Border
        context.fill(centerX - 110, centerY - 80, centerX + 110, centerY + 85, bgColor);
        context.fill(centerX - 110, centerY - 80, centerX + 110, centerY - 78, ModConfig.INSTANCE.accentColor);

        // Title Header
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, centerY - 73, 0xFFFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
