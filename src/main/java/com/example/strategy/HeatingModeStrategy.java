package com.example.strategy;

import com.example.model.Grid;

public interface HeatingModeStrategy {
    void applyHeatMode(Grid grid, double externalTemperature);
}
