package com.example.autorelog.gui;

import com.example.autorelog.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<AbstractModule> modules = new ArrayList<>();

    public ModuleManager() {
        registerModules();
    }

    private void registerModules() {
        class GenericModule extends AbstractModule {
            public GenericModule(String name, Module.Category category) {
                super(name, category);
            }
        }

        // --- COMBAT MODULES ---
        modules.add(new GenericModule("Triggerbot", Module.Category.COMBAT) {
            @Override
            public void onTick() {
                if (!isEnabled()) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                    if (client.player != null && client.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                        if (client.interactionManager != null && client.targetedEntity != null) {
                            client.interactionManager.attackEntity(client.player, client.targetedEntity);
                            client.player.swingHand(Hand.MAIN_HAND);
                        }
                    }
                }
            }
        });

        // --- MOVEMENT MODULES ---
        modules.add(new GenericModule("Sprint", Module.Category.MOVEMENT) {
            @Override
            public void onTick() {
                if (!isEnabled()) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null && client.options.forwardKey.isPressed()) {
                    client.player.setSprinting(true);
                }
            }
        });

        modules.add(new GenericModule("NoFall", Module.Category.MOVEMENT) {
            @Override
            public void onTick() {
                if (!isEnabled()) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null && client.player.fallDistance > 2.5f) {
                    if (client.getNetworkHandler() != null) {
                        client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, client.player.horizontalCollision));
                    }
                }
            }
        });

        // --- RENDER MODULES ---
        modules.add(SusChunkFinderModule.INSTANCE);

        modules.add(new GenericModule("Fullbright", Module.Category.RENDER) {
            @Override
            public void onTick() {
                if (!isEnabled()) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION, 220, 0, false, false, false
                    ));
                }
            }
        });

        // --- MISC / RELOG MODULES ---
        modules.add(new GenericModule("AUTO LOG", Module.Category.MISC) {
            @Override
            public void onTick() {
                ModConfig.INSTANCE.enabled = this.isEnabled();
            }
        });

        modules.add(new GenericModule("AUTO RECONNECT", Module.Category.MISC) {
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
