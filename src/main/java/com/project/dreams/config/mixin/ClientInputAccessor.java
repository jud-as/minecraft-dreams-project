package com.project.dreams.config.mixin;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientInput.class)
public interface ClientInputAccessor {
    @Accessor("moveVector")
    void dreams$setMoveVector(Vec2 moveVector);

    @Accessor("moveVector")
    Vec2 dreams$getMoveVector();
}
