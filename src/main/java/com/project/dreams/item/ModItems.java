package com.project.dreams.item;

import com.project.dreams.Dreams;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Dreams.MOD_ID);

    public static final DeferredItem<Item> WILL = ITEMS.registerItem(
            "will",
            Item::new,
            new Item.Properties()
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
