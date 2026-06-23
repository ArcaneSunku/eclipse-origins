package dev.atomixsoft.solar_eclipse.server.net.services;

import com.badlogic.ashley.core.Entity;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class SessionService {

    private final Map<Channel, Integer> m_AccountsByChannel;
    private final Map<Channel, Integer> m_CharactersByChannel;

    public SessionService() {
        m_AccountsByChannel = new HashMap<>();
        m_CharactersByChannel = new HashMap<>();
    }

    public void createSession(Channel channel, int accountId) {
        m_AccountsByChannel.put(channel, accountId);
    }

    public void removeSession(Channel channel) {
        m_AccountsByChannel.remove(channel);
        m_CharactersByChannel.remove(channel);
    }

    public void selectCharacter(Channel channel, int characterId) {
        m_CharactersByChannel.put(channel, characterId);
    }

    public Integer getCharacterId(Channel channel) {
        return m_CharactersByChannel.get(channel);
    }

    public Integer getAccountId(Channel channel) {
        return m_AccountsByChannel.get(channel);
    }

    public boolean hasSession(Channel channel) {
        return m_AccountsByChannel.containsKey(channel);
    }

}
