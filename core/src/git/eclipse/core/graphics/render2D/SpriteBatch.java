package git.eclipse.core.graphics.render2D;

import git.eclipse.core.graphics.Mesh;
import git.eclipse.core.graphics.Shader;
import git.eclipse.core.graphics.Texture;
import git.eclipse.core.graphics.cameras.OrthoCamera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SpriteBatch {
    private final Shader m_BatchShader;
    private final Mesh m_Mesh;
    private final int m_Size;

    private final FloatBuffer m_Vertices;
    private final IntBuffer m_Indices;
    private final List<Texture> m_Textures;

    private OrthoCamera m_Camera;
    private boolean m_Rendering;
    private int m_SpriteCount;

    public SpriteBatch(Shader shader) {
        this(1000, shader);
    }

    public SpriteBatch(int size, Shader shader) {
        if(size > 8191) throw new IllegalArgumentException("Can't have more than 8191 sprites a batch: " + size);
        shader.createUniform("u_ViewProjection");
        shader.createUniform("u_Textures");

        m_BatchShader = shader;
        m_Size = size;

        m_Textures = new ArrayList<>();
        m_Vertices = MemoryUtil.memAllocFloat(size * 4);
        m_Indices = MemoryUtil.memAllocInt(size * 6);

        m_Mesh = new Mesh(m_Size);
        m_Rendering = false;
    }

    public void begin(OrthoCamera camera) {
        if(m_Rendering) throw new IllegalStateException("You have to call end() before calling begin() when rendering.");

        glDisable(GL_DEPTH_TEST);

        m_Camera = camera;
        m_Camera.update();

        m_SpriteCount = 0;
        m_Rendering = true;
    }

    public void render(Texture texture, Vector2f position, float layer, Vector2f size, Vector3f color) {
        render(texture, new Vector2f(0.0f), new Vector2f(1.0f), new Vector3f(position.x, position.y, layer), size, color);
    }

    public void render(Texture texture, Vector2f cellPos, Vector2f cellSize, Vector3f position, Vector2f size, Vector3f color) {
        if(m_Vertices.remaining() <= m_Size * 4)
            flush();

        if(!m_Textures.contains(texture))
            m_Textures.add(texture);

        Vector2f halfSize = new Vector2f(size.x * 0.5f, size.y * 0.5f);
        Vector2f uv1 = new Vector2f(cellPos.x == 0.0f ? cellPos.x : cellPos.x / texture.getWidth(), cellPos.y == 0.0f ? cellPos.y : cellPos.y / texture.getHeight());
        Vector2f uv2 = new Vector2f(cellSize.x == 1.0f ? cellSize.x : (cellPos.x + cellSize.x) / texture.getWidth(), cellSize.y == 1.0f ? cellSize.y : (cellPos.y + cellSize.y) / texture.getHeight());

        float index = (float) m_Textures.indexOf(texture);
        texture.bind((int) index);

        m_Vertices.put(position.x - halfSize.x).put(position.y + halfSize.y).put(position.z).put(color.x).put(color.y).put(color.z).put(1.0f).put(uv1.x).put(uv1.y).put(index); // Top Left
        m_Vertices.put(position.x - halfSize.x).put(position.y - halfSize.y).put(position.z).put(color.x).put(color.y).put(color.z).put(1.0f).put(uv1.x).put(uv2.y).put(index); // Bottom Left
        m_Vertices.put(position.x + halfSize.x).put(position.y - halfSize.y).put(position.z).put(color.x).put(color.y).put(color.z).put(1.0f).put(uv2.x).put(uv2.y).put(index); // Bottom Right
        m_Vertices.put(position.x + halfSize.x).put(position.y + halfSize.y).put(position.z).put(color.x).put(color.y).put(color.z).put(1.0f).put(uv2.x).put(uv1.y).put(index); // Top Right

        m_Indices.put(0).put(1).put(2);
        m_Indices.put(3).put(0).put(2);

        m_SpriteCount++;

    }

    public void end() {
        glEnable(GL_DEPTH_TEST);

        flush();
        m_Rendering = false;
        m_Camera = null;
    }

    public void dispose() {
        if(m_Rendering) end();

        MemoryUtil.memFree(m_Vertices);
        MemoryUtil.memFree(m_Indices);

        m_Mesh.dispose();
        m_BatchShader.dispose();
    }

    private void flush() {
        m_Camera.update();

        m_Vertices.flip();
        m_Indices.flip();

        m_BatchShader.bind();

        Matrix4f combinedMatrix = m_Camera.getCombined();
        m_BatchShader.setUniformMat4("u_ViewProjection", combinedMatrix);

        glBindVertexArray(m_Mesh.getVAO());
        glBindBuffer(GL_ARRAY_BUFFER, m_Mesh.getVBO(0));
        glBufferSubData(GL_ARRAY_BUFFER, 0, m_Vertices);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_Mesh.getVBO(1));
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, m_Indices);

        glDrawElements(GL_TRIANGLES, m_SpriteCount * 6, GL_UNSIGNED_INT, NULL);

        glBindVertexArray(0);
        m_BatchShader.unbind();


        m_Textures.clear();
        m_Vertices.clear();
        m_Indices.clear();

        m_SpriteCount = 0;
    }

    public Shader getShader() {
        return m_BatchShader;
    }

    public int getSize() {
        return m_Size;
    }

}
