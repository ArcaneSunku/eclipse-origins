package git.eclipse.client;

import git.eclipse.core.network.ClientData;
import git.eclipse.core.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class Main {

    private static final String NAME_VAR = "sGameName";
    private static final String USER_VAR = "sUsername", PASS_VAR = "sPassword";
    private static final String SAVE_PASS_VAR = "bSavePass";
    private static final String IP_VAR = "sIP", PORT_VAR = "iPort";
    private static final String MENU_MUSIC_VAR = "sMenuMusic";
    private static final String MUSIC_VAR = "bMusic", SOUND_VAR = "bSound", DEBUG_VAR = "bDebug";

    public static Hashtable<String, String> GetConfigInfo(String configPath) {
        final Hashtable<String, String> configTable = new Hashtable<>();
        final String config = Utils.StringFromFile(configPath);

        final int nameLoc     = config.indexOf(NAME_VAR) + NAME_VAR.length(),
                  userLoc     = config.indexOf(USER_VAR) + USER_VAR.length(),
                  passLoc     = config.indexOf(PASS_VAR) + PASS_VAR.length(),
                  savePassLoc = config.indexOf(SAVE_PASS_VAR) + SAVE_PASS_VAR.length(),
                  ipLoc       = config.indexOf(IP_VAR) + IP_VAR.length(),
                  portLoc     = config.indexOf(PORT_VAR) + PORT_VAR.length(),
                  mainMscLoc  = config.indexOf(MENU_MUSIC_VAR) + MENU_MUSIC_VAR.length(),
                  musicLoc    = config.indexOf(MUSIC_VAR) + MUSIC_VAR.length(),
                  soundLoc    = config.indexOf(SOUND_VAR) + SOUND_VAR.length(),
                  debugLoc    = config.indexOf(DEBUG_VAR) + DEBUG_VAR.length();

        final String clientName     = config.substring(nameLoc + 1, config.indexOf(USER_VAR) - 1),
                     clientUser     = config.substring(userLoc + 1, config.indexOf(PASS_VAR) - 1),
                     clientPass     = config.substring(passLoc + 1, config.indexOf(SAVE_PASS_VAR) - 1),
                     clientSavePass = config.substring(savePassLoc + 1, config.indexOf(IP_VAR) - 1),
                     clientIP       = config.substring(ipLoc + 1, config.indexOf(PORT_VAR) - 1),
                     clientPort     = config.substring(portLoc + 1, config.indexOf(MENU_MUSIC_VAR) - 1),
                     clientMainMsc  = config.substring(mainMscLoc + 1, config.indexOf(MUSIC_VAR) - 1),
                     clientMusic    = config.substring(musicLoc + 1, config.indexOf(SOUND_VAR) - 1),
                     clientSound    = config.substring(soundLoc + 1, config.indexOf(DEBUG_VAR) - 1),
                     clientDebug    = config.substring(debugLoc + 1);

        configTable.put(NAME_VAR, clientName);
        configTable.put(USER_VAR, clientUser);
        configTable.put(PASS_VAR, clientPass);
        configTable.put(SAVE_PASS_VAR, clientSavePass);
        configTable.put(IP_VAR, clientIP);
        configTable.put(PORT_VAR, clientPort);
        configTable.put(MENU_MUSIC_VAR, clientMainMsc);
        configTable.put(MUSIC_VAR, clientMusic);
        configTable.put(SOUND_VAR, clientSound);
        configTable.put(DEBUG_VAR, clientDebug);

        return configTable;
    }

    public static void main(String[] args) {
        final Hashtable<String, String> configInfo = GetConfigInfo("assets/config.ini");
        final ClientData data = new ClientData();

        data.IP = configInfo.get(IP_VAR);
        data.Port = Integer.parseInt(configInfo.get(PORT_VAR));

        data.UserName = configInfo.get(USER_VAR);
        data.Password = configInfo.get(PASS_VAR);

        final EclipseClient eclipse = new EclipseClient(data, configInfo.get(NAME_VAR));
        eclipse.start();
    }

}
