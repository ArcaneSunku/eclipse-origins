package git.eclipse.core.graphics.cameras;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class OrthoCamera extends Camera {

    private float m_AspectRatio;

    public OrthoCamera(float width, float height) {
        super();
        resize(width, height);
    }

    public void update() {
        Quaternionf rotation = new Quaternionf().rotationXYZ(0f, 0f, Math.toRadians(m_Rotation.z));
        m_View = new Matrix4f();

        m_View.rotate(rotation);
        m_View.translate(-m_Position.x, -m_Position.y, -m_Position.z);
    }

    public void resize(float width, float height) {
        m_AspectRatio =  width / height;

        m_Projection = new Matrix4f();
        m_Projection.ortho(-width / m_AspectRatio, width / m_AspectRatio, -height / m_AspectRatio, height / m_AspectRatio, -1.0f, 1.0f);
    }

    public float getAspectRatio() {
        return m_AspectRatio;
    }

}
