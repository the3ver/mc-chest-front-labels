package net.frank.chestfrontlabels.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frank.chestfrontlabels.ChestLabels;

public class ChestFrontLabelsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                ChestLabels.OpenEditorPayload.TYPE,
                (payload, context) ->
                        context.client().gui.setScreen(new ChestLabelScreen(payload.pos(), payload.label())));
        ChestScreenButton.register();
    }
}
