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

/**
 * Represents an OpenGL texture, either loaded from an image file or created with raw pixel data.
 * This class provides functionality for texture generation, binding, disposal, and retrieval of
 * texture properties such as width and height.
 *
 * <p>The class supports both loading textures from image files and generating textures from raw
 * pixel data. The {@link #bind()} and {@link #bind(int)} methods are used to bind the texture
 * for rendering, and the {@link #dispose()} method is used to release OpenGL resources when
 * the texture is no longer needed.</p>
 *
 * <p><b>Usage:</b> Create an instance of this class by either providing a filepath to an image
 * file or specifying the width, height, and pixel data directly. Use the {@link #bind()} method
 * to activate the texture for rendering and {@link #dispose()} to release OpenGL resources.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Create a texture from an image file
 * Texture texture = new Texture("path/to/texture.png");
 *
 * // Bind the texture for rendering
 * texture.bind();
 *
 * // Use the texture in rendering calls
 * // ...
 *
 * // Dispose of the texture when no longer needed
 * texture.dispose();
 * }</pre>
 *
 * @author Tahnner Shambaugh
 * @version 1.0
 * @see <a href="https://www.lwjgl.org/">LWJGL Documentation</a>
 */
public class Texture {
    private final String m_Filepath;

    private int m_TextureId;
    private int m_Width, m_Height;

    /**
     * Creates a new texture with the specified width, height, and pixel data.
     *
     * @param width  The width of the texture.
     * @param height The height of the texture.
     * @param buf    The pixel data buffer.
     */
    public Texture(int width, int height, ByteBuffer buf) {
        m_Filepath = "";
        generateTexture(width, height, buf);
    }

    /**
     * Creates a new texture by loading an image file from the specified filepath.
     *
     * @param filepath The filepath to the image file.
     */
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

    /**
     * Binds the texture to the default texture unit 0.
     */
    public void bind() {
        bind(0);
    }

    /**
     * Binds the texture to a specific texture unit.
     *
     * @param slot The texture unit slot to bind the texture to.
     */
    public void bind(int slot) {
        if(slot < 0) slot = 0; // Just in case :)
        if(slot > 31) slot = 31; // Same for this :)

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
