package dev.atomixsoft.solar_eclipse.core.net.packet.response;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record MapLoad(int mapdId, int width, int height, int baseTexId, int baseTexX, int baseTexY, byte baseTileType) implements Packet {
}
