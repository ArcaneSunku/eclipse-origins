package git.eclipse.client;

import git.eclipse.core.Window;
import git.eclipse.core.graphics.RenderCmd;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

public class Main {

    private void update() {

    }

    private void render() {

    }

    public static void main(String[] args) {
        final Main main = new Main();
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit())
            throw new IllegalStateException("Failed to initialize GLFW!");

        Window window = new Window("Eclipse Origins", 800, 600);
        window.show();

        RenderCmd.initialize();
        RenderCmd.clearColor(0.2f, 0.2f, 0.2f);

        boolean running = true;
        while(running) {
            if(window.shouldClose()) {
                running = false;
                continue;
            }

            RenderCmd.clear();

            main.update();
            main.render();

            window.swapBuffers();
            glfwPollEvents();
        }

        window.dispose();
        glfwTerminate();

        glfwSetErrorCallback(null).free();
    }

}
