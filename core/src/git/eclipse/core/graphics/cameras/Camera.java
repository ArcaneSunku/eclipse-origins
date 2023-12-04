package git.eclipse.core.graphics.cameras;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Camera {

    protected Matrix4f m_View, m_Projection, m_Combined;
    protected Vector3f m_Position, m_Rotation, m_Scale;

    protected Camera() {
        m_View = new Matrix4f();
        m_Projection = new Matrix4f();
        m_Combined = new Matrix4f();

        m_Position = new Vector3f(0.0f);
        m_Rotation = new Vector3f(0.0f);
        m_Scale = new Vector3f(1.0f);
    }

    public void setPosition(Vector3f position) {
        m_Position.set(position);
    }

    public void setRotation(Vector3f rotation) {
        m_Rotation.set(rotation);
    }

    public void setScale(Vector3f scale) {
        m_Scale.set(scale);
    }

    public void setScale(float scalar) {
        setScale(new Vector3f(scalar));
    }

    public Vector3f getPosition() {
        return m_Position;
    }

    public Vector3f getRotation() {
        return m_Rotation;
    }

    public Vector3f getScale() {
        return m_Scale;
    }

    public Matrix4f getView() {
        return m_View;
    }

    public Matrix4f getProjection() {
        return m_Projection;
    }

    public Matrix4f getCombined() {
        m_Combined.set(m_View).mul(m_Projection);
        return m_Combined;
    }

}
