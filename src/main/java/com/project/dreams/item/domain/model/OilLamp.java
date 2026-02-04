package com.project.dreams.item.domain.model;

import com.project.dreams.Config;
import com.project.dreams.item.ModItems;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LightBlock;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class OilLamp extends Item {
    // As configurações agora vêm do Config.java
    private static final int TICKS_PER_SECOND = 20;

    public OilLamp(Properties properties) {
        // A durabilidade inicial pode ser baseada no valor padrão do config
        super(properties.stacksTo(1).durability(100));
    }

    /**
     * Ciclo de vida: executado a cada tick enquanto o item está no inventário.
     */
    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel level, @NonNull Entity entity, EquipmentSlot slot) {
        ensureDefaults(stack);

        if (getIsOn(stack)) {
            processConsumption(stack);
            // Emissão de luz: coloca um bloco de luz na posição da entidade se for um jogador
            if (entity instanceof Player player) {
                handleLightEmission(level, player);
            }
        }

        mirrorDurability(stack);
    }

    private void handleLightEmission(ServerLevel level, Player player) {
        BlockPos pos = player.blockPosition().above(); // Luz na altura da cabeça/corpo
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
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
            int minToStart = Config.OIL_LAMP_FUEL_CONSUMPTION_RATE.get() + 1;
            if (currentFuel > minToStart) {
                turnOn(stack, player);
            } else {
                player.displayClientMessage(Component.translatable("message.dreams.oil_lamp.no_fuel"), true);
            }
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Processa o acumulador de ticks para garantir consumo constante por segundo.
     */
    private void processConsumption(ItemStack stack) {
        int accum = getTickAccum(stack) + 1;

        if (accum >= TICKS_PER_SECOND) {
            consumeFuel(stack, Config.OIL_LAMP_FUEL_CONSUMPTION_RATE.get());
            accum = 0;
        }

        setTickAccum(stack, accum);
    }

    /**
     * Aplica a redução de combustível e verifica condições de desligamento automático.
     */
    private void consumeFuel(ItemStack stack, int amount) {
        int fuel = Math.max(0, getFuel(stack) - amount);
        
        // Regra: Se o combustível atingir o nível crítico (1), desliga automaticamente
        if (fuel <= 1) {
            fuel = 1;
            if (getIsOn(stack)) {
                setIsOn(stack, false);
            }
        }
        
        setFuel(stack, fuel);
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
        int maxFuel = Config.OIL_LAMP_MAX_FUEL.get();
        stack.setDamageValue(Math.max(0, maxFuel - fuel));
    }

    private void ensureDefaults(ItemStack stack) {
        if (!stack.has(OilLampDataComponents.IS_ON)) setIsOn(stack, false);
        if (!stack.has(OilLampDataComponents.FUEL)) setFuel(stack, Config.OIL_LAMP_MAX_FUEL.get());
        if (!stack.has(OilLampDataComponents.TICK_ACCUM)) setTickAccum(stack, 0);
    }

    // --- ENCAPSULAMENTO DE DADOS (Princípios OO) ---

    private boolean getIsOn(ItemStack stack) {
        return stack.getOrDefault(OilLampDataComponents.IS_ON, false);
    }

    private void setIsOn(ItemStack stack, boolean value) {
        stack.set(OilLampDataComponents.IS_ON, value);
    }

    private int getFuel(ItemStack stack) {
        return stack.getOrDefault(OilLampDataComponents.FUEL, Config.OIL_LAMP_MAX_FUEL.get());
    }

    private void setFuel(ItemStack stack, int value) {
        stack.set(OilLampDataComponents.FUEL, value);
    }

    // Extensão: método público para reabastecer de forma segura
    public void addFuel(ItemStack stack, int amount) {
        if (amount <= 0) return;
        int maxFuel = Config.OIL_LAMP_MAX_FUEL.get();
        int fuel = Math.max(0, Math.min(maxFuel, getFuel(stack) + amount));
        setFuel(stack, fuel);
        mirrorDurability(stack);
    }

    private int getTickAccum(ItemStack stack) {
        return stack.getOrDefault(OilLampDataComponents.TICK_ACCUM, 0);
    }

    private void setTickAccum(ItemStack stack, int value) {
        stack.set(OilLampDataComponents.TICK_ACCUM, value);
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
        int maxFuel = Config.OIL_LAMP_MAX_FUEL.get();
        boolean isOn = getIsOn(stack);

        output.accept(Component.translatable("tooltip.dreams.oil_lamp.fuel", fuel, maxFuel));
        output.accept(Component.translatable(isOn ? "tooltip.dreams.oil_lamp.state.on" : "tooltip.dreams.oil_lamp.state.off"));

        super.appendHoverText(stack, context, display, output, flag);
    }


    /**
     * INTERAÇÃO PROFISSIONAL:
     * Permite reabastecer arrastando o frasco de óleo sobre a lâmpada no inventário.
     */
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
        if (action == ClickAction.SECONDARY && other.is(ModItems.OIL_BOTTLE.get())) {
            int currentFuel = getFuel(stack);
            int maxFuel = Config.OIL_LAMP_MAX_FUEL.get();
            
            if (currentFuel < maxFuel) {
                addFuel(stack, 25); // Cada frasco recupera 25 unidades
                other.shrink(1);
                player.displayClientMessage(Component.translatable("message.dreams.oil_lamp.refueled"), true);
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, accessor);
    }
}
