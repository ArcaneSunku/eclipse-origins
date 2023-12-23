package git.eclipse.client;

import git.eclipse.client.scenes.SceneHandler;
import git.eclipse.core.Window;
import git.eclipse.core.graphics.RenderCmd;
import git.eclipse.core.network.ClientData;
import git.eclipse.core.network.ClientHandler;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

public class EclipseClient implements Runnable {

    private final ClientData m_Data;
    private final Thread m_Thread;

    private GLFWErrorCallback m_ErrorCallback;
    private ClientHandler m_Client;

    private String m_Title;
    private Window m_Window;

    private SceneHandler m_Scenes;
    private volatile boolean m_Running;

    public EclipseClient(ClientData data, String title) {
        m_Data = data;
        m_Title = title;
        m_Running = false;
        m_Thread = new Thread(this, "Main_Thread");
    }

    public synchronized void start() {
        if(m_Running) return;
        m_ErrorCallback = GLFWErrorCallback.createPrint(System.err);
        if(!glfwInit()) {
            System.err.println("Failed to initialize GLFW!");
            throw new RuntimeException("Failed to initialize the program!");
        }

        m_Client = new ClientHandler(m_Data.IP, m_Data.Port);
        m_Client.start();

        m_Running = true;
        m_Thread.start();
    }

    public synchronized void stop() {
        if(!m_Running) return;
        m_Running = false;
    }

    private void initialize() {
        m_Scenes = new SceneHandler(m_Window);
    }

    private void update() {
        m_Scenes.update();
    }

    private void render() {
        m_Scenes.render();
    }

    private void dispose() {
        try {
            m_Client.stop();

            if(m_Scenes != null)
                m_Scenes.dispose();

            if(!m_Window.shouldClose())
                m_Window.close();

            if(m_ErrorCallback != null) {
                m_ErrorCallback.free();
                glfwSetErrorCallback(null);
            }

            glfwTerminate();
            m_Thread.join(1);
            System.exit(0);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        m_Window = new Window(m_Title, 800, 600);
        m_Window.show();

        RenderCmd.initialize();
        initialize();

        RenderCmd.clearColor(0.25f, 0.25f, 0.25f);
        while(m_Running) {
            if(m_Window.shouldClose() || !m_Client.isConnected()) {
                stop();
                continue;
            }

            update();
            render();

            m_Window.swapBuffers();
            glfwPollEvents();
        }

        dispose();
    }
}
