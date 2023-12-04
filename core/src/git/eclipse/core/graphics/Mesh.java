package git.eclipse.core.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

public class Mesh {

    private int m_VaoId;
    private List<Integer> m_VboIdList;
    private float[] m_Vertices;
    private int[] m_Indices;
    private final boolean m_Dynamic;

    public Mesh(float[] vertices, int[] indices) {
        this(vertices, indices, false);
    }

    public Mesh(float[] vertices, int[] indices, boolean dynamic) {
        m_Vertices = vertices;
        m_Indices = indices;
        m_Dynamic = dynamic;

        initialize();
    }

    private void initialize() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final int usage = m_Dynamic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW;
            m_VboIdList = new ArrayList<>();

            m_VaoId = glCreateVertexArrays();
            glBindVertexArray(m_VaoId);

            // Vertex Buffer
            int vbo = glCreateBuffers();
            m_VboIdList.add(vbo);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            FloatBuffer vertexBuffer = stack.callocFloat(m_Vertices.length);
            vertexBuffer.put(0, m_Vertices);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, usage);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 10 * Float.BYTES, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 4, GL_FLOAT, false, 10 * Float.BYTES, 3 * Float.BYTES);

            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 10 * Float.BYTES, 7 * Float.BYTES);

            glEnableVertexAttribArray(3);
            glVertexAttribPointer(3, 1, GL_FLOAT, false, 10 * Float.BYTES, 9 * Float.BYTES);

            // Element Buffer
            int ebo = glCreateBuffers();
            m_VboIdList.add(ebo);
            IntBuffer indexBuffer = stack.callocInt(m_Indices.length);
            indexBuffer.put(0, m_Indices);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, usage);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public void dispose() {
        m_VboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(m_VaoId);
    }

    public void setVertices(float[] vertices) {
        if(!m_Dynamic) {
            System.err.println("You can't update a static mesh!");
            return;
        }

        m_Vertices = Arrays.copyOf(vertices, vertices.length);
    }

    public void setIndices(int[] indices) {
        if(!m_Dynamic) {
            System.err.println("You can't update a static mesh!");
            return;
        }

        m_Indices = Arrays.copyOf(indices, indices.length);
    }

    public int getVAO() {
        return m_VaoId;
    }

    public int getIndexCount() {
        return m_Indices.length;
    }

    public final float[] getVertices() {
        return m_Vertices;
    }

    public final int[] getIndices() {
        return  m_Indices;
    }

    public static Mesh CreateMesh(Vector2f position, Vector2f size, Vector3f color) {
        Vector2f halfSize = new Vector2f(size.x / 2.0f, size.y / 2.0f);
        final float[] vertices = new float[] {
               position.x - halfSize.x, position.y + halfSize.y, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 0.0f, 0.0f, // Top Left
               position.x - halfSize.x, position.y - halfSize.y, 0.0f, color.x, color.y, color.z, 1.0f, 0.0f, 1.0f, 0.0f, // Bottom Left
               position.x + halfSize.x, position.y - halfSize.y, 0.0f, color.x, color.y, color.z, 1.0f, 1.0f, 1.0f, 0.0f, // Bottom Right
               position.x + halfSize.x, position.y + halfSize.y, 0.0f, color.x, color.y, color.z, 1.0f, 1.0f, 0.0f, 0.0f  // Top Right
        };

        final int[] indices = new int[] {
                0, 1, 2,
                3, 0, 2
        };

        return new Mesh(vertices, indices, false);
    }

}
