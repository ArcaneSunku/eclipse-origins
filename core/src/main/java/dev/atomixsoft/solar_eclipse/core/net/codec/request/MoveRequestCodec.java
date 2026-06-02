package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.MoveRequest;
import io.netty.buffer.ByteBuf;

public class MoveRequestCodec implements PacketCodec<MoveRequest> {
    @Override
    public void encode(MoveRequest packet, ByteBuf out) {
        out.writeInt(packet.entityId());
        out.writeByte(packet.direction().asByte());
        out.writeInt(packet.sequence());
    }

    @Override
    public MoveRequest decode(ByteBuf in) {
        int id = in.readInt();
        Direction dir = Direction.Get(in.readByte());
        int sequence = in.readInt();

        return new MoveRequest(id, dir, sequence);
    }
}
