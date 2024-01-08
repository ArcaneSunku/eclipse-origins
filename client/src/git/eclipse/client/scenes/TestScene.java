package git.eclipse.client.scenes;

import git.eclipse.client.AssetLoader;
import git.eclipse.core.graphics.Mesh;
import git.eclipse.core.graphics.Shader;
import git.eclipse.core.graphics.render2D.Sprite;
import git.eclipse.core.graphics.Texture;
import git.eclipse.core.graphics.cameras.OrthoCamera;
import git.eclipse.core.graphics.render2D.SpriteBatch;
import git.eclipse.core.scene.SceneAdapter;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TestScene extends SceneAdapter {

    private OrthoCamera camera;
    private Shader shader;
    private Sprite sprite;
    private Sprite sprite2;
    private Mesh testMesh;

    private SpriteBatch batch;

    @Override
    public void show() {
        AssetLoader.AddTexture("test", "test.png");
        AssetLoader.AddTexture("carrot", "characters/carrot.png");
        AssetLoader.AddShader("basic", "basic");

        shader = AssetLoader.GetShader("basic");
        shader.createUniform("u_ViewProjection");
        shader.createUniform("u_Textures");

        camera = new OrthoCamera(800, 600);

        sprite = new Sprite(AssetLoader.GetTexture("test"));
        sprite.setPosition(0.0f, 0.0f, 0.0f);
        sprite.setSize(64.f, 64.f);

        sprite2 = new Sprite(AssetLoader.GetTexture("carrot"));
        sprite2.setPosition(64.0f, 0.0f, 0.0f);
        sprite2.setScale(2.0f);

        batch = new SpriteBatch(shader);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void update(double dt) {
        // Ignore
    }

    @Override
    public void render() {
        batch.begin(camera);
        sprite.draw(batch);
        sprite2.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(16, 10);
    }

    @Override
    public void dispose() {
        if(shader != null) {
            shader.unbind();
            shader = null;
        }

        if(sprite != null)
            sprite = null;

        if(sprite2 != null)
            sprite2 = null;

        if(testMesh != null)
            testMesh.dispose();

        if(batch != null)
            batch.dispose();
    }

}
