package com.project.dreams.item;

import com.project.dreams.Dreams;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
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

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
