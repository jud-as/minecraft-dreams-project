package com.project.dreams.block;

import com.project.dreams.block.domain.model.block_entity.OilLampBlockEntity;
import com.mojang.serialization.MapCodec;
import com.project.dreams.block.service.BlockEntityService;
import com.project.dreams.item.service.ItemRegisterService;
import com.project.dreams.item.domain.model.oil_lamp.OilLampDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class OilLampBlock extends BaseEntityBlock {
    public static final MapCodec<OilLampBlock> CODEC = simpleCodec(OilLampBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public OilLampBlock(Properties properties) {
        super(properties
                .lightLevel(state -> state.getValue(LIT) ? 15 : 0)
                .strength(0f)
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @NonNull Level level,
            @NonNull BlockState state,
            @NonNull BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityService.OIL_LAMP.get(), OilLampBlockEntity::tick);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(
            @NonNull BlockPos pos,
            @NonNull BlockState state) {
        return new OilLampBlockEntity(pos, state);
    }

    @Override
    public @NonNull RenderShape getRenderShape(
            @NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(
            @NonNull BlockState state, Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof OilLampBlockEntity lamp) {
                lamp.toggle();
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void attack(
            @NonNull BlockState state, Level level,
            @NonNull BlockPos pos,
            @NonNull Player player) {
        if (!level.isClientSide()) {
            this.tryPickup(level, pos, state, player);
        }
    }

    private void tryPickup(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof OilLampBlockEntity lamp) {
            ItemStack stack = new ItemStack(ItemRegisterService.OIL_LAMP.get());
            stack.set(OilLampDataComponents.FUEL.get(), lamp.getFuel());
            stack.set(OilLampDataComponents.IS_ON.get(), state.getValue(LIT));
            
            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected @NonNull ItemStack getCloneItemStack(
            @NonNull LevelReader level,
            @NonNull BlockPos pos,
            @NonNull BlockState state, boolean includeComponents) {
        ItemStack stack = super.getCloneItemStack(level, pos, state, includeComponents);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof OilLampBlockEntity lamp) {
            stack.set(OilLampDataComponents.FUEL.get(), lamp.getFuel());
            stack.set(OilLampDataComponents.IS_ON.get(), state.getValue(LIT));
        }
        return stack;
    }

}
