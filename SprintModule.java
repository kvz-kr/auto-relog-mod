package com.example.autorelog.modules.misc;

import com.example.autorelog.gui.AbstractModule;
import com.example.autorelog.gui.Module;

public class SprintModule extends AbstractModule {
    public SprintModule() {
        super("SPRINT", Module.Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.player.forwardSpeed > 0 && !mc.player.isSneaking() && !mc.player.horizontalCollision) {
            mc.player.setSprinting(true);
        }
    }
}
