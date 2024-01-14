package git.eclipse.core.graphics.render2D;

import git.eclipse.core.graphics.RenderCmd;
import git.eclipse.core.graphics.Shader;
import git.eclipse.core.graphics.Texture;
import git.eclipse.core.graphics.cameras.OrthoCamera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

public class SpriteBatch {
    private static final int MAX_TEXTURE_SLOTS = 32;

    private final Shader m_BatchShader;
    private final int m_Size;

    private final HashMap<Integer, Texture> m_TextureSlots;
    private final int m_VAO;
    private final List<Integer> m_VBOList;
    private final FloatBuffer m_Vertices;
    private final IntBuffer m_Indices;

    private final Texture m_WhiteTexture;
    private int m_TextureSlotIndex;
    private int m_IndexCount;

    private boolean m_Rendering;

    public SpriteBatch(Shader shader) {
        this(1000, shader);
    }

    public SpriteBatch(int size, Shader shader) {
        if(size > 8191) throw new IllegalArgumentException("Can't have more than 8191 sprites a batch: " + size);
        shader.createUniform("u_ViewProjection");
        shader.createUniform("u_Textures");

        m_Size = size;
        m_BatchShader = shader;

        m_TextureSlots = new HashMap<>();
        m_Vertices = MemoryUtil.memAllocFloat(m_Size * 4);

        int[] samplers = new int[MAX_TEXTURE_SLOTS];
        for (var i = 0; i < MAX_TEXTURE_SLOTS; i++)
            samplers[i] = i;

        m_BatchShader.bind();
        m_BatchShader.setUniformiv("u_Textures", samplers);
        m_BatchShader.unbind();

        int offset = 0;
        int[] indices = new int[size * 6];
        for(int i = 0; i < indices.length; i += 6) {
            indices[i]     = offset;
            indices[i + 1] = offset + 1;
            indices[i + 2] = offset + 2;

            indices[i + 3] = offset + 3;
            indices[i + 4] = offset;
            indices[i + 5] = offset + 2;
            offset+=4;
        }

        ByteBuffer wBuffer = MemoryUtil.memAlloc(4);
        wBuffer.put(0, new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255});

        m_WhiteTexture = new Texture(1, 1, wBuffer);
        m_TextureSlots.put(0, m_WhiteTexture);

        m_Indices = MemoryUtil.memAllocInt(indices.length);
        m_Indices.put(0, indices);

        m_VAO = glCreateVertexArrays();
        m_VBOList = new ArrayList<>();
        initBatch();

