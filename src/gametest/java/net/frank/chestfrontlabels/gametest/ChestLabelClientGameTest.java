package net.frank.chestfrontlabels.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.frank.chestfrontlabels.ChestLabels;
import net.frank.chestfrontlabels.client.ChestLabelScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

// Einzel- und Doppeltruhe mit Beschriftung, Screenshot von vorn
public class ChestLabelClientGameTest implements FabricClientGameTest {

    private static final BlockPos SINGLE = new BlockPos(0, 100, 0);
    // Blick nach Süden: die linke Hälfte hat ihre rechte im Uhrzeigersinn, also im Westen
    private static final BlockPos DOUBLE_LEFT = new BlockPos(3, 100, 0);
    private static final BlockPos DOUBLE_RIGHT = new BlockPos(2, 100, 0);

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext game = context.worldBuilder().create()) {
            game.getServer().runCommand("time set noon");
            game.getServer().runCommand("weather clear");
            game.getServer().runCommand("fill -3 99 -3 6 104 6 air");
            game.getServer().runCommand("fill -3 99 -3 6 99 6 stone");
            game.getServer().runCommand("setblock 0 100 0 chest[facing=south]");
            game.getServer().runCommand("setblock 2 100 0 chest[facing=south,type=right]");
            game.getServer().runCommand("setblock 3 100 0 chest[facing=south,type=left]");
            game.getServer().runOnServer(server -> {
                label(server.overworld(), SINGLE, "Erze\nKohle");
                label(server.overworld(), DOUBLE_LEFT, "Holz, Stein und Glas");
                label(server.overworld(), DOUBLE_RIGHT, "Holz, Stein und Glas");
            });
            game.getServer().runCommand("tp @p 2.0 100 2.8 180 30");
            context.runOnClient(client -> {
                if (!client.gui.hud.isHidden()) client.gui.hud.toggle();
            });
            game.getConnection().waitForChunksRender();
            context.waitTicks(20);

            String synced = context.computeOnClient(client ->
                    game.getConnection().getClientLevel().getBlockEntity(SINGLE) instanceof ChestBlockEntity chest
                            ? ChestLabels.labelOf(chest)
                            : "");
            if (!synced.equals("Erze\nKohle"))
                throw new AssertionError("Beschriftung nicht am Client: '" + synced + "'");

            context.takeScreenshot(TestScreenshotOptions.of("chest-labels").withSize(1920, 1080));

            context.setScreen(() -> new ChestLabelScreen(SINGLE, "Erze\nKohle"));
            context.takeScreenshot("editor");
        }
    }

    private static void label(Level level, BlockPos pos, String text) {
        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest))
            throw new AssertionError("Keine Truhe: " + pos);
        chest.setAttached(ChestLabels.LABEL, text);
        chest.setChanged();
    }
}
