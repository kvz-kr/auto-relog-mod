package com.example.autorelog.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Krypton Client"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xCC000000);

        int panelX = 30;
        int panelY = 30;
        int width = 220;

        SusChunkFinderModule mod = SusChunkFinderModule.INSTANCE;

        // Title Header
        context.drawTextWithShadow(this.textRenderer, "SUS CHUNK FINDER", panelX, panelY, 0xFF55FFFF);
        
        // Main Toggle
        int toggleColor = mod.isEnabled() ? 0xFF55FF55 : 0xFF555555;
        context.fill(panelX + width - 20, panelY, panelX + width, panelY + 10, toggleColor);

        int y = panelY + 20;

        // Simulation Distance
        context.drawTextWithShadow(this.textRenderer, "SIMULATION DISTANCE   " + mod.simulationDistance, panelX, y, 0xFFFFFFFF);
        y += 18;

        // Sensitivity
        context.drawTextWithShadow(this.textRenderer, "SENSITIVITY           " + mod.sensitivity, panelX, y, 0xFFFFFFFF);
        y += 18;

        // Smart Adjustment Toggle
        context.drawTextWithShadow(this.textRenderer, "[ " + (mod.smartAdjustment ? "X" : " ") + " ] SMART ADJUSTMENT", panelX, y, 0xFFCCCCCC);
        y += 18;

        // Alpha
        context.drawTextWithShadow(this.textRenderer, "ALPHA                 " + mod.alpha, panelX, y, 0xFFFFFFFF);
        y += 22;

        // Block Toggles
        mod.kelp = renderCheckbox(context, panelX, y, "KELP", mod.kelp, mouseX, mouseY); y += 16;
        mod.caveVines = renderCheckbox(context, panelX, y, "CAVE VINES", mod.caveVines, mouseX, mouseY); y += 16;
        mod.vines = renderCheckbox(context, panelX, y, "VINES", mod.vines, mouseX, mouseY); y += 16;
        mod.amethyst = renderCheckbox(context, panelX, y, "AMETHYST", mod.amethyst, mouseX, mouseY); y += 16;
        mod.bamboo = renderCheckbox(context, panelX, y, "BAMBOO", mod.bamboo, mouseX, mouseY); y += 16;
        mod.cocoa = renderCheckbox(context, panelX, y, "COCOA", mod.cocoa, mouseX, mouseY); y += 16;
        mod.beeNest = renderCheckbox(context, panelX, y, "BEE NEST", mod.beeNest, mouseX, mouseY); y += 16;
        mod.rotatedDeepslate = renderCheckbox(context, panelX, y, "ROTATED DEEPSLATE", mod.rotatedDeepslate, mouseX, mouseY);
    }

    private boolean renderCheckbox(DrawContext context, int x, int y, String label, boolean val, int mouseX, int mouseY) {
        int color = val ? 0xFF55FFFF : 0xFF888888;
        context.drawTextWithShadow(this.textRenderer, (val ? "[X] " : "[ ] ") + label, x, y, color);
        return val;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            double mouseX = click.x();
            double mouseY = click.y();
            SusChunkFinderModule mod = SusChunkFinderModule.INSTANCE;

            int panelX = 30;
            int y = 142; // Start of checkbox array

            // Checkbox click bounds handler
            if (mouseY >= y && mouseY <= y + 16) mod.kelp = !mod.kelp; y += 16;
            if (mouseY >= y && mouseY <= y + 16) mod.caveVines = !mod.caveVines; y += 16;
            if (mouseY >= y && mouseY <= y + 16) mod.vines = !mod.vines; y += 16;
            if (mouseY >= y && mouseY <= y + 16) mod.amethyst = !mod.amethyst; y += 16;
            if (mouseY >= y && mouseY <= y + 16) mod.bamboo = !mod.bamboo; y += 16;
            if (mouseY >= y && mouseY <= y + 16) mod.cocoa = !mod.cocoa; y += 16;
            if (mouseY >= y && mouseY <= y + 16) mod.beeNest = !mod.beeNest; y += 16;
            if (mouseY >= y && mouseY <= y + 16) mod.rotatedDeepslate = !mod.rotatedDeepslate;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
