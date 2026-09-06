package com.example.autorelog.modules.misc;

import com.example.autorelog.gui.AbstractModule;
import com.example.autorelog.gui.Module;

public class FastPlaceModule extends AbstractModule {
    public FastPlaceModule() {
        super("FAST PLACE", Module.Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            // Reduces placement delay to 0 ticks
            // In Yarn mappings: ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
        }
    }
}
