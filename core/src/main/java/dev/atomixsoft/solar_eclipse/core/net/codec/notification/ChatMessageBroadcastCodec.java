package dev.atomixsoft.solar_eclipse.core.net.codec.notification;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ChatMessageBroadcast;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

public class ChatMessageBroadcastCodec implements PacketCodec<ChatMessageBroadcast> {

    @Override
    public void encode(ChatMessageBroadcast packet, ByteBuf out) {
        CodecUtils.writeString(out, packet.username());
        CodecUtils.writeString(out, packet.message());
        out.writeInt(packet.timestamp());
    }

    @Override
    public ChatMessageBroadcast decode(ByteBuf in) {
        String user = CodecUtils.readString(in);
        String msg = CodecUtils.readString(in);
        int timestamp = in.readInt();

        return new ChatMessageBroadcast(user, msg, timestamp);
    }

}
