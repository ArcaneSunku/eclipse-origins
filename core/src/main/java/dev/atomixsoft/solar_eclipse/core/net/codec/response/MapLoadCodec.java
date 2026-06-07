package dev.atomixsoft.solar_eclipse.core.net.codec.response;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.MapLoad;
import io.netty.buffer.ByteBuf;

public class MapLoadCodec implements PacketCodec<MapLoad> {
    @Override
    public void encode(MapLoad packet, ByteBuf out) {
        out.writeInt(packet.mapdId());
        out.writeInt(packet.width());
        out.writeInt(packet.height());

        out.writeInt(packet.baseTexId());
        out.writeInt(packet.baseTexX());
        out.writeInt(packet.baseTexY());

        out.writeByte(packet.baseTileType());
    }

    @Override
    public MapLoad decode(ByteBuf in) {
        int mapId = in.readInt();
        int width = in.readInt();
        int height = in.readInt();

        int textureId = in.readInt();
        int textureX = in.readInt();
        int textureY = in.readInt();

        byte tileType = in.readByte();

        return new MapLoad(mapId, width, height, textureId, textureX, textureY, tileType);
    }
}
