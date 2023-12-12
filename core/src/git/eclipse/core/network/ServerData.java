package git.eclipse.core.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerData {

    public String GameName, MotD;
    public InetAddress IP;
    public int Port;

    public ServerData() {
        this("Eclipse Origins", "12.0.0.1", 7001, "Welcome to Eclipse Origins!");
    }

    public ServerData(String gameName, String ip, int port, String motd) {
        try {
            GameName = gameName;
            IP = InetAddress.getByName(ip);
            Port = port;
            MotD = motd;
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public String toString() {
        return String.format("%s <IP %s Port %d>", GameName, IP.getCanonicalHostName(), Port);
    }
}
