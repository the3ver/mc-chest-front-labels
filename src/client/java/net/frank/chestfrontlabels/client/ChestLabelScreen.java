package net.frank.chestfrontlabels.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frank.chestfrontlabels.ChestLabelLogic;
import net.frank.chestfrontlabels.ChestLabels;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

// Eingabe der Truhenbeschriftung. Leerer Text entfernt sie, Abbrechen oder Esc lässt sie unverändert.
public class ChestLabelScreen extends Screen {

    private final BlockPos pos;
    private final String initialLabel;
    private EditBox labelBox;

    public ChestLabelScreen(BlockPos pos, String initialLabel) {
        super(Component.translatable("screen.chest-front-labels.title"));
        this.pos = pos;
        this.initialLabel = initialLabel;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        labelBox = new EditBox(font, centerX - 100, centerY - 10, 200, 20, title);
        labelBox.setMaxLength(ChestLabelLogic.MAX_LENGTH);
        labelBox.setValue(initialLabel);
        labelBox.setHint(Component.translatable("screen.chest-front-labels.hint"));
        addRenderableWidget(labelBox);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> submit())
                .bounds(centerX - 100, centerY + 20, 98, 20)
                .build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(centerX + 2, centerY + 20, 98, 20)
                .build());

        setInitialFocus(labelBox);
    }

    private void submit() {
        ClientPlayNetworking.send(new ChestLabels.SetLabelPayload(pos, ChestLabelLogic.sanitize(labelBox.getValue())));
        onClose();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isConfirmation() && labelBox.isFocused()) {
            submit();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, height / 2 - 40, 0xFFFFFFFF);
        graphics.centeredText(
                font, Component.translatable("screen.chest-front-labels.info"), width / 2, height / 2 - 26, 0xFFA0A0A0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
