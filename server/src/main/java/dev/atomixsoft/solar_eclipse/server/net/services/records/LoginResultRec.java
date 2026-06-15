package dev.atomixsoft.solar_eclipse.server.net.services.records;

public record LoginResultRec(boolean success, String message, int accountId, String username) {
}
