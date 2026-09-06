package com.example.autorelog.gui;

public abstract class AbstractModule {
    private final String name;
    private final Module.Category category;
    private boolean enabled;

    public AbstractModule(String name, Module.Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public Module.Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public void onTick() {}
}
