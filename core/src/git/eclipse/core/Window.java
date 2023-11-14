package git.eclipse.core;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Window {

    private long m_Handle;

    private String m_Title;
    private int m_Width, m_Height;
    private boolean m_Resizable, m_Focused;

    public Window(String title, int width, int height) {
        this(title, width, height, false);
    }

    public Window(String title, int width, int height, boolean resizable) {
        m_Title = title;
        m_Width = width;
        m_Height = height;

        m_Focused = false;
        m_Resizable = resizable;

        initialize();
    }

    /**
     * Initializes our Window using GLFW.
     */
    private void initialize() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, m_Resizable ? GLFW_TRUE : GLFW_FALSE);

        m_Handle = glfwCreateWindow(m_Width, m_Height, m_Title, MemoryUtil.NULL, MemoryUtil.NULL);
        if(m_Handle == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create a Window!");

        try(MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFWVidMode video_mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if(video_mode == null)
                throw new RuntimeException("Failed to find a video mode!");

            glfwGetWindowSize(m_Handle, pWidth, pHeight);
            glfwSetWindowPos(m_Handle, (video_mode.width() - pWidth.get(0)) / 2, (video_mode.height() - pHeight.get(0)) / 2);
        }

        glfwSetWindowFocusCallback(m_Handle, (window, focused) -> m_Focused = focused);

        glfwMakeContextCurrent(m_Handle);
        glfwSwapInterval(1);
    }

    public void show() {
        if(m_Handle != MemoryUtil.NULL)
            glfwShowWindow(m_Handle);
    }

    public void hide() {
        if(m_Handle != MemoryUtil.NULL)
            glfwHideWindow(m_Handle);
    }

    public void swapBuffers() {
        if(m_Handle != MemoryUtil.NULL)
            glfwSwapBuffers(m_Handle);
    }

    /**
     * Disposes our Window and its resources.
     */
    public void dispose() {
        if(m_Handle != MemoryUtil.NULL) {
            glfwFreeCallbacks(m_Handle);
            glfwDestroyWindow(m_Handle);
        }
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(m_Handle);
    }

    public boolean isResizable() {
        return m_Resizable;
    }

    public boolean isFocused() {
        return m_Focused;
    }

    public String getTitle() {
        return m_Title;
    }

    public int getWidth() {
        return m_Width;
    }

    public int getHeight() {
        return m_Height;
    }

}
