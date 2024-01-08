package git.eclipse.client;

import git.eclipse.client.scenes.MainScene;
import git.eclipse.core.scene.SceneHandler;
import git.eclipse.client.scenes.TestScene;
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
        m_Scenes.addScene("Test", new TestScene());
        m_Scenes.addScene("Main", new MainScene());

        m_Scenes.setActiveScene("Test");
    }

    private void dispose() {
        try {
            m_Client.stop();

            if(m_Scenes != null)
                m_Scenes.dispose();

            AssetLoader.Dispose();
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
        RenderCmd.clearColor(0.05f, 0.05f, 0.05f);

        m_Scenes = new SceneHandler(m_Window);
        initialize();

        double accumulator = 0.0;
        double optimal = 1.0 / 60.0;
        double currentTime = System.nanoTime() / 1e9;

        while(m_Running) {
            RenderCmd.clear();
            if(m_Window.shouldClose()) {
                stop();
                continue;
            }

            if(m_Window.hasResized() && m_Window.isResizable()) {
                m_Scenes.resize(m_Window.getWidth(), m_Window.getHeight());
                m_Window.setResized(false);
            }

            double newTime = System.nanoTime() / 1e9;
            double frameTime = newTime - currentTime;
            currentTime = newTime;

            accumulator += frameTime;
            while(accumulator >= optimal) {
                m_Scenes.update(optimal);
                accumulator -= optimal;
            }

            m_Scenes.render();

            m_Window.swapBuffers();
            glfwPollEvents();

            if(!m_Window.vSyncEnabled())
                sleep(currentTime);
        }

        dispose();
    }

    private void sleep(double currentTime) {
        try {
            double desiredTime = 1.0 / 60.0;
            long sleepTime = (long) ((currentTime - System.nanoTime() + desiredTime) / 1e9);
            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
