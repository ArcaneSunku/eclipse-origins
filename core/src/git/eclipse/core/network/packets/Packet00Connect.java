package git.eclipse.core.network.packets;

import git.eclipse.core.network.ClientHandler;
import git.eclipse.core.network.ServerHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Packet00Connect extends Packet {

    private final String m_IP;
    private final int m_Port;

    public Packet00Connect(byte[] data) {
        super(0);
        String[] dataArray = readData(data).split(" ");

        m_IP   = dataArray[0];
        m_Port = Integer.parseInt(dataArray[1]);
    }

    public Packet00Connect(String ip, int port) {
        super(00);

        m_IP = ip;
        m_Port = port;
    }

    @Override
    public byte[] getData() {
        String combinedData = String.format("00%s %d", m_IP, m_Port);
        return combinedData.getBytes();
    }

    public InetAddress getIP() {
        try {
            return InetAddress.getByName(m_IP);
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public int getPort() {
        return m_Port;
    }

    @Override
    public String toString() {
        return String.format("ConnectPacket[\"%s\", \"%d\"]", m_IP, m_Port);
    }
}
