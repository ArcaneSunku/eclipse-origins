package dev.atomixsoft.solar_eclipse.server.database.records;

public record AccountRecord(int id, String username, String passwordHash, long createdAt) {
}
