package git.eclipse.core.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

/**
 * <p>Represents an OpenGL Mesh. This class provides functionality for
 * managing vertex and index data, initializing OpenGL buffers, and updating dynamic meshes.</p>
 *
 * <p>The class supports both static and dynamic meshes. Static meshes have fixed vertex and
 * index data, while dynamic meshes can be updated with new vertex and index arrays.</p>
 *
 * <p><b>Usage:</b> Create an instance of this class, initialize it, and use the provided methods
 * to set or update vertices and indices. The `initialize` method sets up the necessary OpenGL
 * Vertex Array Object (VAO) and Vertex Buffer Objects (VBOs) for rendering the mesh.</p>
 *
 * <p><b>Important Note:</b> If a static mesh is attempted to be updated using the `setVertices`
 * or `setIndices` methods, a RuntimeException is thrown, and an error message is printed to
 * the error stream.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Create a dynamic mesh with initial size
 * Mesh dynamicMesh = new Mesh(100);
 *
 * // Set vertices and indices for dynamic mesh
 * float[] newVertices = //...;
 * int[] newIndices = //...;
 * dynamicMesh.setVertices(newVertices);
 * dynamicMesh.setIndices(newIndices);
 * }</pre>
 *
 * @author Tahnner Shambaugh
 * @version 1.0
 * @see <a href="https://www.lwjgl.org/">LWJGL Documentation</a>
 */
public class Mesh {

    private int m_VaoId;
    private List<Integer> m_VboIdList;
    private float[] m_Vertices;
    private int[] m_Indices;
    private final boolean m_Dynamic;

    /**
     * Creates a dynamic Mesh with the specified size that will decide how large the vertex and index arrays are.
     *
     * @param maxSize the size that decides how many vertices and indices we'll allow
     */
    public Mesh(int maxSize) {
        this(maxSize * 4, maxSize * 6);
    }

    /**
     * Creates a dynamic Mesh with the specified sized vertices array and indices array.
     *
     * @param maxVertices the max allowed vertices
     * @param maxIndices the max allowed indices
     */
    public Mesh(int maxVertices, int maxIndices) {
        this(new float[maxVertices], new int[maxIndices], true);
    }

    /**
     * Creates a static Mesh with the specified vertices and indices.
     *
     * @param vertices vertex data for our Mesh
     * @param indices index data for our Mesh
     */
    public Mesh(float[] vertices, int[] indices) {
        this(vertices, indices, false);
    }

    /**
     * Creates a Mesh with the specified vertices and indices.
     * The option for dynamic updates is also available.
     *
     * @param vertices vertex data for our Mesh
     * @param indices index data for our Mesh
     * @param dynamic whether our Mesh should allow the vertex and index data to be changed
     */
    public Mesh(float[] vertices, int[] indices, boolean dynamic) {
        m_Vertices = vertices;
        m_Indices = indices;
        m_Dynamic = dynamic;

        initialize();
    }

    /**
     * Initializes the OpenGL Vertex Array Object (VAO) and associated Vertex Buffer Objects (VBOs)
     * for rendering the mesh.
     * <p>
     * This method sets up the VAO, VBOs, and vertex attributes required for rendering the mesh using
     * OpenGL. It allocates memory, binds buffers, configures vertex attributes, and performs other
     * necessary setup operations based on the mesh's characteristics.
     * <p>
     * Note: This method assumes that the OpenGL context is current.
     */
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

            if(!m_Dynamic) {
                FloatBuffer vertexBuffer = stack.callocFloat(m_Vertices.length);
                vertexBuffer.put(0, m_Vertices);
                glBufferData(GL_ARRAY_BUFFER, vertexBuffer, usage);
            } else {
                glBufferData(GL_ARRAY_BUFFER, (long) m_Vertices.length * Float.BYTES, usage);
            }

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
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            if(!m_Dynamic) {
                IntBuffer indexBuffer = stack.callocInt(m_Indices.length);
                indexBuffer.put(0, m_Indices);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, usage);
            } else {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) m_Indices.length * Integer.BYTES, usage);
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    /**
     * Disposes our VBOs and our VAO.
     */
    public void dispose() {
        m_VboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(m_VaoId);
    }
    public int getVAO() {
        return m_VaoId;
    }

    public int getVBO(int index) {
        return m_VboIdList.get(index);
    }

    public int getVertexCount() {
        return m_Vertices.length;
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
