package com.example.strategy;

import com.example.model.Grid;

/** Classe pour le mode de chauffe manuel :  dans ce mode, l’utilisateur a le total contrôle des sources
 de chaleur. Tant qu’il ne les active pas, elles ne s’activent pas.
 * */

public class ManualHeatingModeStrategy  implements HeatingModeStrategy {
    @Override
    public void applyHeatMode(Grid grid, double externalTemperature) {
        // dans le mode de chauffe manuel l'utilisateur contrôle directement les sources de chaleur donc ici on ne fait rien et on laisse l'utilisateur controler
        // les sources de chaleur à l'avenir si le comportement de ce mode de chauffe doit être modifié il suffira de de modifier cette methode
    }
}