        m_Rendering = false;
    }

    private void initBatch() {
        glBindVertexArray(m_VAO);

        int vbo = glCreateBuffers();
        m_VBOList.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (m_Size * 4L) * Float.BYTES, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 11 * Float.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 11 * Float.BYTES, 3 * Float.BYTES);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 11 * Float.BYTES, 7 * Float.BYTES);

        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 1, GL_FLOAT, false, 11 * Float.BYTES, 9 * Float.BYTES);

        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 1, GL_FLOAT, false, 11 * Float.BYTES, 10 * Float.BYTES);

        int ebo = glCreateBuffers();
        m_VBOList.add(ebo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (m_Size * 6L) * Integer.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, m_Indices);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void startBatch() {
        m_IndexCount = 0;
        m_TextureSlotIndex = 1;
    }

    private void nextBatch() {
        flush();
        startBatch();
    }

    public void begin(OrthoCamera camera) {
        if(m_Rendering) throw new IllegalStateException("You have to call end() before calling begin() when rendering.");
        camera.update();

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        m_BatchShader.bind();
        Matrix4f combinedMatrix = camera.getCombined();
        m_BatchShader.setUniformMat4("u_ViewProjection", combinedMatrix);

        m_Rendering = true;
        startBatch();
    }

    public void render(Texture texture, Vector2f position, float layer, Vector2f size, Vector3f color) {
        render(texture, position, layer, size, color, 1.0f);
    }

    public void render(Texture texture, Vector2f position, float layer, Vector2f size, Vector3f color, float tileFactor) {
        render(texture, new Vector2f(0.0f), new Vector2f(1.0f), new Vector3f(position.x, position.y, layer), size, color, tileFactor);
    }

    public void render(Texture texture, Vector2f cellPos, Vector2f cellSize, Vector3f position, Vector2f size, Vector3f color, float tileFactor) {
        if(m_IndexCount >= m_Size * 6)
            nextBatch();

        Vector2f halfSize = new Vector2f(size.x * 0.5f, size.y * 0.5f);
        Vector2f uv1 = new Vector2f(cellPos.x == 0.0f ? cellPos.x : cellPos.x / texture.getWidth(), cellPos.y == 0.0f ? cellPos.y : cellPos.y / texture.getHeight());
        Vector2f uv2 = new Vector2f(cellSize.x == 1.0f ? cellSize.x : (cellPos.x + cellSize.x) / texture.getWidth(), cellSize.y == 1.0f ? cellSize.y : (cellPos.y + cellSize.y) / texture.getHeight());

        float textureSlot = 0.0f;
        for(int i = 0; i < m_TextureSlotIndex; i++) {
            Texture indexed = m_TextureSlots.get(i);
            if(indexed != null && indexed.getTextureId() == texture.getTextureId()) {
                textureSlot = (float) i;
                break;
            }
        }

        if (textureSlot == 0.0f)
        {
            if (m_IndexCount >= m_Size * 6)
                nextBatch();

            textureSlot = (float) m_TextureSlotIndex;
            m_TextureSlots.put((int) textureSlot, texture);
            m_TextureSlotIndex++;
        }

        m_Vertices.put(position.x - halfSize.x).put(position.y + halfSize.y).put(position.z).put(color.x).put(color.y).put(color.z).put(1.0f).put(uv1.x).put(uv1.y).put(textureSlot).put(tileFactor); // Top Left
        m_Vertices.put(position.x - halfSize.x).put(position.y - halfSize.y).put(position.z).put(color.x).put(color.y).put(color.z).put(1.0f).put(uv1.x).put(uv2.y).put(textureSlot).put(tileFactor); // Bottom Left
        m_Vertices.put(position.x + halfSize.x).put(position.y - halfSize.y).put(position.z).put(color.x).put(color.y).put(color.z).put(1.0f).put(uv2.x).put(uv2.y).put(textureSlot).put(tileFactor); // Bottom Right
        m_Vertices.put(position.x + halfSize.x).put(position.y + halfSize.y).put(position.z).put(color.x).put(color.y).put(color.z).put(1.0f).put(uv2.x).put(uv1.y).put(textureSlot).put(tileFactor); // Top Right

        m_IndexCount+= 6;

        if(m_Vertices.remaining() <= m_Size * 4)
            nextBatch();
    }

    public void end() {
        flush();

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glBindVertexArray(0);

        m_BatchShader.unbind();
        m_Rendering = false;
    }

    public void dispose() {
        if(m_Rendering) end();

        glDeleteVertexArrays(m_VAO);
        m_VBOList.forEach(GL15::glDeleteBuffers);

        MemoryUtil.memFree(m_Vertices);
        MemoryUtil.memFree(m_Indices);

        m_BatchShader.dispose();
        m_WhiteTexture.dispose();
    }

    private void flush() {
        if(m_IndexCount > 0) {
            m_Vertices.flip();

            glBindVertexArray(m_VAO);
            glBindBuffer(GL_ARRAY_BUFFER, m_VBOList.get(0));
            glBufferSubData(GL_ARRAY_BUFFER, 0, m_Vertices);

            for(int i = 0; i < m_TextureSlotIndex; i++)
                m_TextureSlots.get(i).bind(i);

            RenderCmd.DrawIndex(m_VAO, m_IndexCount);

            m_Vertices.clear();
        }
    }

    public Shader getShader() {
        return m_BatchShader;
    }

    public int getSize() {
        return m_Size;
    }

}
