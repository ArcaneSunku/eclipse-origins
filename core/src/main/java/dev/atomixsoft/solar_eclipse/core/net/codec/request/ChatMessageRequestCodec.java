package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.ChatMessageRequest;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

public class ChatMessageRequestCodec implements PacketCodec<ChatMessageRequest> {
    @Override
    public void encode(ChatMessageRequest packet, ByteBuf out) {
        CodecUtils.writeString(out, packet.username());
        CodecUtils.writeString(out, packet.message());
    }

    @Override
    public ChatMessageRequest decode(ByteBuf in) {
        String user = CodecUtils.readString(in);
        String msg = CodecUtils.readString(in);

        return new ChatMessageRequest(user, msg);
    }
}
