package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.CharacterSelectReq;
import io.netty.buffer.ByteBuf;

public class CharacterSelectRequestCodec implements PacketCodec<CharacterSelectReq> {
    @Override
    public void encode(CharacterSelectReq packet, ByteBuf out) {
        out.writeInt(packet.slot());
    }

    @Override
    public CharacterSelectReq decode(ByteBuf in) {
        return new CharacterSelectReq(in.readInt());
    }
}
