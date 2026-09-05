package com.example.autorelog.mixin;

import com.example.autorelog.ModConfig;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void onDisconnected(Text reason, CallbackInfo ci) {
        if (ModConfig.INSTANCE.reconnecting) {
            // Handled disconnect logic
            ModConfig.INSTANCE.reconnecting = false;
        }
    }
}
