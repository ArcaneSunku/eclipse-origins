package dev.atomixsoft.solar_eclipse.client.net;

public class ClientSession {

    private static ClientSession INSTANCE = null;
    private static ClientSession Get() {
        if(INSTANCE == null) {
            INSTANCE = new ClientSession();
            return INSTANCE;
        }

        return INSTANCE;
    }

    private int m_LocalEntityId;
    private int m_MapId;
    private String m_Username;

    private ClientSession() {
        m_LocalEntityId = -1;
        m_MapId = -1;
        m_Username = "";
    }

    public static void Login(String user, int entityId, int map) {
        Get().m_Username = user;
        Get().m_LocalEntityId = entityId;
        Get().m_MapId = map;
    }

    public static void Clear() {
        Get().m_Username = "";
        Get().m_LocalEntityId = -1;
        Get().m_MapId = -1;
    }

    public static int GetLocalEntityId() {
        return Get().m_LocalEntityId;
    }

    public static int GetMapId() {
        return Get().m_MapId;
    }

    public static String GetUsername() {
        return Get().m_Username;
    }

}
