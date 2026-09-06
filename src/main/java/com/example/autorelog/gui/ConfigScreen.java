package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Krypton Client"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Header Title
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("KRYPTON MENU"), this.width / 2, 15, 0xFF55FF55);

        Module.Category[] categories = Module.Category.values();
        List<AbstractModule> allModules = ModuleManager.INSTANCE.getModules();

        int columnWidth = 110;
        int cardHeight = 22;
        int gapX = 12;
        int startX = 20;

        for (int c = 0; c < categories.length; c++) {
            Module.Category cat = categories[c];
            int currentX = startX + c * (columnWidth + gapX);
            int currentY = 40;

            // Category Header Box
            context.fill(currentX, currentY, currentX + columnWidth, currentY + 18, 0xFF151515);
            context.drawCenteredTextWithShadow(this.textRenderer, cat.getDisplayName().toUpperCase(), currentX + (columnWidth / 2), currentY + 5, 0xFF00FFCC);

            currentY += 22;

            // Render Modules under this category
            for (AbstractModule mod : allModules) {
                if (mod.getCategory() == cat) {
                    boolean enabled = mod.isEnabled();

                    int bgColor = enabled ? 0xFF1E3A1E : 0xFF1E1E1E;
                    int outlineColor = enabled ? 0xFF55FF55 : 0xFF333333;
                    int textColor = enabled ? 0xFFFFFFFF : 0xFFAAAAAA;

                    // Module Card Box & Outline
                    context.fill(currentX, currentY, currentX + columnWidth, currentY + cardHeight, bgColor);
                    context.drawBorder(currentX, currentY, columnWidth, cardHeight, outlineColor);

                    // Module Label
                    context.drawTextWithShadow(this.textRenderer, mod.getName(), currentX + 8, currentY + 7, textColor);

                    // Indicator Box
                    int indicatorColor = enabled ? 0xFF55FF55 : 0xFF555555;
                    context.fill(currentX + columnWidth - 14, currentY + 7, currentX + columnWidth - 6, currentY + 15, indicatorColor);

                    currentY += cardHeight + 4;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            double mouseX = click.x();
            double mouseY = click.y();

            Module.Category[] categories = Module.Category.values();
            List<AbstractModule> allModules = ModuleManager.INSTANCE.getModules();

            int columnWidth = 110;
            int cardHeight = 22;
            int gapX = 12;
            int startX = 20;

            for (int c = 0; c < categories.length; c++) {
                Module.Category cat = categories[c];
                int currentX = startX + c * (columnWidth + gapX);
                int currentY = 62;

                for (AbstractModule mod : allModules) {
                    if (mod.getCategory() == cat) {
                        if (mouseX >= currentX && mouseX <= currentX + columnWidth &&
                            mouseY >= currentY && mouseY <= currentY + cardHeight) {
                            
                            mod.toggle();
                            return true;
                        }
                        currentY += cardHeight + 4;
                    }
                }
            }
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
