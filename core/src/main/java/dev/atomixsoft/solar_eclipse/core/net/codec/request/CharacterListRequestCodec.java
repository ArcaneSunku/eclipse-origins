package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.CharacterListRequest;
import io.netty.buffer.ByteBuf;

public class CharacterListRequestCodec implements PacketCodec<CharacterListRequest> {
    @Override
    public void encode(CharacterListRequest packet, ByteBuf out) {

    }

    @Override
    public CharacterListRequest decode(ByteBuf in) {
        return new CharacterListRequest();
    }
}
