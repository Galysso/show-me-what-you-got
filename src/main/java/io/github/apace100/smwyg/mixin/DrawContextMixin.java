package io.github.apace100.smwyg.mixin;

import io.github.apace100.smwyg.ShowMeWhatYouGot;
import io.github.apace100.smwyg.tooltip.HorizontalLayoutTooltipComponent;
import io.github.apace100.smwyg.tooltip.ItemStackTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.tools.Tool;
import java.util.List;

@Mixin(value = DrawContext.class)
public class DrawContextMixin {
    private static final TagKey<Item> renderIn3dTag = TagKey.of(RegistryKeys.ITEM, Identifier.of("legendary-tooltips:render_in_3d"));

    @Unique
    private ItemStack smwyg$hoveredStack;

    @Inject(method = "drawHoverEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void smwyg$cacheHoveredStack(TextRenderer textRenderer, Style style, int x, int y, CallbackInfo ci, HoverEvent hoverEvent, HoverEvent.ItemStackContent itemStackContent) {
        ItemStack stack = itemStackContent.asStack();
        if(stack.contains(DataComponentTypes.CUSTOM_DATA)) {
            NbtComponent customDataNbt = stack.get(DataComponentTypes.CUSTOM_DATA);
            if(customDataNbt != null && !customDataNbt.isEmpty() && customDataNbt.copyNbt().getBoolean(ShowMeWhatYouGot.HIDE_STACK_NBT)) {
                return;
            }
        }
        smwyg$hoveredStack = stack;
    }

    //smwyg$hoveredStack.isIn(renderIn3dTag)
    /*@Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("HEAD"))
    private void smwyg$modifyFirstTooltipComponent(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        if(smwyg$hoveredStack == null || smwyg$hoveredStack.isEmpty() || components.isEmpty() || smwyg$hoveredStack.getItem() == null) {
            return;
        }
        System.out.println("1");
        TooltipComponent originalComponent = components.get(0);
        TooltipComponent stackComponent = new ItemStackTooltipComponent(smwyg$hoveredStack);
        TooltipComponent combinedComponent;
        if(textRenderer.isRightToLeft()) {
            System.out.println("2");
            combinedComponent = new HorizontalLayoutTooltipComponent(List.of(originalComponent, stackComponent), 3);
        } else {
            System.out.println("3");
            combinedComponent = new HorizontalLayoutTooltipComponent(List.of(stackComponent, originalComponent), 3);
        }
        components.set(0, combinedComponent);
        System.out.println("4");
        //smwyg$hoveredStack = null;
    }*/

    @ModifyVariable(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true)
    private List<TooltipComponent> smwyg$modifyTooltipComponents(List<TooltipComponent> components) {
        if (smwyg$hoveredStack == null || smwyg$hoveredStack.isEmpty() || components.isEmpty() || smwyg$hoveredStack.isIn(renderIn3dTag)) {
            return components;
        }
        TooltipComponent first = components.get(0);
        TooltipComponent stackComponent = new ItemStackTooltipComponent(smwyg$hoveredStack);
        TooltipComponent combined;
        combined = new HorizontalLayoutTooltipComponent(List.of(stackComponent, first), 3);
        components.set(0, combined);
        smwyg$hoveredStack = null;
        return components;
    }
}
