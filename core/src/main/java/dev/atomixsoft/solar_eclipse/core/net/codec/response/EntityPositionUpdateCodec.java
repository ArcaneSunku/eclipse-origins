package dev.atomixsoft.solar_eclipse.core.net.codec.response;

import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.EntityPositionUpdate;
import io.netty.buffer.ByteBuf;

public class EntityPositionUpdateCodec implements PacketCodec<EntityPositionUpdate> {

    @Override
    public void encode(EntityPositionUpdate packet, ByteBuf out) {
        out.writeInt(packet.entityId());
        out.writeInt(packet.x());
        out.writeInt(packet.y());
        out.writeByte(packet.direction());
        out.writeBoolean(packet.moving());
        out.writeLong(packet.serverTick());
    }

    @Override
    public EntityPositionUpdate decode(ByteBuf in) {
        int entityId = in.readInt();
        int x = in.readInt(), y = in.readInt();
        byte dir = in.readByte();
        boolean moving = in.readBoolean();
        long serverTick = in.readLong();

        return new EntityPositionUpdate(entityId, x, y, dir, moving, serverTick);
    }

}
