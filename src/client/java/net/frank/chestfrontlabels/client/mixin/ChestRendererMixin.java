package net.frank.chestfrontlabels.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.frank.chestfrontlabels.ChestLabelLogic;
import net.frank.chestfrontlabels.ChestLabels;
import net.frank.chestfrontlabels.client.ChestLabelHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Zeichnet die Beschriftung wie Schildtext auf die Front des Truhenkörpers, unterhalb des Schlosses
@Mixin(ChestRenderer.class)
public class ChestRendererMixin {

    // Front des Truhenkörpers liegt 1/16 hinter der Blockkante; knapp davor, damit der Text nicht flackert
    @Unique
    private static final float FRONT_Z = 0.5f - 1.0f / 16.0f + 0.005f;
    // Textmitte 4/16 über dem Blockboden, relativ zur Blockmitte; das Schloss beginnt bei 7/16
    @Unique
    private static final float TEXT_CENTER_Y = 4.0f / 16.0f - 0.5f;

    @Unique
    private static final int TEXT_COLOR = 0xFF000000;

    @Inject(
            method =
                    "extractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("TAIL"))
    // Mixin verlangt alle Parameter der Zielmethode, auch ungenutzte
    @SuppressWarnings("UnusedVariable")
    private void chestfrontlabels$extractLabel(
            BlockEntity chest,
            ChestRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay breakProgress,
            CallbackInfo ci) {
        String label = chest instanceof ChestBlockEntity chestEntity ? ChestLabels.labelOf(chestEntity) : "";
        ((ChestLabelHolder) state).chestfrontlabels$setLabel(label);
    }

    @Inject(
            method =
                    "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("TAIL"))
    // Mixin verlangt alle Parameter der Zielmethode, auch ungenutzte
    @SuppressWarnings("UnusedVariable")
    private void chestfrontlabels$submitLabel(
            ChestRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState camera,
            CallbackInfo ci) {
        String label = ((ChestLabelHolder) state).chestfrontlabels$getLabel();
        // Doppeltruhe: die linke Hälfte zeichnet die gemeinsame Beschriftung mittig über beide Hälften
        if (label.isEmpty() || state.type == ChestType.RIGHT) return;
        boolean isDouble = state.type == ChestType.LEFT;

        Font font = Minecraft.getInstance().font;
        FormattedCharSequence text = Component.literal(label).getVisualOrderText();
        int textWidth = font.width(text);
        float scale = ChestLabelLogic.textScale(
                textWidth, isDouble ? ChestLabelLogic.DOUBLE_FRONT_WIDTH : ChestLabelLogic.SINGLE_FRONT_WIDTH);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        // Danach zeigt +Z zur Truhenfront (SOUTH = 0°)
        poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
        // Die rechte Hälfte liegt von der linken aus bei -X (ChestBlock.getConnectedDirection: im Uhrzeigersinn)
        poseStack.translate(isDouble ? -0.5f : 0.0f, TEXT_CENTER_Y, FRONT_Z);
        poseStack.scale(scale, -scale, scale);
        collector.submitText(
                poseStack,
                -textWidth / 2.0f,
                -font.lineHeight / 2.0f,
                text,
                false,
                Font.DisplayMode.POLYGON_OFFSET,
                state.lightCoords,
                TEXT_COLOR,
                0,
                0);
        poseStack.popPose();
    }
}
