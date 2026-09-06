package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget searchBox;
    private String searchQuery = "";
    
    // Right-Click Context Menu
    private boolean contextMenuOpen = false;
    private int contextMenuX, contextMenuY;
    private String activeContextOption = "";

    public ConfigScreen(Screen parent) {
        super(Text.literal("Client Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Search Bar
        this.searchBox = new TextFieldWidget(this.textRenderer, centerX - 100, centerY - 90, 200, 20, Text.literal("Search..."));
        this.searchBox.setChangedListener(text -> this.searchQuery = text.toLowerCase());
        this.addSelectableChild(this.searchBox);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Theme Background Colors
        int bgColor = 0xDD111111;
        int accentColor = ModConfig.INSTANCE.accentColor;

        if (ModConfig.INSTANCE.theme.equals("STAR")) {
            bgColor = 0xDD0D0814; // Deep Space Purple
        } else if (ModConfig.INSTANCE.theme.equals("ECLIPSE")) {
            bgColor = 0xDD050505; // Pitch Black
        }

        // Draw Main Panel Frame
        context.fill(centerX - 120, centerY - 110, centerX + 120, centerY + 110, bgColor);
        context.fill(centerX - 120, centerY - 110, centerX + 120, centerY - 108, accentColor);

        // Render Search Bar
        this.searchBox.render(context, mouseX, mouseY, delta);

        // Item List Entries
        int currentY = centerY - 60;

        if (matchesSearch("enabled auto-relog")) {
            renderSettingRow(context, "Auto-Relog State", ModConfig.INSTANCE.enabled ? "ENABLED" : "DISABLED", centerX - 100, currentY, mouseX, mouseY);
            currentY += 25;
        }

        if (matchesSearch("delay reconnect speed ticks")) {
            renderSettingRow(context, "Reconnect Delay", ModConfig.INSTANCE.reconnectDelayTicks + " Ticks", centerX - 100, currentY, mouseX, mouseY);
            currentY += 25;
        }

        if (matchesSearch("theme star lunar eclipse")) {
            renderSettingRow(context, "Theme Mode", ModConfig.INSTANCE.theme, centerX - 100, currentY, mouseX, mouseY);
            currentY += 25;
        }

        if (matchesSearch("color accent customization")) {
            renderSettingRow(context, "Accent Color", "HEX: #" + Integer.toHexString(accentColor).toUpperCase(), centerX - 100, currentY, mouseX, mouseY);
        }

        // Draw Right-Click Dropdown Context Menu
        if (contextMenuOpen) {
            renderContextMenu(context, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderSettingRow(DrawContext context, String label, String value, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + 200 && mouseY >= y && mouseY <= y + 20;
        int boxColor = hovered ? 0x44FFFFFF : 0x22FFFFFF;

        context.fill(x, y, x + 200, y + 20, boxColor);
        context.drawTextWithShadow(this.textRenderer, label, x + 8, y + 6, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, value, x + 130, y + 6, ModConfig.INSTANCE.accentColor);
    }

    private void renderContextMenu(DrawContext context, int mouseX, int mouseY) {
        int width = 100;
        int height = 50;

        context.fill(contextMenuX, contextMenuY, contextMenuX + width, contextMenuY + height, 0xFFAAAAAA);
        context.fill(contextMenuX + 1, contextMenuY + 1, contextMenuX + width - 1, contextMenuY + height - 1, 0xFF1E1E1E);

        if (activeContextOption.equals("Theme Mode")) {
            context.drawTextWithShadow(this.textRenderer, "> LUNAR", contextMenuX + 5, contextMenuY + 5, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "> STAR", contextMenuX + 5, contextMenuY + 18, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "> ECLIPSE", contextMenuX + 5, contextMenuY + 31, 0xFFFFFFFF);
        } else if (activeContextOption.equals("Accent Color")) {
            context.drawTextWithShadow(this.textRenderer, "> BLUE", contextMenuX + 5, contextMenuY + 5, 0xFF3B82F6);
            context.drawTextWithShadow(this.textRenderer, "> RED", contextMenuX + 5, contextMenuY + 18, 0xFFEF4444);
            context.drawTextWithShadow(this.textRenderer, "> PURPLE", contextMenuX + 5, contextMenuY + 31, 0xFFA855F7);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Handle Context Menu Selections
        if (contextMenuOpen) {
            if (activeContextOption.equals("Theme Mode")) {
                if (mouseY >= contextMenuY + 5 && mouseY <= contextMenuY + 15) ModConfig.INSTANCE.theme = "LUNAR";
                if (mouseY >= contextMenuY + 18 && mouseY <= contextMenuY + 28) ModConfig.INSTANCE.theme = "STAR";
                if (mouseY >= contextMenuY + 31 && mouseY <= contextMenuY + 41) ModConfig.INSTANCE.theme = "ECLIPSE";
            } else if (activeContextOption.equals("Accent Color")) {
                if (mouseY >= contextMenuY + 5 && mouseY <= contextMenuY + 15) ModConfig.INSTANCE.accentColor = 0xFF3B82F6;
                if (mouseY >= contextMenuY + 18 && mouseY <= contextMenuY + 28) ModConfig.INSTANCE.accentColor = 0xFFEF4444;
                if (mouseY >= contextMenuY + 31 && mouseY <= contextMenuY + 41) ModConfig.INSTANCE.accentColor = 0xFFA855F7;
            }
            ModConfig.save();
            contextMenuOpen = false;
            return true;
        }

        // Left Click Actions
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isHovered(centerX - 100, centerY - 60, mouseX, mouseY)) {
                ModConfig.INSTANCE.enabled = !ModConfig.INSTANCE.enabled;
            } else if (isHovered(centerX - 100, centerY - 35, mouseX, mouseY)) {
                ModConfig.INSTANCE.reconnectDelayTicks = (ModConfig.INSTANCE.reconnectDelayTicks % 20) + 1;
            }
            ModConfig.save();
        }

        // Right Click Actions (Open Dropdown Context Menu)
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (isHovered(centerX - 100, centerY - 10, mouseX, mouseY)) {
                openContextMenu("Theme Mode", (int) mouseX, (int) mouseY);
            } else if (isHovered(centerX - 100, centerY + 15, mouseX, mouseY)) {
                openContextMenu("Accent Color", (int) mouseX, (int) mouseY);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void openContextMenu(String type, int x, int y) {
        this.contextMenuOpen = true;
        this.contextMenuX = x;
        this.contextMenuY = y;
        this.activeContextOption = type;
    }

    private boolean isHovered(int x, int y, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + 200 && mouseY >= y && mouseY <= y + 20;
    }

    private boolean matchesSearch(String keys) {
        return searchQuery.isEmpty() || keys.contains(searchQuery);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
