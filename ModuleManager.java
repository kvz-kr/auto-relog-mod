package com.example.autorelog.gui;

import com.example.autorelog.modules.combat.AutoTotemModule;
import com.example.autorelog.modules.combat.NoHitDelayModule;
import com.example.autorelog.modules.combat.TriggerBotModule;
import com.example.autorelog.modules.misc.FastPlaceModule;
import com.example.autorelog.modules.misc.SprintModule;
import com.example.autorelog.modules.render.FullbrightModule;
import com.example.autorelog.modules.render.PlayerESPModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<AbstractModule> modules = new ArrayList<>();

    public ModuleManager() {
        // Combat
        register(new AutoTotemModule());
        register(new TriggerBotModule());
        register(new NoHitDelayModule());

        // Misc
        register(new SprintModule());
        register(new FastPlaceModule());

        // Render
        register(new FullbrightModule());
        register(new PlayerESPModule());
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
