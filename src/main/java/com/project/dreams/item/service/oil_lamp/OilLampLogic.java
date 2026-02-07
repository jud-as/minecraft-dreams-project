package com.project.dreams.item.service.oil_lamp;

import com.project.dreams.block.domain.model.oil_lamp.dto.mapper.OilLampFuelDTO;
import com.project.dreams.Settings;

/**
 * Lógica compartilhada para o funcionamento da Lâmpada de Óleo.
 * Evita a redundância de código entre Item e Bloco.
 */
public class OilLampLogic {
    public static final int TICKS_PER_SECOND = 20;

    /**
     * Processa um tick de consumo de combustível usando o DTO.
     * @param fuelDTO DTO que encapsula os dados da lâmpada
     */
    public static void tickFuel(OilLampFuelDTO fuelDTO) {
        if (!fuelDTO.isLit()) return;

        int tickAccum = fuelDTO.getTickAccum();
        int nextAccum = tickAccum + 1;
        
        if (nextAccum >= TICKS_PER_SECOND) {
            int consumption = Settings.OIL_LAMP_FUEL_CONSUMPTION_RATE.get();
            int currentFuel = fuelDTO.getFuel();
            int nextFuel = Math.max(0, currentFuel - consumption);
            
            fuelDTO.setFuel(nextFuel);
            nextAccum = 0;

            if (nextFuel <= 1) {
                fuelDTO.setFuel(1); // Mantém 1 para a barra de durabilidade
                fuelDTO.setLit(false);
            }
        }
        fuelDTO.setTickAccum(nextAccum);
    }
}
