package dev.atomixsoft.solar_eclipse.client.events;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.scene.SceneHandler;
import dev.atomixsoft.solar_eclipse.core.event.interfaces.EventConsumer;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;

import java.util.Locale;

public class ShutdownListener implements EventConsumer<ShutdownEvent> {

    private final ClientThread m_Client;

    public ShutdownListener(ClientThread client) {
        m_Client = client;
    }

    @Override
    public void accept(ShutdownEvent event) {
        if(event.handled) return;

        if(event.isServer()) {
            if(!ClientThread.get_scene_name().toLowerCase(Locale.ROOT).equals("menu"))
                ClientThread.set_scene("Menu");

            event.handled = true;
            return;
        }

        m_Client.stop();
        event.handled = true;
    }

}
