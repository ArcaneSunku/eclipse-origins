package dev.atomixsoft.solar_eclipse.client.graphics.ui;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;
import dev.atomixsoft.solar_eclipse.client.graphics.ui.base.Actor;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Button extends Actor {

    public enum State {
        NORMAL,
        HOVERED,
        CLICKED
    }

    private final List<Texture> m_Textures;
    private State m_State;

    public Button(List<Texture> textures) {
        super();
        m_Textures = textures;
        m_State = State.NORMAL;
    }

    public Button(String texture_name) {
        super();
        m_Textures = new ArrayList<>();

        m_Textures.add(AssetLoader.GetTexture(texture_name));
        m_Textures.add(AssetLoader.GetTexture(texture_name + "_hover"));
        m_Textures.add(AssetLoader.GetTexture(texture_name + "_click"));

        m_Size = new Vector2f(100, 75);
        m_State = State.NORMAL;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.render(m_Textures.get(m_State.ordinal()), m_Position, 0, m_Size, new Vector3f(1.0f));
        super.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void onAdd() {

    }

    @Override
    public void onRemove() {

    }
}
