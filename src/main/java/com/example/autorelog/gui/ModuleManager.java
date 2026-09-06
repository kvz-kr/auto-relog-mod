package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<AbstractModule> modules = new ArrayList<>();

    public ModuleManager() {
        registerModules();
    }

    private void registerModules() {
        class BaseModule extends AbstractModule {
            public BaseModule(String name, Module.Category category) {
                super(name, category);
            }
        }

        // Combat
        modules.add(new BaseModule("Triggerbot", Module.Category.COMBAT) {
            @Override
            public void onTick() {
                if (!isEnabled()) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.crosshairTarget != null && client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
                    if (client.player != null && client.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                        if (client.interactionManager != null && client.targetedEntity != null) {
                            client.interactionManager.attackEntity(client.player, client.targetedEntity);
                            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                        }
                    }
                }
            }
        });

        // Movement
        modules.add(new BaseModule("Sprint", Module.Category.MOVEMENT) {
            @Override
            public void onTick() {
                if (!isEnabled()) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null && client.options.forwardKey.isPressed()) {
                    client.player.setSprinting(true);
                }
            }
        });

        modules.add(new BaseModule("NoFall", Module.Category.MOVEMENT) {
            @Override
            public void onTick() {
                if (!isEnabled()) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null && client.player.fallDistance > 2.5f) {
                    if (client.getNetworkHandler() != null) {
                        client.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly(true, client.player.horizontalCollision));
                    }
                }
            }
        });

        // Render
        modules.add(new BaseModule("Fullbright", Module.Category.RENDER) {
            @Override
            public void onTick() {
                if (!isEnabled()) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.NIGHT_VISION, 220, 0, false, false, false
                    ));
                }
            }
        });

        // Misc / Relog
        modules.add(new BaseModule("AUTO LOG", Module.Category.MISC) {
            @Override
            public void onTick() {
                ModConfig.INSTANCE.enabled = this.isEnabled();
            }
        });

        modules.add(new BaseModule("AUTO RECONNECT", Module.Category.MISC) {
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
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }
}
