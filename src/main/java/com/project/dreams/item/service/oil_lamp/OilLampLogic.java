package com.project.dreams.item.service.oil_lamp;

import com.project.dreams.Settings;
import java.util.function.Consumer;

/**
 * Lógica compartilhada para o funcionamento da Lâmpada de Óleo.
 * Evita a redundância de código entre Item e Bloco.
 */
public class OilLampLogic {
    public static final int TICKS_PER_SECOND = 20;

    /**
     * Processa um tick de consumo de combustível.
     * @param currentFuel Combustível atual
     * @param tickAccum Acumulador de ticks
     * @param isOn Se a lâmpada está ligada
     * @param fuelSetter Função para atualizar o combustível
     * @param tickAccumSetter Função para atualizar o acumulador
     * @param onEmpty Callback executado quando o combustível acaba
     */
    public static void tickFuel(int currentFuel, int tickAccum, boolean isOn, 
                                Consumer<Integer> fuelSetter, Consumer<Integer> tickAccumSetter, Runnable onEmpty) {
        if (!isOn) return;

        int nextAccum = tickAccum + 1;
        if (nextAccum >= TICKS_PER_SECOND) {
            int consumption = Settings.OIL_LAMP_FUEL_CONSUMPTION_RATE.get();
            int nextFuel = Math.max(0, currentFuel - consumption);
            fuelSetter.accept(nextFuel);
            nextAccum = 0;

            if (nextFuel <= 1) {
                fuelSetter.accept(1); // Mantém 1 para a barra de durabilidade
                onEmpty.run();
            }
        }
        tickAccumSetter.accept(nextAccum);
    }
}
