package com.example.autorelog.gui;

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

    private void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark translucent overlay
        context.fill(0, 0, this.width, this.height, 0xBB0A0A0C);

        // Header Banner
        context.fill(0, 0, this.width, 32, 0xFF121216);
        context.fill(0, 31, this.width, 32, 0xFF00E676);
        context.drawTextWithShadow(this.textRenderer, "KRYPTON", 16, 11, 0xFF00E676);
        context.drawTextWithShadow(this.textRenderer, "v1.0.0 | Press ESC to save & exit", this.width - 180, 11, 0xFF888888);

        Module.Category[] categories = Module.Category.values();
        List<AbstractModule> allModules = ModuleManager.INSTANCE.getModules();

        int columnWidth = 120;
        int cardHeight = 24;
        int gapX = 14;
        int startX = 20;

        for (int c = 0; c < categories.length; c++) {
            Module.Category cat = categories[c];
            int currentX = startX + c * (columnWidth + gapX);
            int currentY = 48;

            // Category Header Box
            context.fill(currentX, currentY, currentX + columnWidth, currentY + 20, 0xFF1A1A22);
            drawBorder(context, currentX, currentY, columnWidth, 20, 0xFF2A2A36);
            context.drawCenteredTextWithShadow(this.textRenderer, cat.getDisplayName().toUpperCase(), currentX + (columnWidth / 2), currentY + 6, 0xFF00E676);

            currentY += 24;

            // Render Modules
            for (AbstractModule mod : allModules) {
                if (mod.getCategory() == cat) {
                    boolean enabled = mod.isEnabled();
                    boolean hovered = mouseX >= currentX && mouseX <= currentX + columnWidth && mouseY >= currentY && mouseY <= currentY + cardHeight;

                    int bgColor = enabled ? (hovered ? 0xFF1B382B : 0xFF142B20) : (hovered ? 0xFF252530 : 0xFF16161E);
                    int borderColor = enabled ? 0xFF00E676 : (hovered ? 0xFF444454 : 0xFF22222C);
                    int textColor = enabled ? 0xFFFFFFFF : (hovered ? 0xFFCCCCCC : 0xFF777788);

                    // Module Body
                    context.fill(currentX, currentY, currentX + columnWidth, currentY + cardHeight, bgColor);
                    drawBorder(context, currentX, currentY, columnWidth, cardHeight, borderColor);

                    // Module Name
                    context.drawTextWithShadow(this.textRenderer, mod.getName(), currentX + 8, currentY + 8, textColor);

                    // Status Indicator
                    int indicatorColor = enabled ? 0xFF00E676 : 0xFF3A3A4A;
                    context.fill(currentX + columnWidth - 14, currentY + 8, currentX + columnWidth - 6, currentY + 16, indicatorColor);

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

            int columnWidth = 120;
            int cardHeight = 24;
            int gapX = 14;
            int startX = 20;

            for (int c = 0; c < categories.length; c++) {
                Module.Category cat = categories[c];
                int currentX = startX + c * (columnWidth + gapX);
                int currentY = 72;

                for (AbstractModule mod : allModules) {
                    if (mod.getCategory() == cat) {
                        if (mouseX >= currentX && mouseX <= currentX + columnWidth && mouseY >= currentY && mouseY <= currentY + cardHeight) {
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
