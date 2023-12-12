package git.eclipse.core.network;

public class ServerData {

    public String GameName, IP, MotD;
    public int Port;

    public ServerData() {
        this("Eclipse Origins", "12.0.0.1", 7001, "Welcome to Eclipse Origins!");
    }

    public ServerData(String gameName, String ip, int port, String motd) {
        GameName = gameName;
        IP = ip;
        Port = port;
        MotD = motd;
    }

    @Override
    public String toString() {
        return String.format("%s <IP %s Port %d>", GameName, IP, Port);
    }
}
