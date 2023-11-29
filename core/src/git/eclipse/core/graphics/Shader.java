package git.eclipse.core.graphics;

import git.eclipse.core.utils.Files;

import static org.lwjgl.opengl.GL20.*;

/**
 * <p>Everything related to OpenGL Shaders can be found here. This class is what you'll use to create, bind, and dispose of the Shaders you'll use to render.</p>
 */
public class Shader {

    private final String m_Filepath;
    private String m_Source;
    private int m_ProgramId;
    private boolean m_Bound;

    public Shader(String filepath) {
        m_Filepath = filepath;
        readShader(m_Filepath);
        m_Bound = false;
    }

    private void readShader(String filepath) {
        final String vertToken = "#[vertex]", fragToken = "#[fragment]";
        m_Source = Files.StringFromFile(filepath);
        String vertSrc, fragSrc;

        vertSrc = m_Source.substring(m_Source.indexOf(vertToken) + vertToken.length() + 1, m_Source.indexOf(fragToken));
        fragSrc = m_Source.substring(m_Source.indexOf(fragToken) + fragToken.length() + 1);

        final int vertex = createShader(vertSrc, GL_VERTEX_SHADER), fragment = createShader(fragSrc, GL_FRAGMENT_SHADER);
        assert(vertex != -1 && fragment != -1);

        m_ProgramId = linkShader(vertex, fragment);
        assert(m_ProgramId != -1);

        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    private int createShader(String source, int shaderType) {
        int shader = glCreateShader(shaderType);

        glShaderSource(shaderType, source);
        glCompileShader(shader);

        if(glGetShaderi(shader, GL_COMPILE_STATUS) != GL_NO_ERROR) {
            String infoLog = glGetShaderInfoLog(shader, 1024);
            System.err.println(infoLog);
            glDeleteShader(shader);

            return -1;
        }

        return shader;
    }

    private int linkShader(int vertexId, int fragmentId) {
        int program = glCreateProgram();

        glAttachShader(program, vertexId);
        glAttachShader(program, fragmentId);

        glLinkProgram(program);
        if(glGetProgrami(program, GL_LINK_STATUS) != GL_NO_ERROR) {
            String infoLog = glGetProgramInfoLog(program, 1024);
            System.err.println(infoLog);

            glDetachShader(program, vertexId);
            glDetachShader(program, fragmentId);
            glDeleteProgram(program);

            return -1;
        }


        glValidateProgram(program);
        if(glGetProgrami(program, GL_VALIDATE_STATUS) != GL_NO_ERROR) {
            String infoLog = glGetProgramInfoLog(program, 1024);
            System.err.println(infoLog);

            glDetachShader(program, vertexId);
            glDetachShader(program, fragmentId);
            glDeleteProgram(program);

            return -1;
        }

        glDetachShader(program, vertexId);
        glDetachShader(program, fragmentId);

        return program;
    }

    public void bind() {
        if(m_Bound)
            return;

        glUseProgram(m_ProgramId);
        m_Bound = true;
    }

    public void unbind() {
        if(!m_Bound)
            return;

        glUseProgram(0);
        m_Bound = false;
    }

    public void dispose() {
        unbind();
        glDeleteProgram(m_ProgramId);
    }

    public boolean isBound() {
        return m_Bound;
    }

    public String getFilepath() {
        return m_Filepath;
    }

    public String getSource() {
        return m_Source;
    }

}
