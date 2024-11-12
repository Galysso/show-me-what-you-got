package io.github.apace100.smwyg;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ItemSharingMessage(int start, int end, ItemStack itemStack) implements CustomPayload {

    public static final CustomPayload.Id<ItemSharingMessage> PACKET_ID = new Id<>(Identifier.of(ShowMeWhatYouGot.MODID, "share_item"));

    public static final PacketCodec<RegistryByteBuf, ItemSharingMessage> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ItemSharingMessage::start,
            PacketCodecs.VAR_INT, ItemSharingMessage::end,
            ItemStack.PACKET_CODEC, ItemSharingMessage::itemStack,
            ItemSharingMessage::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
