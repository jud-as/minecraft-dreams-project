package com.project.dreams.block.service;

import com.project.dreams.Dreams;
import com.project.dreams.block.domain.model.oil_lamp.block_entity.OilLampBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockEntityService {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Dreams.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OilLampBlockEntity>> OIL_LAMP =
            BLOCK_ENTITIES.register("oil_lamp", () ->
                    new BlockEntityType<>(OilLampBlockEntity::new, BlockRegisterService.OIL_LAMP_BLOCK.get()));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
