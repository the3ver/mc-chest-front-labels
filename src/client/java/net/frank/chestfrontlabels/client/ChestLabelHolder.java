package net.frank.chestfrontlabels.client;

// Von ChestRenderStateMixin an ChestRenderState angehängt: Beschriftung für den aktuellen Frame, leer ohne Beschriftung
public interface ChestLabelHolder {

    String chestfrontlabels$getLabel();

    void chestfrontlabels$setLabel(String label);
}
