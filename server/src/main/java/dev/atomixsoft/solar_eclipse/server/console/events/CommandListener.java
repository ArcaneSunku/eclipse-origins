package dev.atomixsoft.solar_eclipse.server.console.events;

import dev.atomixsoft.solar_eclipse.core.event.interfaces.EventConsumer;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;
import dev.atomixsoft.solar_eclipse.server.Server;

public class CommandListener implements EventConsumer<CommandEvent> {

    @Override
    public void accept(CommandEvent event) {
        String cmd = event.getCommand().toLowerCase();

        switch (cmd) {
            case "exit", "quit" -> {
                System.out.println("Shutting down server...");
                Server.event_bus().post(new ShutdownEvent("Server", true));
            }

            case "help" -> {
                System.out.println("Commands: help, quit, status");
            }

            case "status" -> {
                System.out.println("Server running.");
            }

            default -> {
                System.out.println("Unknown command: " + cmd);
            }
        }
    }

}
