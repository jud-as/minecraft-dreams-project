package com.project.dreams.block.domain.model.block_entity;

import com.project.dreams.Settings;
import com.project.dreams.block.OilLampBlock;
import com.project.dreams.block.service.BlockEntityService;
import com.project.dreams.item.service.oil_lamp.OilLampLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class OilLampBlockEntity extends BlockEntity {
    private int fuel;
    private int tickAccum;

    public OilLampBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityService.OIL_LAMP.get(), pos, state);
        this.fuel = Settings.OIL_LAMP_MAX_FUEL.get();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OilLampBlockEntity be) {
        if (level.isClientSide()) return;

        OilLampLogic.tickFuel(
            be.fuel, 
            be.tickAccum, 
            state.getValue(OilLampBlock.LIT),
            f -> be.fuel = f,
            a -> be.tickAccum = a,
            () -> level.setBlock(pos, state.setValue(OilLampBlock.LIT, false), 3)
        );
        
        if (be.tickAccum == 0) { // Indica que houve mudança de combustível ou tick processado
            be.setChanged();
        }
    }

    public void toggle() {
        if (level == null || level.isClientSide()) return;
        
        BlockState state = getBlockState();
        boolean currentlyLit = state.getValue(OilLampBlock.LIT);
        
        if (currentlyLit) {
            level.setBlock(worldPosition, state.setValue(OilLampBlock.LIT, false), 3);
        } else {
            int minToStart = Settings.OIL_LAMP_FUEL_CONSUMPTION_RATE.get() + 1;
            if (fuel > minToStart) {
                level.setBlock(worldPosition, state.setValue(OilLampBlock.LIT, true), 3);
            }
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(
            @NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Fuel", fuel);
        output.putInt("TickAccum", tickAccum);
    }

    @Override
    protected void loadAdditional(
            @NonNull ValueInput input) {
        super.loadAdditional(input);
        this.fuel = input.getIntOr("Fuel", Settings.OIL_LAMP_MAX_FUEL.get());
        this.tickAccum = input.getIntOr("TickAccum", 0);
    }

    public int getFuel() { return fuel; }
    public void setFuel(int fuel) { this.fuel = fuel; setChanged(); }
}
