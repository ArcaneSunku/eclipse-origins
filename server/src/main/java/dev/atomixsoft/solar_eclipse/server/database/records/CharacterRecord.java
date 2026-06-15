package dev.atomixsoft.solar_eclipse.server.database.records;

public record CharacterRecord(int id, int accountId, int slot,
                              String name, int textureId, byte sex, int mapId, int x, int y, int level,
                              int health, int maxHealth, int spirit, int maxSpirit, int experience, int maxExperience,
                              int gold, long createdAt) {
}
