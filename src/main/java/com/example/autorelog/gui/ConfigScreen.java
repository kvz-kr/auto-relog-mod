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
        context.fill(x, y, x + width, y + 1, color);                  // Top
        context.fill(x, y + height - 1, x + width, y + height, color); // Bottom
        context.fill(x, y, x + 1, y + height, color);                  // Left
        context.fill(x + width - 1, y, x + width, y + height, color);  // Right
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xCC0A0A0C);

        context.fill(0, 0, this.width, 32, 0xFF121216);
        context.fill(0, 31, this.width, 32, 0xFF00E676);
        context.drawTextWithShadow(this.textRenderer, "KRYPTON MENU", 16, 11, 0xFF00E676);
        context.drawTextWithShadow(this.textRenderer, "v1.0.0 | Press ESC to save & exit", this.width - 200, 11, 0xFF888888);

        Module.Category[] categories = Module.Category.values();
        List<AbstractModule> allModules = ModuleManager.INSTANCE.getModules();

        int columnWidth = 115;
        int cardHeight = 22;
        int gapX = 10;
        int startX = 16;

        for (int c = 0; c < categories.length; c++) {
            Module.Category cat = categories[c];
            int currentX = startX + c * (columnWidth + gapX);
            int currentY = 44;

            context.fill(currentX, currentY, currentX + columnWidth, currentY + 18, 0xFF1A1A22);
            drawBorder(context, currentX, currentY, columnWidth, 18, 0xFF2A2A36);
            context.drawCenteredTextWithShadow(this.textRenderer, cat.getDisplayName().toUpperCase(), currentX + (columnWidth / 2), currentY + 5, 0xFF00E676);

            currentY += 22;

            for (AbstractModule mod : allModules) {
                if (mod.getCategory() == cat) {
                    boolean enabled = mod.isEnabled();
                    boolean hovered = mouseX >= currentX && mouseX <= currentX + columnWidth && mouseY >= currentY && mouseY <= currentY + cardHeight;

                    int bgColor = enabled ? (hovered ? 0xFF1B382B : 0xFF142B20) : (hovered ? 0xFF252530 : 0xFF16161E);
                    int borderColor = enabled ? 0xFF00E676 : (hovered ? 0xFF444454 : 0xFF22222C);
                    int textColor = enabled ? 0xFFFFFFFF : (hovered ? 0xFFCCCCCC : 0xFF777788);

                    context.fill(currentX, currentY, currentX + columnWidth, currentY + cardHeight, bgColor);
                    drawBorder(context, currentX, currentY, columnWidth, cardHeight, borderColor);
                    context.drawTextWithShadow(this.textRenderer, mod.getName(), currentX + 6, currentY + 7, textColor);

                    int indicatorColor = enabled ? 0xFF00E676 : 0xFF3A3A4A;
                    context.fill(currentX + columnWidth - 12, currentY + 7, currentX + columnWidth - 5, currentY + 15, indicatorColor);

                    currentY += cardHeight + 3;

                    if (mod == SusChunkFinderModule.INSTANCE && enabled) {
                        currentY = renderSusChunkSubPanel(context, currentX, currentY, columnWidth, mouseX, mouseY);
                    }
                }
            }
        }

        SusChunkFinderModule.INSTANCE.renderChunkHighlights(context);
    }

    private int renderSusChunkSubPanel(DrawContext context, int x, int y, int width, int mouseX, int mouseY) {
        SusChunkFinderModule sus = SusChunkFinderModule.INSTANCE;

        context.fill(x, y, x + width, y + 190, 0xFF101014);
        drawBorder(context, x, y, width, 190, 0xFF00E676);

        int sy = y + 6;

        sy = drawSlider(context, x, sy, width, "SIM DIST", sus.simulationDistance, 1, 12);
        sy = drawSlider(context, x, sy, width, "SENSITIVITY", sus.sensitivity, 1, 20);

        context.drawTextWithShadow(this.textRenderer, "SMART ADJ: " + (sus.smartAdjustment ? "ON" : "OFF"), x + 6, sy, sus.smartAdjustment ? 0xFF00E676 : 0xFF888888); 
        sy += 16;

        sy = drawSlider(context, x, sy, width, "ALPHA", sus.alpha, 5, 100);
        sy += 4;

        sus.kelp = drawCheckbox(context, x + 6, sy, "KELP", sus.kelp); sy += 12;
        sus.caveVines = drawCheckbox(context, x + 6, sy, "CAVE VINES", sus.caveVines); sy += 12;
        sus.vines = drawCheckbox(context, x + 6, sy, "VINES", sus.vines); sy += 12;
        sus.amethyst = drawCheckbox(context, x + 6, sy, "AMETHYST", sus.amethyst); sy += 12;
        sus.bamboo = drawCheckbox(context, x + 6, sy, "BAMBOO", sus.bamboo); sy += 12;
        sus.cocoa = drawCheckbox(context, x + 6, sy, "COCOA", sus.cocoa); sy += 12;
        sus.beeNest = drawCheckbox(context, x + 6, sy, "BEE NEST", sus.beeNest); sy += 12;
        sus.rotatedDeepslate = drawCheckbox(context, x + 6, sy, "ROT DEEPSLATE", sus.rotatedDeepslate);

        return y + 195;
    }

    private int drawSlider(DrawContext context, int x, int y, int width, String label, int value, int min, int max) {
        int sliderX = x + 6;
        int sliderWidth = width - 12;
        int trackY = y + 11;

        context.drawTextWithShadow(this.textRenderer, label + ": " + value, sliderX, y, 0xFF00E676);
        context.fill(sliderX, trackY, sliderX + sliderWidth, trackY + 4, 0xFF22222C);

        double pct = (double) (value - min) / (max - min);
        int fillWidth = (int) (sliderWidth * pct);
        context.fill(sliderX, trackY, sliderX + fillWidth, trackY + 4, 0xFF00E676);
        context.fill(sliderX + fillWidth - 2, trackY - 3, sliderX + fillWidth + 2, trackY + 7, 0xFFFFFFFF);

        return y + 26;
    }

    private boolean drawCheckbox(DrawContext context, int x, int y, String label, boolean checked) {
        int color = checked ? 0xFF00E676 : 0xFF777788;
        context.drawTextWithShadow(this.textRenderer, (checked ? "[X] " : "[ ] ") + label, x, y, color);
        return checked;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            double mouseX = click.x();
            double mouseY = click.y();

            Module.Category[] categories = Module.Category.values();
            List<AbstractModule> allModules = ModuleManager.INSTANCE.getModules();

            int columnWidth = 115;
            int cardHeight = 22;
            int gapX = 10;
            int startX = 16;

            for (int c = 0; c < categories.length; c++) {
                Module.Category cat = categories[c];
                int currentX = startX + c * (columnWidth + gapX);
                int currentY = 66;

                for (AbstractModule mod : allModules) {
                    if (mod.getCategory() == cat) {
                        if (mouseX >= currentX && mouseX <= currentX + columnWidth && mouseY >= currentY && mouseY <= currentY + cardHeight) {
                            mod.toggle();
                            return true;
                        }

                        currentY += cardHeight + 3;

                        if (mod == SusChunkFinderModule.INSTANCE && mod.isEnabled()) {
                            SusChunkFinderModule sus = SusChunkFinderModule.INSTANCE;
                            int subY = currentY + 6;
                            int sliderX = currentX + 6;
                            int sliderWidth = columnWidth - 12;

                            // Slider 1: Simulation Distance
                            if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= subY && mouseY <= subY + 24) {
                                double pct = Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / (double) sliderWidth));
                                sus.simulationDistance = Math.max(1, (int) Math.round(1 + pct * 11));
                                return true;
                            }
                            subY += 26;

                            // Slider 2: Sensitivity
                            if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= subY && mouseY <= subY + 24) {
                                double pct = Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / (double) sliderWidth));
                                sus.sensitivity = Math.max(1, (int) Math.round(1 + pct * 19));
                                return true;
                            }
                            subY += 26;

                            // Smart Adjustment Toggle
                            if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= subY && mouseY <= subY + 14) {
                                sus.smartAdjustment = !sus.smartAdjustment;
                                return true;
                            }
                            subY += 16;

                            // Slider 3: Alpha
                            if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= subY && mouseY <= subY + 24) {
                                double pct = Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / (double) sliderWidth));
                                sus.alpha = Math.max(5, (int) Math.round(5 + pct * 95));
                                return true;
                            }
                            subY += 30;

                            // Checkboxes
                            if (mouseY >= subY && mouseY <= subY + 12) { sus.kelp = !sus.kelp; return true; } subY += 12;
                            if (mouseY >= subY && mouseY <= subY + 12) { sus.caveVines = !sus.caveVines; return true; } subY += 12;
                            if (mouseY >= subY && mouseY <= subY + 12) { sus.vines = !sus.vines; return true; } subY += 12;
                            if (mouseY >= subY && mouseY <= subY + 12) { sus.amethyst = !sus.amethyst; return true; } subY += 12;
                            if (mouseY >= subY && mouseY <= subY + 12) { sus.bamboo = !sus.bamboo; return true; } subY += 12;
                            if (mouseY >= subY && mouseY <= subY + 12) { sus.cocoa = !sus.cocoa; return true; } subY += 12;
                            if (mouseY >= subY && mouseY <= subY + 12) { sus.beeNest = !sus.beeNest; return true; } subY += 12;
                            if (mouseY >= subY && mouseY <= subY + 12) { sus.rotatedDeepslate = !sus.rotatedDeepslate; return true; }

                            currentY += 195;
                        }
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

