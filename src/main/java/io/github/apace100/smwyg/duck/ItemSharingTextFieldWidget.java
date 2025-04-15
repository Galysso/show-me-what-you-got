package io.github.apace100.smwyg.duck;

import net.minecraft.item.ItemStack;

public interface ItemSharingTextFieldWidget {

    void show_me_what_you_got$setStack(ItemStack stack);
    ItemStack show_me_what_you_got$getStack();
    String show_me_what_you_got$getTextBefore();
    String show_me_what_you_got$getTextAfter();
    boolean show_me_what_you_got$hasStack();
    void show_me_what_you_got$onSuggestionInserted(int start, int offset);
    void show_me_what_you_got$reset();
    int show_me_what_you_got$getInsertionStart();
    int show_me_what_you_got$getInsertionEnd();
}
