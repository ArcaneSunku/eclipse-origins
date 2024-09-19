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
        float zoom = getZoom();

        m_View.identity();

        if(inverted) m_View.translate(-position.x, -position.y, 0.0f);
        else m_View.translate(position.x, position.y, 0.0f);

        m_View.rotate(rotation);
        m_View.scale(zoom, zoom, 1.0f);
    }

    public void resize(float width, float height) {
        this.m_Width = width;
        this.m_Height = height;

        if(width > height)
            this.m_AspectRatio =  m_Width / m_Height;
        else
            this.m_AspectRatio = m_Height / m_Width;

        m_Projection.identity();
        m_Projection.ortho(-(m_Width / m_AspectRatio), m_Width / m_AspectRatio, -(m_Height / m_AspectRatio), m_Height / m_AspectRatio, -1.0f, 1.0f);
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
}