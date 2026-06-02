package dev.atomixsoft.solar_eclipse.server.console.events;

import dev.atomixsoft.solar_eclipse.core.event.interfaces.EventConsumer;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ChatMessageBroadcast;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ShutdownNotification;
import dev.atomixsoft.solar_eclipse.server.Server;

public class CommandListener implements EventConsumer<CommandEvent> {

    @Override
    public void accept(CommandEvent event) {
        String raw = event.getCommand().trim();

        String[] parts = raw.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "say" -> {
                if(args.isBlank()) {
                    System.out.println("Usage: say <message>");
                    return;
                }

                String msg = args;

                Server.network().broadcast(new ChatMessageBroadcast("Server", msg, (int) System.currentTimeMillis())).syncUninterruptibly();
                System.out.println("[Server] " + msg);
            }

            case "help" -> {
                System.out.println("Commands: help, quit, status");
            }

            case "status" -> {
                System.out.println("Server running.");
            }

            case "exit", "quit" -> {
                System.out.println("Shutting down server...");
                Server.network().broadcast(new ShutdownNotification("Shutdown Command", 0)).syncUninterruptibly();

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Server.event_bus().post(new ShutdownEvent("Server", true));
            }

            default -> {
                System.out.println("Unknown command: " + cmd);
            }
        }
    }

}
