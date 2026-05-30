package dev.atomixsoft.solar_eclipse.client.graphics.cameras;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class OrthoCamera extends Camera {
    private float m_AspectRatio;
    private float m_Width, m_Height;

    public OrthoCamera(float width, float height) {
        super();
        this.resize(width, height);
    }

    public void update() {
        update(true);
    }

    public void update(boolean inverted) {
        Quaternionf rotation = new Quaternionf().rotationXYZ(0f, 0f, Math.toRadians(getRotation().z));
        Vector3f position = getPosition();

        m_View.identity();
        m_View.translate(position.x, position.y, 0.0f);
        m_View.rotate(rotation);

        if(inverted)
            m_View.invert();
    }

    public void resize(float width, float height) {
        m_Width = width;
        m_Height = height;
        m_AspectRatio =  m_Width / m_Height;

        float zoom = 1.0f / m_Zoom;
        m_Projection.identity();
        m_Projection.ortho(-m_AspectRatio * zoom, m_AspectRatio * zoom, -zoom, zoom, -1.0f, 1.0f);
    }

    public void setZoom(float zoom) {
        super.setZoom(zoom);
        resize(m_Width, m_Height);
    }

    public void getBounds(Vector3f outMin, Vector3f outMax) {
        float hWidth = m_AspectRatio * (1.0f / m_Zoom);
        float hHeight = (1.0f / m_Zoom);

        Vector3f position = getPosition();

        outMin.set(position.x - hWidth, position.y - hHeight, 0);
        outMax.set(position.x + hWidth, position.y + hHeight, 0);
    }

    public float getAspectRatio() {
        return this.m_AspectRatio;
    }

    public float getWidth() {
        return this.m_Width;
    }

    public float getHeight() {
        return this.m_Height;
    }

    public float getViewWidth() {
        return m_AspectRatio * (1.0f / m_Zoom);
    }

    public float getViewHeight() {
        return (1.0f / m_Zoom);
    }

}