package io.github.apace100.smwyg.mixin.Minecraft;

import io.github.apace100.smwyg.ShowMeWhatYouGot;
import io.github.apace100.smwyg.SmwygItemMatch;
import io.github.apace100.smwyg.duck.ItemSharingTextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin implements ItemSharingTextFieldWidget {

    @Shadow private String text;

    @Shadow private int selectionStart;
    @Shadow private int selectionEnd;

    @Shadow public abstract String getText();

    @Unique
    private ItemStack itemStack;
    @Unique
    private String insertedString;
    @Unique
    private int insertedLength;
    @Unique
    private int insertedIndex = 0;

    @Override
    public void show_me_what_you_got$setStack(ItemStack stack) {
        this.itemStack = stack;
        Text text = stack.toHoverableText();
        this.insertedString = text.getString();
        this.insertedLength = insertedString.length();
    }

    @Override
    public ItemStack show_me_what_you_got$getStack() {
        return this.itemStack;
    }

    @Override
    public String show_me_what_you_got$getTextBefore() {
        return this.text.substring(0, insertedIndex);
    }

    @Override
    public String show_me_what_you_got$getTextAfter() {
        return this.text.substring(insertedIndex + insertedLength);
    }

    @Override
    public boolean show_me_what_you_got$hasStack() {
        return this.itemStack != null;
    }

    @Override
    public void show_me_what_you_got$onSuggestionInserted(int start, int offset) {
        if(this.itemStack != null && start <= insertedIndex) {
            this.insertedIndex += offset;
        }
    }

    @Inject(method = "getCursorPosWithOffset", at = @At("RETURN"), cancellable = true)
    private void smwyg$modifyCursorOffset(int offset, CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValue();
        if(original > insertedIndex && original < insertedIndex + insertedLength) {
            if(offset < 0) {
                cir.setReturnValue(insertedIndex);
            } else {
                cir.setReturnValue(insertedIndex + insertedLength);
            }
        }
    }

    @Inject(method = "write",
            at = @At(
                    value = "HEAD"))
    private void smwyg$removeStackWhenOverwritten(String text, CallbackInfo ci) {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        if(i <= insertedIndex && j > insertedIndex) {
            this.text = this.text.substring(0, insertedIndex) + this.text.substring(insertedIndex + insertedLength);
            if(this.selectionStart > this.selectionEnd) {
                this.selectionStart -= insertedLength;
            } else {
                this.selectionEnd -= insertedLength;
            }
            show_me_what_you_got$reset();
        }
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setSelectionStart(I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void smwyg$moveInsertedText(String text, CallbackInfo ci, int i, int j, int k, String string, int l, String string2) {
        if(i <= insertedIndex && j <= insertedIndex) {
            this.insertedIndex += l;
        }
    }

    @ModifyVariable(method = "setCursor", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int smwyg$modifyCursorSetting(int original) {
        if(original > insertedIndex && original < insertedIndex + insertedLength) {
            if(original <= insertedIndex + (insertedLength / 2)) {
                return insertedIndex;
            } else {
                return insertedIndex + insertedLength;
            }
        } else {
            return original;
        }
    }

    @ModifyVariable(method = "setSelectionStart", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int smwyg$modifySelectionStart(int original) {
        if(original > insertedIndex && original < insertedIndex + insertedLength) {
            return insertedIndex;
        } else {
            return original;
        }
    }

    @Inject(method = "eraseCharactersTo", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;<init>(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void smwyg$eraseInsertion(int characterOffset, CallbackInfo ci, int i, int j) {
        if(i <= insertedIndex && j >= insertedIndex + insertedLength) {
            show_me_what_you_got$reset();
        } else {
            if(i <= insertedIndex || j <= insertedIndex) {
                this.insertedIndex -= (j - i);
            }
        }
    }

    @ModifyVariable(method = "setSelectionEnd", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int smwyg$modifySelectionEnd(int original) {
        if(original > insertedIndex && original < insertedIndex + insertedLength) {
            return insertedIndex;
        } else {
            return original;
        }
    }

    @ModifyVariable(method = "setText", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String smwyg$handleItemSetting(String text) {
        if(show_me_what_you_got$hasStack()) {
            return text;
        }
        if(ShowMeWhatYouGot.hasSmwygItem(text)) {
            SmwygItemMatch itemMatch = ShowMeWhatYouGot.extractItem(text);
            if (itemMatch == null) {
                show_me_what_you_got$reset();
            } else {
                String before = text.substring(0, itemMatch.start);
                String after = text.substring(itemMatch.end);
                if (itemMatch.stack == null) {
                    text = before + I18n.translate("smwyg.chat.stale_link") + after;
                    show_me_what_you_got$reset();
                } else {
                    String itemText = itemMatch.stack.toHoverableText().getString();
                    text = before + itemText + after;
                    itemStack = itemMatch.stack;
                    insertedString = itemText;
                    insertedIndex = itemMatch.start;
                    insertedLength = itemText.length();
                }
            }
        } else {
            show_me_what_you_got$reset();
        }
        return text;
    }

    @Override
    public void show_me_what_you_got$reset() {
        itemStack = null;
        insertedString = "";
        insertedIndex = 0;
        insertedLength = 0;
    }

    @Override
    public int show_me_what_you_got$getInsertionStart() {
        return insertedIndex;
    }

    @Override
    public int show_me_what_you_got$getInsertionEnd() {
        return insertedIndex + insertedLength;
    }
}
