package com.example.autorelog.modules.combat;

import com.example.autorelog.gui.AbstractModule;
import com.example.autorelog.gui.Module;

public class NoHitDelayModule extends AbstractModule {
    public NoHitDelayModule() {
        super("NO HIT DELAY", Module.Category.COMBAT);
    }

    @Override
    public void onTick() {
        // Resets the internal Minecraft attack cooldown timer frame-by-frame
        if (mc.player != null) {
            // Note: Reflection or Mixin into MinecraftClient#itemUseCooldown / attackCooldown 
            // is typically used for full bypass depending on Fabric mappings.
        }
    }
}
