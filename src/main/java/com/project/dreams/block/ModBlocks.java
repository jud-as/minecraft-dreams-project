package com.project.dreams.block;
import com.project.dreams.Dreams;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Dreams.MOD_ID);



    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}