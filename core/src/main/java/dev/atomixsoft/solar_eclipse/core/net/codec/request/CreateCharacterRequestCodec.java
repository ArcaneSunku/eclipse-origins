package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.CreateCharacterRequest;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

public class CreateCharacterRequestCodec implements PacketCodec<CreateCharacterRequest> {
    @Override
    public void encode(CreateCharacterRequest packet, ByteBuf out) {
        out.writeInt(packet.slot());
        CodecUtils.writeString(out, packet.name());
        out.writeInt(packet.classId());
        out.writeByte(packet.sex());
        out.writeByte(packet.spriteId());
    }

    @Override
    public CreateCharacterRequest decode(ByteBuf in) {
        int slot = in.readInt();
        String name = CodecUtils.readString(in);
        int classId = in.readInt();
        byte sex = in.readByte();
        byte spriteId = in.readByte();

        return new CreateCharacterRequest(slot, name, classId, sex, spriteId);
    }
}
