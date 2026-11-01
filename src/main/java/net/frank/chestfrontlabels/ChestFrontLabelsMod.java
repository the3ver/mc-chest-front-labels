package net.frank.chestfrontlabels;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class ChestFrontLabelsMod implements ModInitializer {

    public static final String MOD_ID = "chest-front-labels";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ChestLabels.register();
    }
}
