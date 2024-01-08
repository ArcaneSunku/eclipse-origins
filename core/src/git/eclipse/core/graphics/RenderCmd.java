package git.eclipse.core.graphics;

import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

/**
 * <p>A static class meant to essentially wrap OpenGL related functions.</p>
 * <p>Will have static methods related to general/non-specific OpenGL functionality.</p>
 *
 * <p>Example:</p>
 * <ul>
 *     <li>Clearing the Screen</li>
 *     <li>Setting a Clear Color</li>
 * </ul>
 */
public class RenderCmd {

    public static void initialize() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void clearColor(int r, int g, int b) {
        float red = r > 255 ? Math.min(255.f, r) : Math.max(r, 0.f);
        float green = g > 255 ? Math.min(255.f, g) : Math.max(g, 0.f);
        float blue = b > 255 ? Math.min(255.f, b) : Math.max(b, 0.f);

        if(red != 0)
            red = red / 255.f;

        if(green != 0)
            green = green / 255.f;

        if(blue != 0)
            blue = blue / 255.f;

        Vector3f normalized = new Vector3f(red, green, blue);
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
