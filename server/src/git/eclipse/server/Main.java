package git.eclipse.server;

import git.eclipse.core.utils.Utils;

public class Main {

    private static final String NAME_VAR = "sName";
    private static final String MOTD_VAR = "sMoTD";
    private static final String IP_VAR = "sIP";
    private static final String PORT_VAR = "iPort";

    public static void main(String[] args) {
        final String config = Utils.StringFromFile("data/config.ini");

        final int nameLoc = config.indexOf(NAME_VAR) + NAME_VAR.length(),
        motdLoc = config.indexOf(MOTD_VAR) + MOTD_VAR.length(),
        ipLoc = config.indexOf(IP_VAR) + IP_VAR.length(),
        portLoc = config.indexOf(PORT_VAR) + PORT_VAR.length();

        final String serverName = config.substring(nameLoc + 1, config.indexOf(MOTD_VAR) - 1);
        final String serverMoTD = config.substring(motdLoc + 1, config.indexOf(IP_VAR) - 1);
        final String serverIP = config.substring(ipLoc + 1, config.indexOf(PORT_VAR) - 1);
        final int serverPort = Integer.parseInt(config.substring(portLoc + 1, portLoc + 5));

        final Server server = new Server(serverName, serverMoTD, serverIP, serverPort);
        server.start();
    }

}
