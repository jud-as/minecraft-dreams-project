package com.project.dreams.block.domain.model.oil_lamp.dto.mapper;

public interface OilLampFuelDTO {
    int getFuel();
    void setFuel(int fuel);
    int getTickAccum();
    void setTickAccum(int ticks);
    boolean isLit();
    void setLit(boolean lit);
}
