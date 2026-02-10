package com.project.dreams.block.domain.model.red_door.block;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

public class RedDoorBlock extends BlockItem {


    public static final String ID = "red_door";
    public static final String NAME = "Red Door";

    public RedDoorBlock(Block block, Properties properties) {
        super(block, properties);
    }

    public @NonNull InteractionResult unlock (@NonNull ItemStack stack) {
        if (stack.getItem() instanceof RedDoorBlock) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }
}
