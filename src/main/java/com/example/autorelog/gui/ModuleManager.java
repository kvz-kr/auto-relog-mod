package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<AbstractModule> modules = new ArrayList<>();

    public ModuleManager() {
        registerDefaultModules();
    }

    private void registerDefaultModules() {
        // Combat Modules
        modules.add(new AbstractModule("Auto Crystal", Module.Category.COMBAT));
        modules.add(new AbstractModule("Kill Aura", Module.Category.COMBAT));
        modules.add(new AbstractModule("Triggerbot", Module.Category.COMBAT));

        // Movement Modules
        modules.add(new AbstractModule("Flight", Module.Category.MOVEMENT));
        modules.add(new AbstractModule("Sprint", Module.Category.MOVEMENT));
        modules.add(new AbstractModule("NoFall", Module.Category.MOVEMENT));

        // Render Modules
        modules.add(new AbstractModule("ESP", Module.Category.RENDER));
        modules.add(new AbstractModule("Tracers", Module.Category.RENDER));
        modules.add(new AbstractModule("Fullbright", Module.Category.RENDER));

        // Player Modules
        modules.add(new AbstractModule("FastEat", Module.Category.PLAYER));
        modules.add(new AbstractModule("AutoRespawn", Module.Category.PLAYER));

        // Misc Modules (Auto Relog & Auto Reconnect mapped to ModConfig)
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
