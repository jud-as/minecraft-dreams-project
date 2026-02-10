package com.project.dreams.item.domain.model.key.red_key;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.project.dreams.item.service.ItemRegisterService.ITEMS;

public class RedKeyItem extends Item {

    public static final String ID = "red_key";
    public static final String NAME = "Red Key";

    public RedKeyItem(Properties properties) {
        super(properties);
    }

    public static final DeferredItem<Item> RED_KEY = ITEMS.registerItem(
            "red_key",
            Item::new
    );
}
