package dev.atomixsoft.solar_eclipse.client.graphics.ui.base;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.graphics.cameras.OrthoCamera;
import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;

import org.jetbrains.annotations.NotNull;

/**
 * <p>The mainstay for GUI rendering. Naming conventions are inspired (lifted) from libGDX, implementation is not the same.<br>
 * <br>
 * This class will handle all GUI elements known as "Actors". Responsible for how/when they are rendered and how they interact.</p>
 */
public class Stage {

    private final OrthoCamera m_Camera;
    private final SpriteBatch m_Batch;

    private Group m_Root;

    public Stage(int width, int height) {
        m_Camera = new OrthoCamera(width, height);
        m_Camera.setZoom(1.0f);

        m_Batch = new SpriteBatch(AssetLoader.GetShader("basic"));

        m_Root = new Group();
    }

    public void draw() {
        m_Camera.update();

        m_Batch.begin(m_Camera);
        m_Root.draw(m_Batch);
        m_Batch.end();
    }

    public void act(float delta) {
        m_Root.act(delta);
    }

    public void resize(int width, int height) {
        m_Camera.resize(width, height);
    }

    public void setRoot(@NotNull Group root) {
        m_Root = root;
    }

    public Group getRoot() {
        return m_Root;
    }

}
