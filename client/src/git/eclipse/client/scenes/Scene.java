package git.eclipse.client.scenes;

public interface Scene {

    void show();
    void hide();
    void dispose();

    void update();
    void render();

    void resize(int width, int height);

}
