package dev.atomixsoft.solar_eclipse.server;

import dev.atomixsoft.solar_eclipse.server.logging.Logger;

import java.util.Scanner;

public class ConsoleThread implements Runnable {

    private final Logger m_Logger;
    private final Runnable m_OnShutdown;

    public ConsoleThread(Logger logger, Runnable shutdownCallback) {
        m_Logger = logger;
        m_OnShutdown = shutdownCallback;
    }

    @Override
    public void run() {
        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.print("> ");
            if(!in.hasNextLine()) continue;

            String cmd = in.nextLine().trim();
            switch (cmd.toLowerCase()) {
                case "exit":
                case "quit":
                    m_OnShutdown.run();
                    return;
                case "help":
                    m_Logger.info("Commands: help, quit, status");
                    break;
                case "status":
                    m_Logger.info("Server running.");
                    break;
                default:
                    m_Logger.warn("Unknown command: " + cmd);
                    break;
            }
        }
    }

}
