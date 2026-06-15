package dev.atomixsoft.solar_eclipse.server.net.services.records;

public record AuthenticationResult(boolean success, String message, int accountId) {
}
