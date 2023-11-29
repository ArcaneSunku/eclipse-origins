package git.eclipse.core.utils.connect;

public class ServerData {

    public String GameName, IP, MotD;
    public int Port;

    public ServerData() {
        GameName = "Eclipse Origins";
        IP = "127.0.0.1";
        Port = 7001;
        MotD = "Welcome to Eclipse Origins!";
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
