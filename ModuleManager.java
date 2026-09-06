package com.example.autorelog.gui;

import com.example.autorelog.modules.combat.AutoTotemModule;
import com.example.autorelog.modules.misc.SprintModule;
import com.example.autorelog.modules.render.FullbrightModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<AbstractModule> modules = new ArrayList<>();

    public ModuleManager() {
        // Register core working modules
        register(new SprintModule());
        register(new FullbrightModule());
        register(new AutoTotemModule());
    }

    private void register(AbstractModule module) {
        modules.add(module);
    }

    public List<AbstractModule> getModules() {
        return modules;
    }

    public void onTick() {
        for (AbstractModule module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }
}
