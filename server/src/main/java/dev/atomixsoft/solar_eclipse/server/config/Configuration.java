package dev.atomixsoft.solar_eclipse.server.config;

import dev.atomixsoft.solar_eclipse.core.config.ConfigurationFile;
import dev.atomixsoft.solar_eclipse.core.config.INIConfigurationFile;

public class Configuration {

    private String NAME_VAR;
    private String MOTD_VAR;
    private String IP_VAR;
    private Integer PORT_VAR;
    private String DEBUG_VAR;

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
            m_ConfigFile.load(path);

            NAME_VAR  = m_ConfigFile.getValue("server.sName");
            MOTD_VAR  = m_ConfigFile.getValue("server.sMoTD");
            IP_VAR    = m_ConfigFile.getValue("server.sIP");
            PORT_VAR  = Integer.parseInt(m_ConfigFile.getValue("server.iPort"));
            DEBUG_VAR = m_ConfigFile.getValue("server.debug");

            LOG_LEVEL_VAR   = m_ConfigFile.getValue("logging.sLevel");
            LOG_PATTERN_VAR = m_ConfigFile.getValue("logging.sPattern");

        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load the configuration file");
        }
    }

    public String getName() {
        return NAME_VAR;
    }

    public String getMoTD() {
        return MOTD_VAR;
    }

    public String getIP() {
        return IP_VAR;
    }

    public Integer getPort() {
        return PORT_VAR;
    }

    public String getDebug() {
        return DEBUG_VAR;
    }

    public String getLogLevel() {
        return LOG_LEVEL_VAR;
    }

    public String getLogPattern() {
        return LOG_PATTERN_VAR;
    }

}
