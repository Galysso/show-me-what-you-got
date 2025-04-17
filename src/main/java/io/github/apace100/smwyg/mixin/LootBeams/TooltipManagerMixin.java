package io.github.apace100.smwyg.mixin.LootBeams;

import com.lootbeams.managers.TooltipManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipManager.class)
public class TooltipManagerMixin {
    @Inject(method = "canRenderTooltips", at = @At("HEAD"), cancellable = true)
    private static void canRenderTooltips(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
