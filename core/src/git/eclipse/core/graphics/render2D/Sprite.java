package git.eclipse.core.graphics.render2D;

import git.eclipse.core.graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Sprite {

    private Texture m_Texture;
    private final Vector3f m_Color;

    private final Vector2f m_CellPos;
    private final Vector2f m_CellSize;

    private final Vector3f m_Position;
    private final Vector2f m_Size;
    private float m_Scale;
    private float m_TileFactor;

    public Sprite() {
        this(null);
    }

    public Sprite(Texture texture) {
        this(texture, new Vector3f(1.0f), new Vector3f(0.0f), new Vector2f(texture.getWidth(), texture.getHeight()), 1.0f);
    }

    public Sprite(Texture texture, Vector3f color, Vector3f position, Vector2f size, float scale) {
        this(texture, color, new Vector2f(0.0f), new Vector2f(1.0f), position, size, scale);
    }

    public Sprite(Texture texture, Vector3f color, Vector2f cellPos, Vector2f cellSize, Vector3f position, Vector2f size, float scale) {
        this(texture, color.x, color.y, color.z, cellPos.x, cellPos.y, cellSize.x, cellSize.y, position.x, position.y, position.z, size.x, size.y, scale);
    }

    public Sprite(Texture texture, float r, float g, float b, float cellX, float cellY, float cellWidth, float cellHeight, float x, float y, float layer, float width, float height, float scale) {
        m_Texture = texture;
        m_Color = new Vector3f(r, g, b);

        m_CellPos = new Vector2f(cellX, cellY);
        m_CellSize = new Vector2f(cellWidth, cellHeight);

        m_Position = new Vector3f(x, y, layer);
        m_Size = new Vector2f(width, height);
        m_Scale = scale;
        m_TileFactor = 1.0f;
    }

    public void draw(SpriteBatch batch) {
        if(m_Texture == null) throw new IllegalStateException("Can't draw a Sprite without a texture!");

        batch.render(m_Texture, m_CellPos, m_CellSize, m_Position, new Vector2f(m_Size).mul(m_Scale), m_Color, m_TileFactor);
    }

    public void setTexture(Texture texture) {
        m_Texture = texture;
    }

    public void setColor(float r, float g, float b) {
        boolean normalized = !(r > 1.0f || g > 1.0f || b > 1.0f);
        if(normalized)
            m_Color.set(r, g, b);
        else
            m_Color.set(r > 0.0f ? r / 255.0f : 0.0f, g > 0.0f ? g / 255.0f : 0.0f, b > 0.0f ? b / 255.0f : 0.0f);
    }

    public void setColor(Vector3f color) {
        setColor(color.x, color.y, color.z);
    }

    public void setPosition(float x, float y, float layer) {
        m_Position.set(x, y, layer);
    }

    public void setSize(float width, float height) {
        m_Size.set(width, height);
    }

    public void setScale(float scale) {
        m_Scale = scale;
    }

    public void setCellPos(Vector2f cellPos) {
        m_CellPos.set(cellPos);
    }

    public void setCellSize(Vector2f cellSize) {
        m_CellSize.set(cellSize);
    }

    public void setTileFactor(float tileFactor) {
        m_TileFactor = tileFactor;
    }

    public Texture getTexture() {
        return m_Texture;
    }

    public Vector3f getColor() {
        return m_Color;
    }

    public Vector3f getPosition() {
        return m_Position;
    }

    public Vector2f getCellPos() {
        return m_CellPos;
    }

    public Vector2f getSize() {
        return m_Size;
    }

    public Vector2f getCellSize() {
        return m_CellSize;
    }

    public float getScale() {
        return m_Scale;
    }

    public float getTileFactor() { return m_TileFactor; }

}
