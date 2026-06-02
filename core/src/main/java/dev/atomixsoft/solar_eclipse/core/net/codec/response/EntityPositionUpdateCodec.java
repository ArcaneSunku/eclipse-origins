package dev.atomixsoft.solar_eclipse.core.net.codec.response;

import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.EntityPositionUpdate;
import io.netty.buffer.ByteBuf;

public class EntityPositionUpdateCodec implements PacketCodec<EntityPositionUpdate> {

    @Override
    public void encode(EntityPositionUpdate packet, ByteBuf out) {
        out.writeInt(packet.entityId());
        out.writeFloat(packet.x());
        out.writeFloat(packet.y());
        out.writeByte(packet.direction().asByte());
        out.writeBoolean(packet.moving());
        out.writeDouble(packet.serverTick());
    }

    @Override
    public EntityPositionUpdate decode(ByteBuf in) {
        int entityId = in.readInt();
        float x = in.readFloat(), y = in.readFloat();
        Direction dir = Direction.Get(in.readByte());
        boolean moving = in.readBoolean();
        double serverTick = in.readDouble();

        return new EntityPositionUpdate(entityId, x, y, dir, moving, serverTick);
    }

}
