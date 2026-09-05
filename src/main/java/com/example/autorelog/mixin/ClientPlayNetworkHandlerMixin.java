package com.example.autorelog.mixin;

import com.example.autorelog.AutoRelogMod;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void onDisconnectedInject(Text reason, CallbackInfo ci) {
        AutoRelogMod.resetRelogState();
    }
}
