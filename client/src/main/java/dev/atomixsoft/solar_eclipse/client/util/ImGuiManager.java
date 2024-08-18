package dev.atomixsoft.solar_eclipse.client.util;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

/**
 * <p>Handles everything required to use imgui with our project.</p>
 */
public class ImGuiManager {

    private final ImGuiImplGl3 m_imguiImplGL3;
    private final ImGuiImplGlfw m_imguiImplGLFW;

    public ImGuiManager() {
        m_imguiImplGL3 = new ImGuiImplGl3();
        m_imguiImplGLFW = new ImGuiImplGlfw();
    }

    public void init(long window, String glslVersion) {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();

        io.setIniFilename("client/ui.conf");

        m_imguiImplGL3.init(glslVersion);
        m_imguiImplGLFW.init(window, true);
    }

    public void setup() {
        m_imguiImplGLFW.newFrame();
        ImGui.newFrame();
    }

    public void render() {
        ImGui.render();
        m_imguiImplGL3.renderDrawData(ImGui.getDrawData());

        if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupPtr);
        }
    }

    public void dispose() {
        m_imguiImplGL3.dispose();
        m_imguiImplGLFW.dispose();

        ImGui.destroyContext();
    }

}
