package dev.atomixsoft.solar_eclipse.core.net.codec.notification;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.EntitySpawn;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

public class EntitySpawnCodec implements PacketCodec<EntitySpawn> {
    @Override
    public void encode(EntitySpawn packet, ByteBuf out) {
        out.writeInt(packet.entityId());
        CodecUtils.writeString(out, packet.name());
        out.writeInt(packet.tileX());
        out.writeInt(packet.tileY());
        out.writeByte(packet.direction());
    }

    @Override
    public EntitySpawn decode(ByteBuf in) {
        int entityId = in.readInt();
        String name = CodecUtils.readString(in);
        int tileX = in.readInt();
        int tileY = in.readInt();
        byte direction = in.readByte();

        return new EntitySpawn(entityId, name, tileX, tileY, direction);
    }
}
