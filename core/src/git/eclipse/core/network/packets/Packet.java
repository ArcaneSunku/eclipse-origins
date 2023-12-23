package git.eclipse.core.network.packets;

import git.eclipse.core.network.ClientHandler;
import git.eclipse.core.network.ServerHandler;

public abstract class Packet {

    protected final byte m_PacketId;

    public Packet(int id) {
        m_PacketId = (byte) id;
    }

    public abstract void writeData(ClientHandler client);
    public abstract void writeData(ServerHandler server);

    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public abstract byte[] getData();

    public PacketType getType() {
        return PacketType.LookupPacket(m_PacketId);
    }

}
