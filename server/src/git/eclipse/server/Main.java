package git.eclipse.server;

import git.eclipse.core.network.ServerData;
import git.eclipse.core.utils.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class Main {

    private static final String NAME_VAR = "sName", MOTD_VAR = "sMoTD";
    private static final String IP_VAR = "sIP", PORT_VAR = "iPort";

    public static Hashtable<String, String> GetConfigInfo(String configPath) {
        final Hashtable<String, String> configTable = new Hashtable<>();
        final String config = Utils.StringFromFile(configPath);

        final int nameLoc = config.indexOf(NAME_VAR) + NAME_VAR.length(),
                  motdLoc = config.indexOf(MOTD_VAR) + MOTD_VAR.length(),
                  ipLoc   = config.indexOf(IP_VAR) + IP_VAR.length(),
                  portLoc = config.indexOf(PORT_VAR) + PORT_VAR.length();

        final String serverName = config.substring(nameLoc + 1, config.indexOf(MOTD_VAR) - 1),
                     serverMoTD = config.substring(motdLoc + 1, config.indexOf(IP_VAR) - 1),
                     serverIP   = config.substring(ipLoc + 1,   config.indexOf(PORT_VAR) - 1),
                     serverPort = config.substring(portLoc + 1, portLoc + 5);

        configTable.put(NAME_VAR, serverName);
        configTable.put(MOTD_VAR, serverMoTD);
        configTable.put(IP_VAR, serverIP);
        configTable.put(PORT_VAR, serverPort);

        return configTable;
    }

    public static void main(String[] args) {
        final ServerData data = new ServerData();
        final Hashtable<String, String> configInfo = GetConfigInfo("data/config.ini");

        try {
            data.GameName = configInfo.get(NAME_VAR);
            data.MotD     = configInfo.get(MOTD_VAR);
            data.IP       = InetAddress.getByName(configInfo.get(IP_VAR));
            data.Port     = Integer.parseInt(configInfo.get(PORT_VAR));
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        final EclipseServer server = new EclipseServer(data);
        server.start();
    }

}
