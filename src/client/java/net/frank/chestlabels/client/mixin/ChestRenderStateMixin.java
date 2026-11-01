package net.frank.chestlabels.client.mixin;

import net.frank.chestlabels.client.ChestLabelHolder;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChestRenderState.class)
public class ChestRenderStateMixin implements ChestLabelHolder {

    @Unique
    private String chestlabels$label;

    @Override
    public String chestlabels$getLabel() {
        return chestlabels$label;
    }

    @Override
    public void chestlabels$setLabel(String label) {
        chestlabels$label = label;
    }
}
