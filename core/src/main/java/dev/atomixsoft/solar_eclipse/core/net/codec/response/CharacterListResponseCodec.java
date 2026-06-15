package dev.atomixsoft.solar_eclipse.core.net.codec.response;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.CharacterListResponse;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.CharacterSummaryPacket;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class CharacterListResponseCodec implements PacketCodec<CharacterListResponse> {
    @Override
    public void encode(CharacterListResponse packet, ByteBuf out) {
        out.writeInt(packet.characters().size());

        for(CharacterSummaryPacket character : packet.characters()) {
            out.writeInt(character.slot());
            CodecUtils.writeString(out, character.name());
            out.writeInt(character.level());
            out.writeInt(character.spriteId());
        }
    }

    @Override
    public CharacterListResponse decode(ByteBuf in) {
        int count = in.readInt();

        List<CharacterSummaryPacket> characters = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            characters.add(new CharacterSummaryPacket(in.readInt(),
                    CodecUtils.readString(in),
                    in.readInt(), in.readInt()));
        }

        return new CharacterListResponse(characters);
    }
}
