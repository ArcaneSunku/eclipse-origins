package git.eclipse.core.scene;

public abstract class Scene {

    public abstract void show();
    public abstract void hide();
    public abstract void dispose();

    public abstract void update(double dt);
    public abstract void render();

    public abstract void resize(int width, int height);

}
