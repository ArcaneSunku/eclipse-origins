package git.eclipse.core.graphics;

import git.eclipse.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * <p>Everything related to OpenGL Shaders can be found here. This class is what you'll use to create, bind, and dispose of the Shaders you'll use to render.</p>
 */
public class Shader {

    private final Map<String, Integer> m_UniformMap;
    private final int m_ProgramId;

    public Shader(List<ShaderModuleData> shaderModulesDataList) {
        m_ProgramId = glCreateProgram();
        if(m_ProgramId == 0)
            throw new RuntimeException("Failed to create a Shader Program!");

        m_UniformMap = new HashMap<>();
        List<Integer> shaderModules = new ArrayList<>();
        shaderModulesDataList.forEach(s -> shaderModules.add(createShader(Utils.readFile(s.shaderFile), s.shaderType)));

        link(shaderModules);
    }

    public void bind() {
        glUseProgram(m_ProgramId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void dispose() {
        unbind();

        if(m_ProgramId != 0)
            glDeleteProgram(m_ProgramId);
    }

    public void createUniform(String name) {
        int location = glGetUniformLocation(m_ProgramId, name);
        if(location < 0)
            throw new RuntimeException(String.format("Failed to create uniform [%s]!", name));

        m_UniformMap.putIfAbsent(name, location);
    }

    public void setUniform1i(String name, int value) {
        int location = m_UniformMap.get(name);
        glUniform1i(location, value);
    }

    public void setUniformBool(String name, boolean value) {
        int bool = value ? GL_TRUE : GL_FALSE;
        setUniform1i(name, bool);
    }

    public void setUniform1f(String name, float value) {
        if(!m_UniformMap.containsKey(name)) createUniform(name);

        int location = m_UniformMap.get(name);
        glUniform1f(location, value);
    }

    public void setUniform2f(String name, float val1, float val2) {
        if(!m_UniformMap.containsKey(name)) createUniform(name);

        int location = m_UniformMap.get(name);
        glUniform2f(location, val1, val2);
    }

    public void setUniform2f(String name, Vector2f value) {
        setUniform2f(name, value.x, value.y);
    }

    public void setUniform3f(String name, float val1, float val2, float val3) {
        if(!m_UniformMap.containsKey(name)) createUniform(name);

        int location = m_UniformMap.get(name);
        glUniform3f(location, val1, val2, val3);
    }

    public void setUniform3f(String name, Vector3f value) {
        setUniform3f(name, value.x, value.y, value.z);
    }

    public void setUniformMat4(String name, Matrix4f value) {
        if(!m_UniformMap.containsKey(name)) createUniform(name);
        int location = m_UniformMap.get(name);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.callocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void validate() {
        glValidateProgram(m_ProgramId);
        if(glGetProgrami(m_ProgramId, GL_VALIDATE_STATUS) == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(m_ProgramId, 1024));
            throw new RuntimeException("Error validating Shader Program!");
        }
    }

    protected int createShader(String shaderSource, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if(shaderId == 0)
            throw new RuntimeException("Failed to create shader: " + shaderType);

        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);
        if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(shaderId, 1024));
            throw new RuntimeException("Failed to compile shader: " + shaderType);
        }

        glAttachShader(m_ProgramId, shaderId);
        return shaderId;
    }

    private void link(List<Integer> shaderModules) {
        glLinkProgram(m_ProgramId);
        if(glGetProgrami(m_ProgramId, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(m_ProgramId, 1024));
            throw new RuntimeException("Error linking Shader Program!");
        }

        shaderModules.forEach(s -> glDetachShader(m_ProgramId, s));
        shaderModules.forEach(GL30::glDeleteShader);
    }

    public int getProgramId() {
        return m_ProgramId;
    }

    public record ShaderModuleData(String shaderFile, int shaderType) { }

}
