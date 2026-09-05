package com.example.autorelog.gui;

import com.example.autorelog.AutoRelogMod;
import com.example.autorelog.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;

    private TextFieldWidget yThresholdField;
    private TextFieldWidget cooldownField;
    private boolean enabledState;
    private boolean hudState;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Auto Relog Settings"));
        this.parent = parent;
        this.config = AutoRelogMod.getConfig();
        this.enabledState = config.enabled;
        this.hudState = config.showHud;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Auto Relog: " + (enabledState ? "ON" : "OFF")),
                button -> {
                    enabledState = !enabledState;
                    button.setMessage(Text.literal("Auto Relog: " + (enabledState ? "ON" : "OFF")));
                })
                .dimensions(centerX - 100, startY, 200, 20)
                .build());

        this.yThresholdField = new TextFieldWidget(this.textRenderer, centerX - 100, startY + 30, 200, 20, Text.literal("Y Threshold"));
        this.yThresholdField.setText(String.valueOf(config.yThreshold));
        this.addSelectableChild(this.yThresholdField);

        this.cooldownField = new TextFieldWidget(this.textRenderer, centerX - 100, startY + 60, 200, 20, Text.literal("Cooldown (s)"));
        this.cooldownField.setText(String.valueOf(config.cooldownSeconds));
        this.addSelectableChild(this.cooldownField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Show HUD: " + (hudState ? "ON" : "OFF")),
                button -> {
                    hudState = !hudState;
                    button.setMessage(Text.literal("Show HUD: " + (hudState ? "ON" : "OFF")));
                })
                .dimensions(centerX - 100, startY + 90, 200, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save & Close"),
                button -> saveAndClose())
                .dimensions(centerX - 100, startY + 130, 200, 20)
                .build());
    }

    private void saveAndClose() {
        try {
            config.yThreshold = Double.parseDouble(yThresholdField.getText());
        } catch (NumberFormatException ignored) {}

        try {
            config.cooldownSeconds = Math.max(0, Integer.parseInt(cooldownField.getText()));
        } catch (NumberFormatException ignored) {}

        config.enabled = enabledState;
        config.showHud = hudState;
        config.save();

        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        this.yThresholdField.render(context, mouseX, mouseY, delta);
        this.cooldownField.render(context, mouseX, mouseY, delta);
    }
}
