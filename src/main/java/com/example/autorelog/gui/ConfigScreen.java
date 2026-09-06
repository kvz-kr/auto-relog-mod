package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private static final List<Module> MODULES = Module.getAllModules();
    private TextFieldWidget searchBox;
    private String filter = "";

    public ConfigScreen(Screen parent) {
        super(Text.literal("Client Options"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Search Bar at Top Right
        this.searchBox = new TextFieldWidget(this.textRenderer, this.width - 130, 10, 120, 16, Text.literal("SEARCH"));
        this.searchBox.setChangedListener(text -> this.filter = text.toUpperCase().trim());
        this.addSelectableChild(this.searchBox);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark translucent background overlay
        context.fill(0, 0, this.width, this.height, 0xC0000000);

        // Render Search Widget
        this.searchBox.render(context, mouseX, mouseY, delta);

        // Render Category Columns
        int startX = 15;
        int columnWidth = 110;
        int spacing = 10;

        Module.Category[] categories = Module.Category.values();

        for (int i = 0; i < categories.length; i++) {
            Module.Category cat = categories[i];
            int currentX = startX + i * (columnWidth + spacing);
            
            if (currentX + columnWidth > this.width) break;

            // Header Box
            context.fill(currentX, 10, currentX + columnWidth, 24, 0xEE121216);
            context.drawTextWithShadow(this.textRenderer, cat.name, currentX + 6, 14, 0xFF42A5F5);

            // Module Rows
            int currentY = 28;
            for (Module mod : MODULES) {
                if (mod.category == cat) {
                    if (!filter.isEmpty() && !mod.name.contains(filter)) {
                        continue;
                    }

                    boolean hovered = mouseX >= currentX && mouseX <= currentX + columnWidth && mouseY >= currentY && mouseY <= currentY + 12;

                    // Active blue accent line or gray background
                    int rowColor = hovered ? 0x44FFFFFF : 0x22121216;
                    context.fill(currentX, currentY, currentX + columnWidth, currentY + 11, rowColor);

                    if (mod.enabled) {
                        context.fill(currentX, currentY, currentX + 2, currentY + 11, ModConfig.INSTANCE.accentColor);
                    }

                    // Module Name Text
                    int textColor = mod.enabled ? 0xFFFFFFFF : 0xFF888888;
                    context.drawTextWithShadow(this.textRenderer, mod.name, currentX + 5, currentY + 2, textColor);

                    // Status Indicator Dot
                    int dotColor = mod.enabled ? ModConfig.INSTANCE.accentColor : 0xFF555555;
                    context.fill(currentX + columnWidth - 7, currentY + 4, currentX + columnWidth - 3, currentY + 8, dotColor);

                    currentY += 12;
                }
            }
        }

        // Bottom Navigation Bar
        int bottomY = this.height - 20;
        context.fill(0, bottomY, this.width, this.height, 0xFF0B0B0E);
        context.drawCenteredTextWithShadow(this.textRenderer, "📁 CONFIGS         👤 FRIENDS", this.width / 2, bottomY + 6, 0xFFCCCCCC);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean released) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        int startX = 15;
        int columnWidth = 110;
        int spacing = 10;

        Module.Category[] categories = Module.Category.values();

        for (int i = 0; i < categories.length; i++) {
            Module.Category cat = categories[i];
            int currentX = startX + i * (columnWidth + spacing);
            int currentY = 28;

            for (Module mod : MODULES) {
                if (mod.category == cat) {
                    if (!filter.isEmpty() && !mod.name.contains(filter)) {
                        continue;
                    }

                   // Toggle local GUI state and backend execution module
mod.enabled = !mod.enabled;
for (AbstractModule absMod : ModuleManager.INSTANCE.getModules()) {
    if (absMod.getName().equalsIgnoreCase(mod.name)) {
        absMod.setEnabled(mod.enabled);
        break;
    }
}
                            
                            // Link Auto Relog module to your configuration
                            if (mod.name.equals("AUTO LOG") || mod.name.equals("AUTO RECONNECT")) {
                                ModConfig.INSTANCE.enabled = mod.enabled;
                                ModConfig.save();
                            }
                            return true;
                        }
                    }
                    currentY += 12;
                }
            }
        }

        return super.mouseClicked(click, released);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
