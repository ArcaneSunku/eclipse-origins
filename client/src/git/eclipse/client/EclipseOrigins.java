package git.eclipse.client;

import git.eclipse.core.Window;
import git.eclipse.core.graphics.RenderCmd;
import git.eclipse.core.graphics.Shader;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * <p>Represents the Game Thread's main class. Holds the Thread, Window, and essentially the internals of the game.</p>
 */
public class EclipseOrigins implements Runnable {

    private Window m_Window;
    private Thread m_Thread;

    private String m_Title;
    private int m_Width, m_Height;
    private volatile boolean m_Running;

    private Shader m_Shader;

    /**
     * <p>Creates a 640 x 430 window with the title "Eclipse Origins".</p>
     */
    public EclipseOrigins() {
        this("Eclipse Origins", 640, 430);
    }

    /**
     * <p>Creates the Application with a set title, width, and height.</p>
     *
     * @param title the name that will be shown on the Window
     * @param width the width of the Window
     * @param height the height of the Window
     */
    public EclipseOrigins(String title, int width, int height) {
        m_Title = title;
        m_Width = width;
        m_Height = height;
    }

    private void initialize() {
        m_Shader = new Shader("assets/shaders/basic.glsl");
    }

    private void update() {

    }

    private void render() {

    }

    private void dispose() {
        m_Shader.dispose();

        try {
            glfwTerminate();
            glfwSetErrorCallback(null).free();

            m_Thread.join(1);
            System.exit(0);
        } catch (InterruptedException ignored) {
            System.exit(-1);
        }
    }

    /**
     * <p>Starts up the Main Thread and sets everything in motion</p>
     */
    public void start() {
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit())
            throw new IllegalStateException("Failed to initialize GLFW!");

        m_Thread = new Thread(this, "Main_Thread");
        m_Thread.start();
    }

    /**
     * <p>Stops the Main Thread and closes the Window if it isn't closed already.</p>
     */
    public void stop() {
        if(m_Running) {
            if(!m_Window.shouldClose())
                m_Window.close();

            m_Running = false;
        }
    }

    @Override
    public void run() {
        m_Window = new Window(m_Title, m_Width, m_Height);
        m_Window.show();
        m_Running = true;

        RenderCmd.initialize();
        RenderCmd.clearColor(0.1f, 0.1f, 0.1f);

        initialize();
        while(m_Running) {
            if(m_Window.shouldClose()) {
                m_Running = false;
                continue;
            }

            RenderCmd.clear();

            update();
            render();

            m_Window.swapBuffers();
            glfwPollEvents();
        }

        m_Window.dispose();
        dispose();
    }
}
