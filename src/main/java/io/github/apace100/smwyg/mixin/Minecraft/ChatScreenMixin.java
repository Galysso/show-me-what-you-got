package io.github.apace100.smwyg.mixin.Minecraft;

import io.github.apace100.smwyg.ShowMeWhatYouGotClient;
import io.github.apace100.smwyg.duck.ItemSharingTextFieldWidget;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Shadow protected TextFieldWidget chatField;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setText(Ljava/lang/String;)V"))
    private void setItemSharingText(CallbackInfo ci) {
        if(ShowMeWhatYouGotClient.sharingItem != null) {
            if(this.chatField instanceof ItemSharingTextFieldWidget istfw) {
                istfw.show_me_what_you_got$setStack(ShowMeWhatYouGotClient.sharingItem);
                ShowMeWhatYouGotClient.sharingItem = null;
            }
        }
    }

    @Inject(method = "setChatFromHistory", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setText(Ljava/lang/String;)V"))
    private void removeSetItem(int offset, CallbackInfo ci) {
        if(this.chatField instanceof ItemSharingTextFieldWidget istfw) {
            istfw.show_me_what_you_got$reset();
        }
    }

    @ModifyVariable(method = "sendMessage", at = @At("HEAD"), argsOnly = true)
    private boolean smwyg$sendItemSharingMessage(boolean addToHistory) {
        if(this.chatField instanceof ItemSharingTextFieldWidget istfw) {
            if(!istfw.show_me_what_you_got$hasStack()) {
                return addToHistory;
            }
            String before = istfw.show_me_what_you_got$getTextBefore();
            ItemStack stack = istfw.show_me_what_you_got$getStack();
            String after = istfw.show_me_what_you_got$getTextAfter();

            // Add SMWYG message to chat message history
            if(addToHistory) {
                String stackString = "[[smwyg:" + ShowMeWhatYouGotClient.stackToString(stack) + "]]";
                this.client.inGameHud.getChatHud().addToMessageHistory(before + stackString + after);
            }

            // Inform server about shared item
            ShowMeWhatYouGotClient.sendItemSharingMessage(istfw.show_me_what_you_got$getInsertionStart(), istfw.show_me_what_you_got$getInsertionEnd(), stack);

            // Prevent vanilla from adding message to chat message history
            return false;
        }
        return addToHistory;
    }
}
