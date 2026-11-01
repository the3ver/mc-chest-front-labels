package net.frank.chestfrontlabels.client.mixin;

import net.frank.chestfrontlabels.client.ChestLabelHolder;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChestRenderState.class)
public class ChestRenderStateMixin implements ChestLabelHolder {

    @Unique
    private String chestfrontlabels$label = "";

    @Override
    public String chestfrontlabels$getLabel() {
        return chestfrontlabels$label;
    }

    @Override
    public void chestfrontlabels$setLabel(String label) {
        chestfrontlabels$label = label;
    }
}
