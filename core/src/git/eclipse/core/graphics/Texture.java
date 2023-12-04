package git.eclipse.core.graphics;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL45.glCreateTextures;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private final String m_Filepath;

    private int m_TextureId;
    private int m_Width, m_Height;

    public Texture(int width, int height, ByteBuffer buf) {
        m_Filepath = "";
        generateTexture(width, height, buf);
    }

    public Texture(String filepath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            m_Filepath = filepath;
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer buf = stbi_load(filepath, w, h, channels, 4);
            if (buf == null) throw new RuntimeException(String.format("Image file [%s] not loaded: %s", m_Filepath, stbi_failure_reason()));

            int width = w.get(), height = h.get();
            generateTexture(width, height, buf);

            stbi_image_free(buf);
        }
    }

    public void bind() {
        bind(0);
    }

    public void bind(int slot) {
        if(slot > 31) slot = 31;

        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, m_TextureId);
    }

    public void dispose() {
        glDeleteTextures(m_TextureId);
    }

    private void generateTexture(int width, int height, ByteBuffer buf) {
        m_Width = width;
        m_Height = height;
        m_TextureId = glCreateTextures(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, m_TextureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public String getFilepath() {
        return m_Filepath;
    }

    public int getWidth() {
        return m_Width;
    }

    public int getHeight() {
        return m_Height;
    }
}
