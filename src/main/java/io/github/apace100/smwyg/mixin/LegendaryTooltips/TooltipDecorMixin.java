package io.github.apace100.smwyg.mixin.LegendaryTooltips;

import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.anthonyhilyard.legendarytooltips.tooltip.ItemModelComponent;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import io.github.apace100.smwyg.tooltip.HorizontalLayoutTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TooltipDecor.class)
public class TooltipDecorMixin {
    @Unique
    private static List<TooltipComponent> capturedComponents;

    @Inject(method = "drawBorder", at = @At("HEAD"))
    private static void captureComponents(MatrixStack poseStack, int x, int y, int width, int height, ItemStack item, List<TooltipComponent> components, TextRenderer font, LegendaryTooltipsConfig.FrameDefinition frameDefinition, boolean comparison, int index, CallbackInfo ci) {
        TooltipDecorMixin.capturedComponents = components;
    }

    @ModifyVariable(
        method = "drawBorder",
        at = @At(value = "STORE"), // Cela peut nécessiter ajustement !
        name = "numComponents"
    )
    private static int modifyNumComponents(int value) {
        if (!TooltipDecorMixin.capturedComponents.isEmpty()) {
            if (TooltipDecorMixin.capturedComponents.get(0) instanceof HorizontalLayoutTooltipComponent) {
                return value + 1;
            } else {
                if (TooltipDecorMixin.capturedComponents.get(0) instanceof ItemModelComponent) {
                    return value - 1;
                }
                return value;
            }
        }
        return value;
    }
}
