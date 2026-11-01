package net.frank.chestlabels.client;

// Von ChestRenderStateMixin an ChestRenderState angehängt: Beschriftung für den aktuellen Frame, null ohne Beschriftung
public interface ChestLabelHolder {

    String chestlabels$getLabel();

    void chestlabels$setLabel(String label);
}
