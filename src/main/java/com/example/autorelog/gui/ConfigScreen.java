package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Auto-Relog Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Enabled: " + ModConfig.INSTANCE.enabled),
            button -> {
                ModConfig.INSTANCE.enabled = !ModConfig.INSTANCE.enabled;
                button.setMessage(Text.literal("Enabled: " + ModConfig.INSTANCE.enabled));
            })
            .dimensions(this.width / 2 - 100, this.height / 2 - 45, 200, 20)
            .build()
        );

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Auto Equip Slot: " + (ModConfig.INSTANCE.selectedSlot + 1)),
            button -> {
                ModConfig.INSTANCE.selectedSlot = (ModConfig.INSTANCE.selectedSlot + 1) % 9;
                button.setMessage(Text.literal("Auto Equip Slot: " + (ModConfig.INSTANCE.selectedSlot + 1)));
            })
            .dimensions(this.width / 2 - 100, this.height / 2 - 15, 200, 20)
            .build()
        );

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            button -> this.client.setScreen(this.parent))
            .dimensions(this.width / 2 - 100, this.height / 2 + 25, 200, 20)
            .build()
        );
    }
}
