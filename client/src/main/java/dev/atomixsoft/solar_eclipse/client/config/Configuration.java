package dev.atomixsoft.solar_eclipse.client.config;

import dev.atomixsoft.solar_eclipse.core.config.*;


public class Configuration {
    private String NAME_VAR;
    private String USER_VAR, PASS_VAR, SAVE_PASS_VAR;
    private String IP_VAR;
    private Integer PORT_VAR;
    private String MENU_MUSIC_VAR;
    private String MUSIC_VAR, SOUND_VAR;
    private String DEBUG_VAR;

    private String ACTION_VAR, CANCEL_VAR, PICKUP_VAR;
    private String RUN_VAR, UP_VAR, DOWN_VAR, LEFT_VAR, RIGHT_VAR;

    public String LOG_LEVEL_VAR, LOG_PATTERN_VAR;

    private final ConfigurationFile m_ConfigFile;

    public enum SupportedConfigFileTypes {
        INI
    }


    public Configuration(SupportedConfigFileTypes fileType ) {
        switch(fileType) {
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

            NAME_VAR = m_ConfigFile.getValue("client.sGameName");
            USER_VAR = m_ConfigFile.getValue("client.sUsername");
            PASS_VAR = m_ConfigFile.getValue("client.sPassword");
            SAVE_PASS_VAR = m_ConfigFile.getValue("client.bSavePass");
            IP_VAR = m_ConfigFile.getValue("client.sIP");
            PORT_VAR = Integer.parseInt(m_ConfigFile.getValue("client.iPort"));
            MENU_MUSIC_VAR = m_ConfigFile.getValue("client.sMenuMusic");
            MUSIC_VAR = m_ConfigFile.getValue("client.bMusic");
            SOUND_VAR = m_ConfigFile.getValue("client.bSound");
            DEBUG_VAR = m_ConfigFile.getValue("client.bDebug");

            ACTION_VAR = m_ConfigFile.getValue("input.sAction");
            CANCEL_VAR = m_ConfigFile.getValue("input.sCancel");
            PICKUP_VAR = m_ConfigFile.getValue("input.sPickUp");
            RUN_VAR    = m_ConfigFile.getValue("input.sRun");
            UP_VAR     = m_ConfigFile.getValue("input.sUp");
            DOWN_VAR   = m_ConfigFile.getValue("input.sDown");
            LEFT_VAR   = m_ConfigFile.getValue("input.sLeft");
            RIGHT_VAR  = m_ConfigFile.getValue("input.sRight");

            LOG_LEVEL_VAR   = m_ConfigFile.getValue("logging.sLevel");
            LOG_PATTERN_VAR = m_ConfigFile.getValue("logging.sPattern");
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load the configuration file");
        }
    }

    public String getGameName() {
        return NAME_VAR;
    }

    public String getUsername() {
        return USER_VAR;
    }

    public String getPassword() {
        return PASS_VAR;
    }

    public String getSavePass() {
        return SAVE_PASS_VAR;
    }

    public String getIP() {
        return IP_VAR;
    }

    public Integer getPort() {
        return PORT_VAR;
    }

    public String getMenuMusic() {
        return MENU_MUSIC_VAR;
    }

    public String getMusic() {
        return MUSIC_VAR;
    }

    public String getSound() {
        return SOUND_VAR;
    }

    public String getDebug() {
        return DEBUG_VAR;
    }

    public String getActionKey() {
        return ACTION_VAR;
    }

    public String getCancelKey() {
        return CANCEL_VAR;
    }

    public String getPickUpKey() {
        return PICKUP_VAR;
    }

    public String getRunKey() {
        return RUN_VAR;
    }

    public String getUpKey() {
        return UP_VAR;
    }

    public String getDownKey() {
        return DOWN_VAR;
    }

    public String getLeftKey() {
        return LEFT_VAR;
    }

    public String getRightKey() {
        return RIGHT_VAR;
    }

    public String getLogLevel() {
        return LOG_LEVEL_VAR;
    }

    public String getLogPattern() {
        return LOG_PATTERN_VAR;
    }
}
