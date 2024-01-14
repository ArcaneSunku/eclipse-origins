package git.eclipse.core.graphics.cameras;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OrthoCamera extends Camera {

    private float m_AspectRatio;
    private float m_Width, m_Height;

    public OrthoCamera(float width, float height) {
        super();
        resize(width, height);
    }

    public void update() {
        Quaternionf rotation = new Quaternionf().rotationXYZ(0f, 0f, Math.toRadians(m_Rotation.z));
        Vector3f position = getPosition();
        float zoom = getZoom();

        m_View = new Matrix4f();
        m_View.rotate(rotation);
        m_View.translate(-position.x, -position.y, -position.z);
        m_View.scale(zoom, zoom, 1.0f);
    }

    public void resize(float width, float height) {
        m_Width = width;
        m_Height = height;

        if(width > height)
            m_AspectRatio =  m_Width / m_Height;
        else
            m_AspectRatio = m_Height / m_Width;

        m_Projection = new Matrix4f();
        m_Projection.ortho(-width / m_AspectRatio, width / m_AspectRatio, -height / m_AspectRatio, height / m_AspectRatio, -1.0f, 1.0f);
    }

    public float getAspectRatio() {
        return m_AspectRatio;
    }

    public float getWidth() {
        return m_Width;
    }

    public float getHeight() {
        return m_Height;
    }

}
