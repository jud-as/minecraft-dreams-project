package com.project.dreams.item.domain.model.oil_lamp;

import com.project.dreams.block.domain.model.oil_lamp.dto.mapper.OilLampFuelDTO;
import com.project.dreams.Settings;
import com.project.dreams.block.service.BlockService;
import com.project.dreams.block.domain.model.oil_lamp.block.OilLampBlock;
import com.project.dreams.block.domain.model.oil_lamp.block_entity.OilLampBlockEntity;
import com.project.dreams.item.service.ItemRegisterService;
import com.project.dreams.item.service.oil_lamp.OilLampLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class OilLampItem extends BlockItem {

    public OilLampItem(Properties properties) {
        super(BlockService.OIL_LAMP_BLOCK.get(), properties.stacksTo(1).durability(100));
    }

    /**
     * Ciclo de vida: executado a cada tick enquanto o item está no inventário.
     */
    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel level, @NonNull Entity entity, EquipmentSlot slot) {
        ensureDefaults(stack);

        OilLampLogic.tickFuel(new ItemStackFuelWrapper(stack));

        if (getIsOn(stack)) {
            if (entity instanceof Player player) {
                handleLightEmission(level, player, stack);
            }
        } else {
            BlockPos lastPos = stack.get(OilLampDataComponents.LIGHT_POS.get());
            if (lastPos != null) {
                removeLight(level, lastPos);
                stack.remove(OilLampDataComponents.LIGHT_POS.get());
            }
        }

        mirrorDurability(stack);
    }

    @Override
    public @NonNull InteractionResult place(@NonNull BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        BlockPos lightPos = stack.get(OilLampDataComponents.LIGHT_POS.get());
        if (lightPos != null && context.getLevel() instanceof ServerLevel serverLevel) {
            removeLight(serverLevel, lightPos);
            stack.remove(OilLampDataComponents.LIGHT_POS.get());
        }
        return super.place(context);
    }

    private void handleLightEmission(ServerLevel level, Player player, ItemStack stack) {
        BlockPos currentPos = player.blockPosition().above();
        BlockPos lastPos = stack.get(OilLampDataComponents.LIGHT_POS.get());

        if (lastPos != null && !lastPos.equals(currentPos)) {
            removeLight(level, lastPos);
        }

        BlockState state = level.getBlockState(currentPos);
        if (state.isAir() || state.is(Blocks.LIGHT)) {
            level.setBlock(currentPos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
            stack.set(OilLampDataComponents.LIGHT_POS.get(), currentPos);
        }
    }

    private void removeLight(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).is(Blocks.LIGHT)) {
            level.removeBlock(pos, false);
        }
    }

    /**
     * Ação de Uso: Alterna o estado da lâmpada (Ligado/Desligado).
     */
    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ensureDefaults(stack);

        boolean currentState = getIsOn(stack);
        int currentFuel = getFuel(stack);

        if (currentState) {
            turnOff(stack, player);
        } else {
            int minToStart = Settings.OIL_LAMP_FUEL_CONSUMPTION_RATE.get() + 1;
            if (currentFuel > minToStart) {
                turnOn(stack, player);
            } else {
                player.displayClientMessage(Component.translatable("message.dreams.oil_lamp.no_fuel"), true);
            }
        }

        return InteractionResult.SUCCESS;
    }


    private void turnOn(ItemStack stack, Player player) {
        setIsOn(stack, true);
        player.displayClientMessage(Component.translatable("message.dreams.oil_lamp.on"), true);
    }

    private void turnOff(ItemStack stack, Player player) {
        setIsOn(stack, false);
        player.displayClientMessage(Component.translatable("message.dreams.oil_lamp.off"), true);
    }

    /**
     * Sincroniza a durabilidade do Minecraft (dano) com o combustível restante.
     * damage = 0 (barra cheia), damage = MAX_FUEL (barra vazia).
     */
    private void mirrorDurability(ItemStack stack) {
        int fuel = getFuel(stack);
        int maxFuel = Settings.OIL_LAMP_MAX_FUEL.get();
        stack.setDamageValue(Math.max(0, maxFuel - fuel));
    }

    private void ensureDefaults(ItemStack stack) {
        if (!stack.has(OilLampDataComponents.IS_ON.get())) setIsOn(stack, false);
        if (!stack.has(OilLampDataComponents.FUEL.get())) setFuel(stack, Settings.OIL_LAMP_MAX_FUEL.get());
        if (!stack.has(OilLampDataComponents.TICK_ACCUM.get())) setTickAccum(stack, 0);
    }

    private boolean getIsOn(ItemStack stack) {
        return stack.getOrDefault(OilLampDataComponents.IS_ON.get(), false);
    }

    private void setIsOn(ItemStack stack, boolean value) {
        stack.set(OilLampDataComponents.IS_ON.get(), value);
    }

    private int getFuel(ItemStack stack) {
        return stack.getOrDefault(OilLampDataComponents.FUEL.get(), Settings.OIL_LAMP_MAX_FUEL.get());
    }

    private void setFuel(ItemStack stack, int value) {
        stack.set(OilLampDataComponents.FUEL.get(), value);
    }

    // Extensão: método público para reabastecer de forma segura
    public void addFuel(ItemStack stack, int amount) {
        if (amount <= 0) return;
        int maxFuel = Settings.OIL_LAMP_MAX_FUEL.get();
        int fuel = Math.max(0, Math.min(maxFuel, getFuel(stack) + amount));
        setFuel(stack, fuel);
        mirrorDurability(stack);
    }

    private int getTickAccum(ItemStack stack) {
        return stack.getOrDefault(OilLampDataComponents.TICK_ACCUM.get(), 0);
    }

    private void setTickAccum(ItemStack stack, int value) {
        stack.set(OilLampDataComponents.TICK_ACCUM.get(), value);
    }

    @Override
    public void appendHoverText(
            @NonNull ItemStack stack,
            Item.@NonNull TooltipContext context,
            @NonNull TooltipDisplay display,
            Consumer<Component> output,
            @NonNull TooltipFlag flag) {

        ensureDefaults(stack);
        int fuel = getFuel(stack);
        int maxFuel = Settings.OIL_LAMP_MAX_FUEL.get();
        boolean isOn = getIsOn(stack);

        output.accept(Component.translatable("tooltip.dreams.oil_lamp.fuel", fuel, maxFuel));
        output.accept(Component.translatable(isOn ? "tooltip.dreams.oil_lamp.state.on" : "tooltip.dreams.oil_lamp.state.off"));

        super.appendHoverText(stack, context, display, output, flag);
    }


    /**
     * Permite reabastecer arrastando o frasco de óleo sobre a lâmpada no inventário.
     */
    @Override
    public boolean overrideOtherStackedOnMe(
            @NonNull ItemStack stack,
            @NonNull ItemStack other,
            @NonNull Slot slot,
            @NonNull ClickAction action,
            @NonNull Player player,
            @NonNull SlotAccess accessor) {
        if (action == ClickAction.SECONDARY && other.is(ItemRegisterService.OIL_BOTTLE.get())) {
            int currentFuel = getFuel(stack);
            int maxFuel = Settings.OIL_LAMP_MAX_FUEL.get();
            
            if (currentFuel < maxFuel) {
                addFuel(stack, 25); // Cada frasco recupera 25 unidades
                other.shrink(1);
                player.displayClientMessage(Component.translatable("message.dreams.oil_lamp.refueled"), true);
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, accessor);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NonNull BlockPos pos, @NonNull Level level, @Nullable Player player, @NonNull ItemStack stack, @NonNull BlockState state) {
        boolean result = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof OilLampBlockEntity lamp) {
            lamp.setFuel(getFuel(stack));
            // Sincroniza o estado LIT do bloco com o IS_ON do item
            if (getIsOn(stack)) {
                level.setBlock(pos, state.setValue(OilLampBlock.LIT, true), 3);
            }
        }
        return result;
    }

    private class ItemStackFuelWrapper implements OilLampFuelDTO {
        private final ItemStack stack;

        public ItemStackFuelWrapper(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int getFuel() {
            return OilLampItem.this.getFuel(stack);
        }

        @Override
        public void setFuel(int fuel) {
            OilLampItem.this.setFuel(stack, fuel);
        }

        @Override
        public int getTickAccum() {
            return OilLampItem.this.getTickAccum(stack);
        }

        @Override
        public void setTickAccum(int ticks) {
            OilLampItem.this.setTickAccum(stack, ticks);
        }

        @Override
        public boolean isLit() {
            return OilLampItem.this.getIsOn(stack);
        }

        @Override
        public void setLit(boolean lit) {
            OilLampItem.this.setIsOn(stack, lit);
        }
    }
}
