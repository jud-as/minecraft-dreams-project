package com.project.dreams.block.service;
import com.project.dreams.Dreams;
import com.project.dreams.block.domain.model.oil_lamp.block.OilLampBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegisterService {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Dreams.MOD_ID);

    public static final DeferredBlock<Block> OIL_LAMP_BLOCK = BLOCKS.registerBlock(
            "oil_lamp",
            OilLampBlock::new,
            properties ->
            BlockBehaviour.Properties.of()
                    .strength(1.0f)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
    );


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}