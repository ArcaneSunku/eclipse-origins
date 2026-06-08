package dev.atomixsoft.solar_eclipse.server.database.records;

public record CharacterRecord(int id, int accountId, int slot,
                              String name, int textureId, byte sex, int mapId, int x, int y, int gold, long createdAt) {
}
