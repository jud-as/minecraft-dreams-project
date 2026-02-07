package com.project.dreams.block.domain.model.oil_lamp.block;

import com.project.dreams.block.domain.model.oil_lamp.block_entity.OilLampBlockEntity;
import com.mojang.serialization.MapCodec;
import com.project.dreams.block.service.BlockEntityService;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Containers;
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
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof OilLampBlockEntity lamp && !level.isClientSide() && !player.isCreative()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), lamp.toItemStack());
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeComponents, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof OilLampBlockEntity lamp) {
            return lamp.toItemStack();
        }
        return super.getCloneItemStack(level, pos, state, includeComponents, player);
    }
}
