package net.frank.chestlabels.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frank.chestlabels.ChestLabels;

public class ChestLabelsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                ChestLabels.OpenEditorPayload.TYPE,
                (payload, context) ->
                        context.client().gui.setScreen(new ChestLabelScreen(payload.pos(), payload.label())));
    }
}
