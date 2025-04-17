package io.github.apace100.smwyg;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class AutoChatScreen extends ChatScreen {
    public AutoChatScreen(String originalChatText) {
        super(originalChatText);
    }

    public TextFieldWidget getChatField() {
        return this.chatField;
    }
}
