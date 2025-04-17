package io.github.apace100.smwyg.mixin.Iceberg;

import com.anthonyhilyard.iceberg.component.TitleBreakComponent;
import com.anthonyhilyard.iceberg.util.Tooltips;
import io.github.apace100.smwyg.tooltip.HorizontalLayoutTooltipComponent;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(value = Tooltips.class, remap = false)
public class TooltipsMixin {
    /**
     * @author Galysso
     * @reason Add compatibility with HorizontalLayoutTooltipComponent as a title line
     */
    @Overwrite
    public static int calculateTitleLines(List<TooltipComponent> components) {
        if (components != null && !components.isEmpty()) {
            int titleLines = 0;
            boolean foundTitleBreak = false;

            for(TooltipComponent component : components) {
                if (component instanceof OrderedTextTooltipComponent || component instanceof HorizontalLayoutTooltipComponent) {
                    ++titleLines;
                } else {
                    if (component instanceof TitleBreakComponent) {
                        foundTitleBreak = true;
                        break;
                    }

                    titleLines = 0;
                }
            }

            if (!foundTitleBreak) {
                titleLines = 1;
            }

            return titleLines;
        } else {
            return 0;
        }
    }
}
