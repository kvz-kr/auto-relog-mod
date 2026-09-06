package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private static final List<Module> MODULES = Module.getAllModules();
    private String searchFilter = "";

    public ConfigScreen(Screen parent) {
        super(Text.literal("Auto-Relog Configuration"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFFFF);

        Module.Category[] categories = Module.Category.values();
        int startX = 20;
        int startY = 40;
        int cardWidth = 120;
        int cardHeight = 25;
        int gap = 10;

        for (int i = 0; i < categories.length; i++) {
            Module.Category cat = categories[i];
            int currentX = startX + (i % 3) * (cardWidth + gap);
            int currentY = startY + (i / 3) * 160;

            context.drawTextWithShadow(this.textRenderer, cat.getDisplayName(), currentX, currentY, 0xFFFFAA00);
            currentY += 15;

            for (Module mod : MODULES) {
                if (mod.category == cat) {
                    if (!searchFilter.isEmpty() && !mod.name.toLowerCase().contains(searchFilter.toLowerCase())) {
                        continue;
                    }

                    int bgColor = mod.enabled ? 0x8000FF00 : 0x80000000;
                    context.fill(currentX, currentY, currentX + cardWidth, currentY + cardHeight, bgColor);

                    int textColor = mod.enabled ? 0xFFFFFFFF : 0xFF888888;
                    context.drawTextWithShadow(this.textRenderer, mod.name, currentX + 5, currentY + 2, textColor);

                    int dotColor = mod.enabled ? ModConfig.INSTANCE.accentColor : 0xFF555555;
                    context.fill(currentX + cardWidth - 10, currentY + 10, currentX + cardWidth - 4, currentY + 16, dotColor);

                    currentY += cardHeight + 5;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Module.Category[] categories = Module.Category.values();
            int startX = 20;
            int startY = 40;
            int cardWidth = 120;
            int cardHeight = 25;
            int gap = 10;

            for (int i = 0; i < categories.length; i++) {
                Module.Category cat = categories[i];
                int currentX = startX + (i % 3) * (cardWidth + gap);
                int currentY = startY + (i / 3) * 160 + 15;

                for (Module mod : MODULES) {
                    if (mod.category == cat) {
                        if (!searchFilter.isEmpty() && !mod.name.toLowerCase().contains(searchFilter.toLowerCase())) {
                            continue;
                        }

                        if (mouseX >= currentX && mouseX <= currentX + cardWidth &&
                            mouseY >= currentY && mouseY <= currentY + cardHeight) {
                            
                            mod.enabled = !mod.enabled;
                            for (AbstractModule absMod : ModuleManager.INSTANCE.getModules()) {
                                if (absMod.getName().equalsIgnoreCase(mod.name)) {
                                    absMod.setEnabled(mod.enabled);
                                    break;
                                }
                            }

                            if (mod.name.equals("AUTO LOG") || mod.name.equals("AUTO RECONNECT")) {
                                ModConfig.INSTANCE.enabled = mod.enabled;
                            }
                            return true;
                        }
                        currentY += cardHeight + 5;
                    }
                }
            }
        }
        return false;
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
