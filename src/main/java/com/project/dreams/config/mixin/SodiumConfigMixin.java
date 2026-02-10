package com.project.dreams.config.mixin;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.config.builder.StaticOptionBuilderImpl", remap = false)
public abstract class SodiumConfigMixin {
    @Shadow private Component tooltip;

    @Inject(method = "validateData", at = @At("HEAD"))
    private void dreams$fixBlankTooltip(CallbackInfo ci) {
        if (this.tooltip == null || this.tooltip.getString().isBlank()) {
            this.tooltip = Component.translatable("options.iris.shaderPackSelection.sodium_tooltip");
        }
    }
}
