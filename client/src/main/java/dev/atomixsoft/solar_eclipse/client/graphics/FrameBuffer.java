package dev.atomixsoft.solar_eclipse.client.graphics;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {

    private final int m_FramebufferId;
    private final int m_RenderBufferId;
    private final int m_ColorBufferId;

    private int m_Width, m_Height;

    public FrameBuffer(int width, int height) {
        this(width, height, GL_NEAREST);
    }

    public FrameBuffer(int width, int height, int minMagFilter) {
        m_Width = width;
        m_Height = height;

        m_FramebufferId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, m_FramebufferId);

        m_ColorBufferId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, m_ColorBufferId);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, m_Width, m_Height, 0, GL_RGBA, GL_UNSIGNED_BYTE, MemoryUtil.NULL);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minMagFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, minMagFilter);
        glBindTexture(GL_TEXTURE_2D, 0);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, m_ColorBufferId, 0);

        m_RenderBufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, m_RenderBufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, m_Width, m_Height);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, m_RenderBufferId);

        if(!isComplete()) {
            ClientThread.log().error("Frame Buffer wasn't complete!");
            unbind();
            dispose();
            throw new RuntimeException();
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, m_FramebufferId);
        glViewport(0, 0, m_Width, m_Height);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void dispose() {
        glDeleteFramebuffers(m_FramebufferId);
        glDeleteRenderbuffers(m_RenderBufferId);
        glDeleteTextures(m_ColorBufferId);
    }

    private boolean isComplete() {
        return glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
    }

    public int getColorBufferId() {
        return m_ColorBufferId;
    }

    public int getWidth() {
        return m_Width;
    }

    public int getHeight() {
        return m_Height;
    }

}
