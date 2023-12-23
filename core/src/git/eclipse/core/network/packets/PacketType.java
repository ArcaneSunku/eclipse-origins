package git.eclipse.core.network.packets;

/**
 * Simple enum that represents a "network packet" type. These will help decide what type of packet is being sent/received and what data goes with it.
 */
public enum PacketType {
    INVALID(-1), CONNECT(0), DISCONNECT(1);
    public static PacketType LookupPacket(int id) {
        for(PacketType p : PacketType.values())
            if(id == p.m_PacketId)
                return p;

        return PacketType.INVALID;
    }

    public static PacketType LookupPacket(String id) {
        try {
            return LookupPacket(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            return PacketType.INVALID;
        }
    }

    private final int m_PacketId;
    PacketType(int packetId) {
        m_PacketId = packetId;
    }

    public int getId() {
        return m_PacketId;
    }

    @Override
    public String toString() {
        String type;

        switch(LookupPacket(getId())) {
            case INVALID -> type    = "INVALID";
            case CONNECT -> type    = "CONNECT";
            case DISCONNECT -> type = "DISCONNECT";
            default -> type = "UNKNOWN"; // LookupPacket essentially prevents this
        }

        return String.format("%s_PACKET[%d]", type, m_PacketId);
    }
}
