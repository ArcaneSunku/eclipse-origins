package dev.atomixsoft.solar_eclipse.server.config;

import dev.atomixsoft.solar_eclipse.core.config.ConfigurationFile;
import dev.atomixsoft.solar_eclipse.core.config.INIConfigurationFile;

public class Configuration {

    private String NAME_VAR;
    private String MOTD_VAR;
    private String IP_VAR;
    private String PORT_VAR;

    private final ConfigurationFile m_ConfigFile;

    public String LOG_LEVEL_VAR, LOG_PATTERN_VAR;

    public enum SupportedConfigFileTypes {
        INI
    }

    public Configuration(SupportedConfigFileTypes fileType) {
        switch (fileType) {
            case SupportedConfigFileTypes.INI:
                this.m_ConfigFile = new INIConfigurationFile();

                break;
            default:
                throw new IllegalArgumentException("Unsupported configuration file type");
        }
    }

    public Configuration(SupportedConfigFileTypes fileType, String path) {
        this(fileType);

        try {
            this.m_ConfigFile.load(path);

            this.NAME_VAR = this.m_ConfigFile.getValue("server.sName");
            this.MOTD_VAR = this.m_ConfigFile.getValue("server.sMoTD");
            this.IP_VAR = this.m_ConfigFile.getValue("server.sIP");
            this.PORT_VAR = this.m_ConfigFile.getValue("server.iPort");

            this.LOG_LEVEL_VAR = this.m_ConfigFile.getValue("logging.sLevel");
            this.LOG_PATTERN_VAR = this.m_ConfigFile.getValue("logging.sPattern");

        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load the configuration file");
        }
    }

    public String getName() {
        return this.NAME_VAR;
    }

    public String getMoTD() {
        return this.MOTD_VAR;
    }

    public String getIP() {
        return this.IP_VAR;
    }

    public String getPort() {
        return this.PORT_VAR;
    }

    public String getLogLevel() {
        return this.LOG_LEVEL_VAR;
    }

    public String getLogPattern() {
        return this.LOG_PATTERN_VAR;
    }

}
