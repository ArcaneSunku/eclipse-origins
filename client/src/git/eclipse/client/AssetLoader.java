package git.eclipse.client;

import git.eclipse.core.graphics.Shader;
import git.eclipse.core.graphics.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class AssetLoader {

    // =========== INTERNAL DIRECTORIES ===========
    private static final String TEXTURES_DIR = "assets/graphics/";
    private static final String SHADERS_DIR = "assets/shaders/";

    private static final String SFX_DIR = "/assets/audio/sfx/";
    private static final String BGM_DIR = "/assets/audio/bgm/";
    // =========== INTERNAL DIRECTORIES ===========

    // =========== EXTERNAL DIRECTORIES ===========
    private static final String LOG_DIR = "assets/logs/";
    private static final String CACHED_MAPS_DIR = "assets/maps/";
    // =========== EXTERNAL DIRECTORIES ===========

    private static AssetLoader m_Instance = null;
    public static AssetLoader Instance() {
        if(m_Instance == null) {
            m_Instance = new AssetLoader();
            return m_Instance;
        }

        return m_Instance;
    }

    public static void Dispose() {
        Instance().dispose();
    }

    public static void AddTexture(String name, String path) {
        Instance().addTexture(name, path);
    }

    public static void AddShader(String name, String shaderFile) {
        Instance().addShader(name, shaderFile);
    }

    public static Texture GetTexture(String name) {
        return Instance().getTexture(name, false);
    }

    public static Texture GetTextureFromPath(String filePath) {
        return Instance().getTexture(filePath, true);
    }

    public static Shader GetShader(String name) {
        return Instance().getShader(name);
    }

    private final Map<String, Texture> m_TextureMap;
    private final Map<String, Shader> m_ShaderMap;

    private AssetLoader() {
        m_TextureMap = new HashMap<>();
        m_ShaderMap = new HashMap<>();
    }

    private void dispose() {
        for(Texture texture : m_TextureMap.values())
            texture.dispose();

        for(Shader shader : m_ShaderMap.values()) {
            shader.unbind();
            shader.dispose();
        }

        m_TextureMap.clear();
        m_ShaderMap.clear();

        m_Instance = null;
    }

    private void addTexture(String name, String path) {
        String fullPath = TEXTURES_DIR + path;
        for(Texture texture : m_TextureMap.values()) {
            String texturePath = texture.getFilepath();
            if(texturePath.equalsIgnoreCase(fullPath)) {
                System.err.printf("[%s] already exists in the TextureMap!%n", fullPath);
                return;
            }
        }

        Texture texture = new Texture(fullPath);
        m_TextureMap.put(name, texture);
    }

    private void addShader(String name, String shaderFile) {
        if(m_ShaderMap.containsKey(name)) {
            System.err.println("Already contains shader: " + name);
            return;
        }

        String fullPath = SHADERS_DIR + shaderFile;
        List<Shader.ShaderModuleData> dataList = new ArrayList<>();

        dataList.add(new Shader.ShaderModuleData(fullPath + ".vert", GL_VERTEX_SHADER));
        dataList.add(new Shader.ShaderModuleData(fullPath + ".frag", GL_FRAGMENT_SHADER));

        Shader shader = new Shader(dataList);
        m_ShaderMap.put(name, shader);
    }

    private Texture getTexture(String name, boolean filePath) {
        if(!filePath) { // Checks if the string give is a filepath or a Map Key
            if(!m_TextureMap.containsKey(name)) {
                System.err.println("Couldn't find Texture: " + name);
                return null;
            }

            return m_TextureMap.get(name);
        } else {
            for(Texture texture : m_TextureMap.values()) {
                if(texture.getFilepath().equalsIgnoreCase(name))
                    return texture;
            }

            System.err.println("Failed to find Texture from path: " + name);
            return null;
        }
    }

    private Shader getShader(String name) {
        if(!m_ShaderMap.containsKey(name)) {
            System.err.println("Couldn't find Shader: " + name);
            return null;
        }

        return m_ShaderMap.get(name);
    }

}
