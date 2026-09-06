package com.example.autorelog.gui;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractModule {
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    private final String name;
    private final Module.Category category;
    private boolean enabled;

    public AbstractModule(String name, Module.Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onRender() {}

    public String getName() { return name; }
    public Module.Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            toggle();
        }
    }
}
