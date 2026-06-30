package dev.atomixsoft.solar_eclipse.client.graphics;

import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.glGetInteger;

public final class RenderCapabilities {
    private static final int PREFERRED_TEXTURE_SLOTS = 32;
    private static final int MINIMUM_TEXTURE_SLOTS = 8;

    private static boolean s_Initialized = false;
    private static String s_GLVersion = "unknown";
    private static String s_GLSLVersion = "unknown";
    private static int s_MaxFragmentTextureUnits = 0;
    private static int s_MaxCombinedTextureUnits = 0;
    private static int s_MaxBatchTextureSlots = MINIMUM_TEXTURE_SLOTS;

    private RenderCapabilities() { }

    public static void Initialize() {
        s_GLVersion = glGetString(GL_VERSION);
        s_GLSLVersion = glGetString(GL_SHADING_LANGUAGE_VERSION);
        s_MaxFragmentTextureUnits = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);
        s_MaxCombinedTextureUnits = glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);

        if(s_MaxFragmentTextureUnits < MINIMUM_TEXTURE_SLOTS) {
            throw new IllegalStateException(
                    "OpenGL driver supports only " + s_MaxFragmentTextureUnits
                            + " fragment texture units; at least " + MINIMUM_TEXTURE_SLOTS + " are required.");
        }

        s_MaxBatchTextureSlots = Math.min(s_MaxFragmentTextureUnits, PREFERRED_TEXTURE_SLOTS);
        s_Initialized = true;
    }

    public static int GetMaxBatchTextureSlots() {
        ensureInitialized();
        return s_MaxBatchTextureSlots;
    }

    public static String GetSummary() {
        ensureInitialized();
        return "OpenGL=" + s_GLVersion
                + ", GLSL=" + s_GLSLVersion
                + ", fragmentTextureUnits=" + s_MaxFragmentTextureUnits
                + ", combinedTextureUnits=" + s_MaxCombinedTextureUnits
                + ", batchTextureSlots=" + s_MaxBatchTextureSlots;
    }

    private static void ensureInitialized() {
        if(!s_Initialized)
            throw new IllegalStateException("Render capabilities have not been initialized.");
    }
}
