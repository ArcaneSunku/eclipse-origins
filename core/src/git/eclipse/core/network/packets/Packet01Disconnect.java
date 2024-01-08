package git.eclipse.core.network.packets;

import git.eclipse.core.network.ClientHandler;
import git.eclipse.core.network.ServerHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Packet01Disconnect extends Packet {

    private final String m_IP;
    private final int m_Port;

    public Packet01Disconnect(byte[] data) {
        super(01);
        String dataString = readData(data);

        m_IP   = dataString.substring(0, dataString.indexOf(' '));
        m_Port = Integer.parseInt(dataString.substring(m_IP.length() + 1));
    }

    public Packet01Disconnect(String ip, int port) {
        super(1);

        m_IP = ip;
        m_Port = port;
    }

    @Override
    public byte[] getData() {
        String combinedData = String.format("01%s %d", m_IP, m_Port);
        return combinedData.getBytes();
    }

    public String getIP() {
        return m_IP;
    }

    public int getPort() {
        return m_Port;
    }

    @Override
    public String toString() {
        return String.format("DisconnectPacket[\"%s\", \"%d\"]", m_IP, m_Port);
    }
}
