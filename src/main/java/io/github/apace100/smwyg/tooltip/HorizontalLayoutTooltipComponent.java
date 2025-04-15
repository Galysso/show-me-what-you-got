package io.github.apace100.smwyg.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

import java.util.List;

public class HorizontalLayoutTooltipComponent implements TooltipComponent {

    private final List<TooltipComponent> components;
    private final int gap;

    private int height;
    private boolean heightDirty = true;

    public HorizontalLayoutTooltipComponent(List<TooltipComponent> components, int gap) {
        this.components = components;
        this.gap = gap;
    }

    private void calculateHeight() {
        int h = 0;
        for(TooltipComponent tc : components) {
            if(tc.getHeight() > h) {
                h = tc.getHeight();
            }
        }
        height = h;
    }

    @Override
    public int getHeight() {
        if(heightDirty) {
            calculateHeight();
            heightDirty = false;
        }
        return height;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int sumOfWidths = 0;
        for(TooltipComponent tc : components) {
            sumOfWidths += tc.getWidth(textRenderer);
        }
        sumOfWidths += gap * (components.size() - 1);
        return sumOfWidths;
    }

    private int getComponentY(TooltipComponent component, TextRenderer textRenderer) {
        int height = component.getHeight();
        return (this.height - height) / 2;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        int currentX = x;
        for(TooltipComponent tc : components) {
            tc.drawText(textRenderer, currentX, y + getComponentY(tc, textRenderer), matrix, vertexConsumers);
            currentX += tc.getWidth(textRenderer) + gap;
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        int currentX = x;
        for(TooltipComponent tc : components) {
            tc.drawItems(textRenderer, currentX, y + getComponentY(tc, textRenderer), context);
            currentX += tc.getWidth(textRenderer) + gap;
        }
    }
}
