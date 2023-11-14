package git.eclipse.core.graphics;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class RenderCmd {

    public static void initialize() {
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void clearColor(int r, int g, int b) {
        float red, grn, blu;
        if(r > 255)
            red = 1.0f;
        else if(r <= 0)
            red = 0.0f;
        else
            red = r / 255.f;

        if(g > 255)
            grn = 1.0f;
        else if(g <= 0)
            grn = 0.0f;
        else
            grn = g / 255.f;

        if(b > 255)
            blu = 1.0f;
        else if(b <= 0)
            blu = 0.0f;
        else
            blu = b / 255.f;

        Vector3f normalized = new Vector3f(red, grn, blu);
        clearColor(normalized.x, normalized.y, normalized.z);
    }

    public static void clearColor(float r, float g, float b) {
        glClearColor(r, g, b, 1.0f);
    }

    public static void clear() {
        clear(true);
    }

    public static void clear(boolean clearDepth) {
        int clearBit = GL_COLOR_BUFFER_BIT;
        if(clearDepth)
            clearBit |= GL_DEPTH_BUFFER_BIT;

        glClear(clearBit);
    }

}
