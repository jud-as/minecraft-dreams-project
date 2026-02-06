package com.project.dreams.item.domain.model.oil_lamp;

import com.mojang.serialization.Codec;
import com.project.dreams.Dreams;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

/**
 * Registrador de componentes de dados específicos para o item OilLampItem.
 * Segue o padrão orientado a objetos para encapsular metadados do item.
 */
public class OilLampDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Dreams.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_ON =
            register("is_on", builder -> builder.persistent(Codec.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FUEL =
            register("fuel", builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TICK_ACCUM =
            register("tick_accum", builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<net.minecraft.core.BlockPos>> LIGHT_POS =
            register("light_pos", builder -> builder.persistent(net.minecraft.core.BlockPos.CODEC));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENT_TYPES.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
