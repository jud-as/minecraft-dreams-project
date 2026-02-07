package com.project.dreams.config.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.irisshaders.iris.compat.sodium.config.IrisConfig", remap = false)
public class IrisConfigMixin {
    @Redirect(method = "registerConfigLate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;empty()Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent dreams$provideTooltip() {
        return Component.translatable("options.iris.shaderPackSelection.sodium_tooltip");
    }
}
