package io.github.apace100.smwyg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowMeWhatYouGot implements ModInitializer {

	public static final String MODID = "smwyg";
	public static final Logger LOGGER = LogManager.getLogger(ShowMeWhatYouGot.class);

	// Add a 'true' value to an item stack's custom data NBT with this key to prevent
	// SMWYG's custom tooltip component when sharing.
	public static final String HIDE_STACK_NBT = MODID + ":hide_stack";

	public static final MessageItemList MESSAGE_ITEM_LIST = new MessageItemList();

	@Override
	public void onInitialize() {

		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.CONTENT_PHASE, ((sender, message) -> {
			Optional<SmwygItemMatch> itemMatch = MESSAGE_ITEM_LIST.get(sender);
			if(itemMatch.isEmpty()) {
				return message;
			}
			return decorateMessage(message, itemMatch.get());
		}));

		PayloadTypeRegistry.playC2S().register(ItemSharingMessage.PACKET_ID, ItemSharingMessage.PACKET_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ItemSharingMessage.PACKET_ID, (payload, context) -> {
			int start = payload.start();
			int end = payload.end();
			ItemStack stack = payload.itemStack();
			SmwygItemMatch itemMatch = new SmwygItemMatch();
			itemMatch.stack = stack;
			itemMatch.start = start;
			itemMatch.end = end;
			MESSAGE_ITEM_LIST.add(context.player(), itemMatch);
		});
	}

	private static final Pattern ITEM_REGEX = Pattern.compile("\\[\\[smwyg:(?<item>.*)\\]\\]");

	public static boolean hasSmwygItem(String text) {
		Matcher matcher = ITEM_REGEX.matcher(text);
		return matcher.find();
	}

	public static SmwygItemMatch extractItem(String text) {
		Matcher matcher = ITEM_REGEX.matcher(text);
		if(!matcher.find()) {
			return null;
		}
		String itemNbt = matcher.group("item");
		int start = matcher.start();
		int end = matcher.end();
		SmwygItemMatch itemMatch = new SmwygItemMatch();
		itemMatch.start = start;
		itemMatch.end = end;
		itemMatch.stack = ShowMeWhatYouGotClient.stackFromString(itemNbt);
		return itemMatch;
	}

	public static Text decorateMessage(Text message, SmwygItemMatch itemMatch) {
		if(itemMatch == null || itemMatch.stack == null) {
			return message;
		}
		String msgContent = message.getString();
		MutableText resultText = Text.literal(msgContent.substring(0, itemMatch.start));
		resultText.append(itemMatch.stack.toHoverableText());
		resultText.append(Text.literal(msgContent.substring(itemMatch.end)));
		return resultText;
	}
}
