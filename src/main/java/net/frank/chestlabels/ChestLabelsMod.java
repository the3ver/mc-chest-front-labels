package net.frank.chestlabels;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class ChestLabelsMod implements ModInitializer {

    public static final String MOD_ID = "chestlabels";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {}
}
