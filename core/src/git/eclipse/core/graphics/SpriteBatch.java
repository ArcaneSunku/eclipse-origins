package git.eclipse.core.graphics;

public class SpriteBatch {

    private Shader m_BatchShader;
    private boolean m_Rendering;

    public SpriteBatch(Shader shader) {
        m_BatchShader = shader;
        m_Rendering = false;
    }

    public void begin() {
        if(m_Rendering) throw new IllegalStateException("You have to call end() before calling begin() when rendering.");
    }

    public void end() {

    }

    public Shader getShader() {
        return m_BatchShader;
    }

}
