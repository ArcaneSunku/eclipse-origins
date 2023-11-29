package git.eclipse.core.graphics;

public class Texture {

    private String m_Filepath;
    private int m_TextureId;
    private int m_Width, m_Height;
    private byte[] m_ImageData;
    private boolean m_Bound;

    public Texture(String filepath) {
        m_Filepath = filepath;
        m_Bound = false;
    }

    public void bind() {

    }

    public void unbind() {

    }

    public void dispose() {

    }

    public int getId() {
        return m_TextureId;
    }

    public int getWidth() {
        return m_Width;
    }

    public int getHeight() {
        return m_Height;
    }

    public boolean isBound() {
        return m_Bound;
    }

    public String getFilepath() {
        return m_Filepath;
    }

}
