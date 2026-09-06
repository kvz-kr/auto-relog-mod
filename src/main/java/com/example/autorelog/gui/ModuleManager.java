package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<AbstractModule> modules = new ArrayList<>();

    public ModuleManager() {
        registerModules();
    }

    private void registerModules() {
        // Registers custom modules mirroring the Krypton HUD/Utility framework
        modules.add(new AbstractModule("AUTO LOG", Module.Category.MISC) {
            @Override
            public void onTick() {
                ModConfig.INSTANCE.enabled = this.isEnabled();
            }
        });

        modules.add(new AbstractModule("AUTO RECONNECT", Module.Category.MISC) {
            @Override
            public void onTick() {
                ModConfig.INSTANCE.autoReconnect = this.isEnabled();
            }
        });
    }

    public List<AbstractModule> getModules() {
        return modules;
    }

    public void onTick() {
        for (AbstractModule module : modules) {
            module.onTick();
        }
    }
}
