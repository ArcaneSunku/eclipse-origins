package dev.atomixsoft.solar_eclipse.server;


import dev.atomixsoft.solar_eclipse.server.config.Configuration;
import dev.atomixsoft.solar_eclipse.server.logging.Logger;

public class Server {
    public static final Configuration ConfigInfo = new Configuration(Configuration.SupportedConfigFileTypes.INI, "server_data/server.ini");
    public static volatile boolean running = true;

    public static void main(String[] args) throws Exception {
        Logger logger = new Logger(ConfigInfo.getName() + " - Server",
                Logger.SupportedLogHandlerTypes.ASYNC_CONSOLE,
                ConfigInfo.getLogLevel(),
                ConfigInfo.getLogPattern());

        logger.debug("Spinning up threads...");

        NettyServer server = new NettyServer(logger);
        server.start(Integer.parseInt(ConfigInfo.getPort()));

        Thread console = new Thread(new ConsoleThread(logger, () -> {
            logger.info("Closing server...");
            running = false;
            server.shutdown();
        }), "Server_Thread");

        console.setDaemon(true);
        console.start();

        while(running) {
            Thread.sleep(1000);
        }
    }
}
