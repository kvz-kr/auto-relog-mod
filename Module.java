package com.example.autorelog.gui;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public String name;
    public Category category;
    public boolean enabled;

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        PLAYER("Player"),
        MISC("Misc");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public static List<Module> getAllModules() {
        List<Module> list = new ArrayList<>();
        for (AbstractModule absMod : ModuleManager.INSTANCE.getModules()) {
            Module m = new Module(absMod.getName(), absMod.getCategory());
            m.enabled = absMod.isEnabled();
            list.add(m);
        }
        return list;
    }
}
