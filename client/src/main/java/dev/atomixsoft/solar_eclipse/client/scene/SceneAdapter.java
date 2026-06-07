package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.util.input.Controller;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public class SceneAdapter extends SceneHandler.Scene {
    @Override
    public void show() {  }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }

    @Override
    public void update(Controller input, double dt) { }

    @Override
    public void render() { }

    @Override
    public void imgui() { }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void handlePacket(Packet packet) { }
}
