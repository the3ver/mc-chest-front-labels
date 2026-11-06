package net.frank.chestfrontlabels.client;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.frank.chestfrontlabels.ChestLabels;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;

// Knopf "Beschriften" oben rechts in der Truhenansicht. Die Truhe ist die, auf die der Spieler beim Öffnen schaute.
final class ChestScreenButton {

    // Maße der Vanilla-Truhenansicht (ContainerScreen)
    private static final int IMAGE_WIDTH = 176;
    private static final int BASE_HEIGHT = 114;
    private static final int ROW_HEIGHT = 18;

    private ChestScreenButton() {}

    static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (!(screen instanceof ContainerScreen container)
                    || !(client.hitResult instanceof BlockHitResult hit)
                    || client.level == null
                    || !(client.level.getBlockEntity(hit.getBlockPos()) instanceof ChestBlockEntity chest)) {
                return;
            }
            Component text = Component.translatable("screen.chest-front-labels.button");
            int buttonWidth = Screens.getFont(screen).width(text) + 8;
            int top = (height - BASE_HEIGHT - container.getMenu().getRowCount() * ROW_HEIGHT) / 2;
            int right = (width + IMAGE_WIDTH) / 2;
            Screens.getWidgets(screen)
                    .add(Button.builder(text, button -> {
                                if (client.player == null) return;
                                String label = ChestLabels.labelOf(chest);
                                client.player.closeContainer();
                                client.gui.setScreen(new ChestLabelScreen(chest.getBlockPos(), label));
                            })
                            .bounds(right - 7 - buttonWidth, top + 3, buttonWidth, 12)
                            .build());
        });
    }
}
