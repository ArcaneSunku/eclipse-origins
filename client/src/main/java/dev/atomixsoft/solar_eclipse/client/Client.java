package dev.atomixsoft.solar_eclipse.client;

import dev.atomixsoft.solar_eclipse.client.config.Configuration;
import dev.atomixsoft.solar_eclipse.client.config.Configuration.SupportedConfigFileTypes;
import dev.atomixsoft.solar_eclipse.client.logging.Logger;
import dev.atomixsoft.solar_eclipse.client.logging.Logger.SupportedLogHandlerTypes;


public class Client {
    public static final Configuration ConfigInfo = new Configuration(SupportedConfigFileTypes.INI, "client/client.ini");

    public static void main(String[] args) {
        Logger logger = new Logger(ConfigInfo.getGameName() + " - Client",
                                   SupportedLogHandlerTypes.ASYNC_CONSOLE,
                                   ConfigInfo.getLogLevel(),
                                   ConfigInfo.getLogPattern());
        final ClientThread eclipse = new ClientThread(ConfigInfo.getGameName(), logger);

        logger.debug("Spinning up threads...");

        eclipse.start();
    }
}
