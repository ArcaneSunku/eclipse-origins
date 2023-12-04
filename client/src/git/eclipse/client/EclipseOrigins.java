package git.eclipse.client;

import git.eclipse.core.Window;
import git.eclipse.core.graphics.Mesh;
import git.eclipse.core.graphics.RenderCmd;
import git.eclipse.core.graphics.Shader;
import git.eclipse.core.graphics.Texture;
import git.eclipse.core.graphics.cameras.OrthoCamera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * <p>Represents the Game Thread's main class. Holds the Thread, Window, and essentially the internals of the game.</p>
 */
public class EclipseOrigins implements Runnable {

    private GLFWErrorCallback m_ErrorCallback;
    private Window m_Window;
    private Thread m_Thread;

    private String m_Title;
    private int m_Width, m_Height;
    private volatile boolean m_Running;

    private OrthoCamera m_Camera;
    private Mesh m_Mesh;
    private Shader m_Shader;
    private Texture m_Texture;

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
        m_Camera = new OrthoCamera(m_Width, m_Height);
        List<Shader.ShaderModuleData> shaderModules = new ArrayList<>();
        shaderModules.add(new Shader.ShaderModuleData("assets/shaders/basic.vert", GL_VERTEX_SHADER));
        shaderModules.add(new Shader.ShaderModuleData("assets/shaders/basic.frag", GL_FRAGMENT_SHADER));

        m_Shader = new Shader(shaderModules);
        m_Shader.validate();

        m_Texture = new Texture("assets/graphics/test.png");
        m_Mesh = Mesh.CreateMesh(new Vector2f(0.0f), new Vector2f(200.0f, 200.0f), new Vector3f(1.0f, 1.0f, 1.0f));

        m_Shader.createUniform("u_ViewProjection");
        m_Shader.createUniform("u_Textures");
    }

    private void update() {
        m_Camera.update();
        Vector3f pos = m_Camera.getPosition();

        m_Camera.setPosition(pos);
    }

    private void render() {
        m_Shader.bind();

        Matrix4f viewProjection = m_Camera.getCombined();
        m_Shader.setUniformMat4("u_ViewProjection", viewProjection);

        m_Texture.bind();
        glBindVertexArray(m_Mesh.getVAO());
        glDrawElements(GL_TRIANGLES, m_Mesh.getIndexCount(), GL_UNSIGNED_INT, NULL);

        m_Shader.unbind();
    }

    private void resize(int width, int height) {
        m_Camera.resize(width, height);
        m_Window.setResized(false);
    }

    private void dispose() {
        // Shuts down and frees any app resources
        m_Shader.dispose();
        m_Texture.dispose();
        m_Mesh.dispose();

        // Shuts down GLFW and the Main Thread
        try {
            glfwTerminate();
            m_ErrorCallback.free();
            glfwSetErrorCallback(null);

            m_Thread.join(1);
            System.exit(0);
        } catch (InterruptedException ignored) {
            System.exit(-1);
        }
    }

    /**
     * <p>Starts up the Main Thread and sets everything in motion.</p>
     */
    public void start() {
        // We shouldn't start it if it is already running, straightforward
        if(!m_Running) {
            m_ErrorCallback = GLFWErrorCallback.createPrint(System.err).set();
            if(!glfwInit())
                throw new IllegalStateException("Failed to initialize GLFW!");

            m_Thread = new Thread(this, "Main_Thread");
            m_Running = true;
            m_Thread.start();
        }
    }

    /**
     * <p>Stops the Main Thread and closes the Window if it isn't closed already.</p>
     */
    public void stop() {
        // We shouldn't stop it if it isn't running, straightforward
        if(m_Running) {
            if(!m_Window.shouldClose())
                m_Window.close();

            m_Running = false;
        }
    }

    @Override
    public void run() {
        m_Window = new Window(m_Title, m_Width, m_Height, true);
        m_Window.show();

        // Initialize OpenGL and our App after window creation
        RenderCmd.initialize();
        initialize();

        RenderCmd.clearColor(0.1f, 0.1f, 0.1f);
        // The Main Loop for our Application
        while(m_Running) {
            if(m_Window.shouldClose()) {
                m_Running = false;
                continue;
            }

            RenderCmd.clear();
            if(m_Window.hasResized()) {
                m_Width = m_Window.getWidth();
                m_Height = m_Window.getHeight();
                resize(m_Width, m_Height);
            }

            update();
            render();

            m_Window.swapBuffers();
            glfwPollEvents();
        }

        m_Window.dispose();
        dispose();
    }
}
