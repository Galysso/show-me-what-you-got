package io.github.apace100.smwyg.mixin;

import com.lootbeams.compat.iceberg.IcebergCompat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.List;

@Mixin(IcebergCompat.class)
public class IcebergCompatMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Rect2i getTooltipRect(ItemStack itemStack, DrawContext context, TooltipPositioner positioner, List<TooltipComponent> components, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, TextRenderer textRenderer, int minWidth, boolean centeredTitle) {
        try {
            List<TooltipComponent> mutableComponents = new ArrayList<>(components);
            return (Rect2i)Class.forName("com.anthonyhilyard.iceberg.util.Tooltips").getMethod("calculateRect", ItemStack.class, DrawContext.class, TooltipPositioner.class, List.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, TextRenderer.class, Integer.TYPE, Boolean.TYPE).invoke((Object)null, itemStack, context, positioner, mutableComponents, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, textRenderer, minWidth, centeredTitle);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
