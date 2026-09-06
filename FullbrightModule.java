package com.example.autorelog.modules.render;

import com.example.autorelog.gui.AbstractModule;
import com.example.autorelog.gui.Module;

public class FullbrightModule extends AbstractModule {
    private double oldGamma = 1.0;

    public FullbrightModule() {
        super("FULLBRIGHT", Module.Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (mc.options != null) {
            oldGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(100.0);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(oldGamma);
        }
    }
}
