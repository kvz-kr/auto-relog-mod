package com.example.autorelog.modules.render;

import com.example.autorelog.gui.AbstractModule;
import com.example.autorelog.gui.Module;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerESPModule extends AbstractModule {
    public PlayerESPModule() {
        super("PLAYER ESP", Module.Category.RENDER);
    }

    @Override
    public void onRender() {
        if (mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != mc.player && player.isAlive()) {
                // Enable glowing effect client-side for active targets
                player.setGlowing(this.isEnabled());
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.world == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            player.setGlowing(false);
        }
    }
}
