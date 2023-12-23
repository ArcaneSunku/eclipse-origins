package git.eclipse.core.network.packets;

import git.eclipse.core.network.ClientHandler;
import git.eclipse.core.network.ServerHandler;

public class Packet00Connect extends Packet {

    private final String m_IP;
    private final int m_Port;

    public Packet00Connect(byte[] data) {
        super(00);
        String dataString = readData(data);

        m_IP   = dataString.substring(0, dataString.indexOf(' '));
        m_Port = Integer.parseInt(dataString.substring(m_IP.length() + 1));
    }

    public Packet00Connect(String ip, int port) {
        super(00);

        m_IP = ip;
        m_Port = port;
    }


    @Override
    public void writeData(ClientHandler client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(ServerHandler server) {
        server.sendDataToAll(getData());
    }

    @Override
    public byte[] getData() {
        String combinedData = String.format("00%s %d", m_IP, m_Port);
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
        return String.format("ConnectPacket[\"%s\", \"%d\"]", m_IP, m_Port);
    }
}
