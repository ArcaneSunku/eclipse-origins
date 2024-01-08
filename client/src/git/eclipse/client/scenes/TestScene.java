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

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TestScene extends SceneAdapter {

    private List<Sprite> spriteList;
    private OrthoCamera camera;
    private SpriteBatch batch;

    @Override
    public void show() {
        spriteList = new ArrayList<>();
        camera = new OrthoCamera(600, 400);

        AssetLoader.AddShader("basic", "basic");

        AssetLoader.AddTexture("test", "test.png");
        AssetLoader.AddTexture("tileset1", "tilesets/1.png");
        AssetLoader.AddTexture("tileset2", "tilesets/2.png");

        for(int y = 8; y > -10; y--) {
            for(int x = -10; x < 10; x++) {
                Sprite sprite = new Sprite(AssetLoader.GetTexture("tileset1"));
                sprite.setPosition(x * 32, y * 32, 0);
                sprite.setSize(32, 32);

                sprite.setCellPos(new Vector2f(0, 32));
                sprite.setCellSize(new Vector2f(32, 32));
                spriteList.add(sprite);
            }
        }

        Sprite sprite = new Sprite(AssetLoader.GetTexture("tileset2"));
        sprite.setSize(32, 32);
        sprite.setCellPos(new Vector2f(32, 32 * 6));
        sprite.setCellSize(new Vector2f(32, 32));

        spriteList.add(sprite);
        batch = new SpriteBatch(AssetLoader.GetShader("basic"));
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void update(double dt) {
        Sprite sprite = spriteList.get(spriteList.size()-1);
        Vector3f pos = sprite.getPosition();



        sprite.setPosition(pos.x, pos.y, pos.z);
    }

    @Override
    public void render() {
        batch.begin(camera);

        for(Sprite sprite : spriteList)
            sprite.draw(batch);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(600, 400);
    }

    @Override
    public void dispose() {
        if(spriteList != null) {
            spriteList.clear();
            spriteList = null;
        }

        if(batch != null)
            batch.dispose();
    }

}
