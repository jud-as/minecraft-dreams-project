package com.project.dreams.item.service;

import com.project.dreams.Dreams;
import com.project.dreams.item.domain.model.oil_lamp.OilLampItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegisterService {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Dreams.MOD_ID);

    public static final DeferredItem<Item> WILL = ITEMS.registerSimpleItem(
            "will",
            p -> p.food(new FoodProperties.Builder()
                    .alwaysEdible()
                    .nutrition(1)
                    .saturationModifier(2f)
                    .build())
    );

    public static final DeferredItem<OilLampItem> OIL_LAMP = ITEMS.registerItem(
            "oil_lamp",
            OilLampItem::new
    );

    public static final DeferredItem<Item> OIL_BOTTLE = ITEMS.registerSimpleItem("oil_bottle");
    public static final DeferredItem<Item> RED_KEY = ITEMS.registerSimpleItem("red_key");


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
