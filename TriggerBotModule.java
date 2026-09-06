package com.example.autorelog.modules.combat;

import com.example.autorelog.gui.AbstractModule;
import com.example.autorelog.gui.Module;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBotModule extends AbstractModule {
    public TriggerBotModule() {
        super("TRIGGER BOT", Module.Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        // Check if player attack cooldown is fully charged (1.0 = 100%)
        if (mc.player.getAttackCooldownProgress(0.5f) < 1.0f) return;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitResult = (EntityHitResult) mc.crosshairTarget;
            mc.interactionManager.attackEntity(mc.player, hitResult.getEntity());
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
