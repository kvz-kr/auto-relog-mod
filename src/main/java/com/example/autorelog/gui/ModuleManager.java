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
        // Concrete module implementation class for generic modules
        class GenericModule extends AbstractModule {
            public GenericModule(String name, Module.Category category) {
                super(name, category);
            }
        }

        // Combat Modules
        modules.add(new GenericModule("Auto Crystal", Module.Category.COMBAT));
        modules.add(new GenericModule("Kill Aura", Module.Category.COMBAT));
        modules.add(new GenericModule("Triggerbot", Module.Category.COMBAT));

        // Movement Modules
        modules.add(new GenericModule("Flight", Module.Category.MOVEMENT));
        modules.add(new GenericModule("Sprint", Module.Category.MOVEMENT));
        modules.add(new GenericModule("NoFall", Module.Category.MOVEMENT));

        // Render Modules
        modules.add(new GenericModule("ESP", Module.Category.RENDER));
        modules.add(new GenericModule("Tracers", Module.Category.RENDER));
        modules.add(new GenericModule("Fullbright", Module.Category.RENDER));

        // Player Modules
        modules.add(new GenericModule("FastEat", Module.Category.PLAYER));
        modules.add(new GenericModule("AutoRespawn", Module.Category.PLAYER));

        // Misc Modules (Auto Relog & Auto Reconnect mapped to ModConfig)
        modules.add(new GenericModule("AUTO LOG", Module.Category.MISC) {
            @Override
            public void onTick() {
                ModConfig.INSTANCE.enabled = this.isEnabled();
            }
        });

        modules.add(new GenericModule("AUTO RECONNECT", Module.Category.MISC) {
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
