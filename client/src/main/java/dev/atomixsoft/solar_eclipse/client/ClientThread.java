package dev.atomixsoft.solar_eclipse.client;

import dev.atomixsoft.solar_eclipse.client.events.PacketListener;
import dev.atomixsoft.solar_eclipse.client.events.ShutdownListener;
import dev.atomixsoft.solar_eclipse.client.logging.Logger;

import dev.atomixsoft.solar_eclipse.client.net.ClientSession;
import dev.atomixsoft.solar_eclipse.client.net.NetworkClient;
import dev.atomixsoft.solar_eclipse.client.scene.MainScene;
import dev.atomixsoft.solar_eclipse.client.util.ImGuiManager;
import dev.atomixsoft.solar_eclipse.client.util.input.Controller;
import dev.atomixsoft.solar_eclipse.core.event.EventBus;
import dev.atomixsoft.solar_eclipse.core.event.types.InputEvent;
import dev.atomixsoft.solar_eclipse.core.event.types.SendPacketEvent;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;
import dev.atomixsoft.solar_eclipse.core.net.data.InventorySlotData;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.PacketRegistry;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.*;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.*;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

import dev.atomixsoft.solar_eclipse.client.util.Window;

import dev.atomixsoft.solar_eclipse.client.util.input.InputHandler;

import dev.atomixsoft.solar_eclipse.client.audio.AudioMaster;

import dev.atomixsoft.solar_eclipse.client.graphics.RenderCmd;

import dev.atomixsoft.solar_eclipse.client.scene.SceneHandler;
import dev.atomixsoft.solar_eclipse.client.scene.MenuScene;
import dev.atomixsoft.solar_eclipse.client.scene.TestScene;


public class ClientThread implements Runnable {
    private static ClientThread s_Instance = null;
    public static Logger log() {
        return s_Instance.m_Logger;
    }
    public static NetworkClient network() { return s_Instance.m_Network; }
    public static EventBus eventBus() {
        return s_Instance.m_EventBus;
    }
    public static Vector2f size() {
        return new Vector2f(s_Instance.m_Window.getFrameBufferWidth(), s_Instance.m_Window.getFrameBufferHeight());
    }
    public static String get_scene_name() { return s_Instance.m_Scenes.getActiveSceneName(); }

    public static void set_size(int width, int height) {
        s_Instance.m_Window.requestResize(width, height);
    }
    public static void set_scene(String name) {
        s_Instance.m_Scenes.setActiveScene(name);
    }

    private final Controller m_Controller;
    private final EventBus m_EventBus;
    private final Thread m_Thread;
    private final Logger m_Logger;

    private volatile boolean m_Running;

    private String m_Title;
    private GLFWErrorCallback m_ErrorCallback;
    private Window m_Window;
    private SceneHandler m_Scenes;
    private ImGuiManager m_GUIManager;

    private NetworkClient m_Network;

    public ClientThread(String title, Logger logger) {
        m_Title = title;
        m_Running = false;

        this.m_Controller = new Controller();
        this.m_EventBus = new EventBus();

        this.m_Thread = new Thread(this, "Main_Thread");
        this.m_Logger = logger;
        this.m_GUIManager = new ImGuiManager();

        if(s_Instance == null) s_Instance = this;

        m_EventBus.register(ShutdownEvent.class, new ShutdownListener(this));
    }

    public synchronized void start() {
        if(m_Running)
            return;

        m_ErrorCallback = GLFWErrorCallback.createPrint(System.err);
        if(!glfwInit()) {
            this.m_Logger.error("Failed to initialize GLFW!");
            throw new RuntimeException("Failed to initialize the program!");
        }

        m_Running = true;
        m_Thread.start();
    }

    public synchronized void stop() {
        if(!m_Running)
            return;

        m_Running = false;
    }

    private void initialize() {
        m_GUIManager.init(m_Window.getHandle(), "#version 130");
        AudioMaster.Init();

        loadGUITextures("menu");
        loadGUITextures("main");

        loadNonUITextures("animation", 3);
        loadNonUITextures("character", 3);
        loadNonUITextures("face", 3);
        loadNonUITextures("item", 14);
        loadNonUITextures("tileset", 2);

        AssetLoader.AddShader("basic", "basic");

        m_Scenes.addScene("Test", new TestScene());
        m_Scenes.addScene("Menu", new MenuScene());
        m_Scenes.addScene("Main", new MainScene());

        m_Scenes.setActiveScene("Menu");
        m_Scenes.getActiveScene().resize(m_Window.getWidth(), m_Window.getHeight());

        PacketRegistry.Initialize();
        m_Network = new NetworkClient();
        try {
            m_Network.connect(Client.ConfigInfo.getIP(), Client.ConfigInfo.getPort(), m_Logger);
        } catch (Exception e) {
            m_Logger.error(e.getMessage());
        }

        if(m_Network.isConnected())
            m_EventBus.register(SendPacketEvent.class, new PacketListener(m_Network));
    }

    private void dispose() {
        this.m_Logger.debug("Client thread cleaning up...");

        AudioMaster.CleanUp();

        if(m_Scenes != null)
            m_Scenes.dispose();

        if(m_Network != null)
            m_Network.disconnect();

        if(m_Window != null)
            m_Window.close();

        m_GUIManager.dispose();
        AssetLoader.Dispose();
        m_EventBus.shutdown();

        if(m_ErrorCallback != null) {
            m_ErrorCallback.free();
            glfwSetErrorCallback(null);
        }

        glfwTerminate();
        System.exit(0);
    }

    @Override
    public void run() {
        this.m_Logger.debug("Client thread running...");

        m_Window = new Window(m_Title, 515, 352, true, true);
        m_Window.show();

        RenderCmd.Init();
        m_Scenes = new SceneHandler(m_Controller, m_Window);
        initialize();

        double accumulator = 0.0;
        double optimal = 1.0 / 60.0;
        double currentTime = System.nanoTime() / 1e9;
        double newTime, frameTime;

        InputHandler input = InputHandler.Instance();
        ClientThread.eventBus().register(InputEvent.class, InputHandler.Instance());
        while(m_Running) {
            if(m_Window.shouldClose()) {
                stop();
                continue;
            }

            newTime = System.nanoTime() / 1e9;
            frameTime = newTime - currentTime;
            currentTime = newTime;
            accumulator += frameTime;

            processIncomingPackets();
            while(accumulator >= optimal) {
                input.process();
                m_Scenes.update(optimal);

                accumulator -= optimal;
            }

            RenderCmd.Clear();

            m_Window.poll();
            m_Window.beginFrame();

            if(m_Window.resizeRequested()) {
                m_Window.applyPendingResize();
                m_Scenes.resize((int) size().x, (int) size().y);
            }

            m_Scenes.render(m_GUIManager);

            m_Window.endFrame();

            if(!m_Window.vSyncEnabled())
                sleep(currentTime);
        }

        dispose();
    }

    private void processIncomingPackets() {
        Packet packet;

        while((packet = m_Network.poll()) != null)
            handlePacket(packet);
    }

    private void handlePacket(Packet packet) {
        switch (packet) {

            case LoginResponse p -> {
                m_Logger.info("Login status: " + p.success() + " - " + p.message());
                if(!p.success())
                    break;

                ClientSession.Login(p.username(), p.playerEntityId(), p.mapId());
                if(p.playerEntityId() != -1) {
                    set_size(785, 594);
                    set_scene("Test");
                }
            }

            case CharacterListResponse p -> {
                m_Scenes.handlePackets(p);
            }

            case EntityPositionUpdate p -> {
                m_Scenes.handlePackets(p);
            }

            case MapLoad p -> {
                m_Scenes.handlePackets(p);
            }

            case PlayerStatsSnapshot p -> {
                m_Scenes.handlePackets(p);
            }

            case ChatMessageBroadcast p -> {
                m_Scenes.handlePackets(p);
            }

            case ShutdownNotification p -> {
                m_Logger.info("Server has shutdown...");
                m_EventBus.post(new ShutdownEvent("Server", true));
            }

            case EntityDespawn p -> {
                m_Scenes.handlePackets(p);
            }

            case EntitySpawn p -> {
                m_Scenes.handlePackets(p);
            }

            case InventorySnapshotPacket p -> {
                m_Scenes.handlePackets(p);
            }

            case ItemDefinitionSnapshotPacket p -> {
                m_Scenes.handlePackets(p);
            }

            default -> {
                m_Logger.error("Unhandled packet: " + packet.getClass().getSimpleName());
            }
        }
    }

    private void sleep(double currentTime) {
        double desiredTime = 1.0 / 60.0;
        long sleepTime = (long) ((currentTime - System.nanoTime() + desiredTime) / 1e9);

        try {
            if (sleepTime > 0)
                Thread.sleep(sleepTime);

        } catch (InterruptedException e) {
            m_Logger.debug(e.getMessage());
        }
    }

    /**
     * Loads the GUI textures related to the name you pass. </br>
     * This will search for a folder with the given name and load what you specify from there. </br>
     * Calls to {@link #loadButtonTextures(String, String)} should be called in here somewhere.
     *
     * @param name the name of the folder you want to load the UI textures for
     */
    private void loadGUITextures(String name) {
        if(name.equalsIgnoreCase("main")) {
            AssetLoader.AddTexture("ui_" + name + "_bank",       "gui/" + name + "/bank.jpg");
            AssetLoader.AddTexture("ui_" + name + "_character",  "gui/" + name + "/character.jpg");
            AssetLoader.AddTexture("ui_" + name + "_itemDesc",   "gui/" + name + "/description_item.jpg");
            AssetLoader.AddTexture("ui_" + name + "_spellDesc",  "gui/" + name + "/description_spell.jpg");
            AssetLoader.AddTexture("ui_" + name + "_dragbox",    "gui/" + name + "/dragbox.jpg");
            AssetLoader.AddTexture("ui_" + name + "_hotbar",     "gui/" + name + "/hotbar.jpg");
            AssetLoader.AddTexture("ui_" + name + "_inventory",  "gui/" + name + "/inventory.jpg");
            AssetLoader.AddTexture("ui_" + name + "_main",       "gui/" + name + "/main.jpg");
            AssetLoader.AddTexture("ui_" + name + "_options",    "gui/" + name + "/options.jpg");
            AssetLoader.AddTexture("ui_" + name + "_party",      "gui/" + name + "/party.jpg");
            AssetLoader.AddTexture("ui_" + name + "_shop",       "gui/" + name + "/shop.jpg");
            AssetLoader.AddTexture("ui_" + name + "_skills",     "gui/" + name + "/skills.jpg");
            AssetLoader.AddTexture("ui_" + name + "_trade",      "gui/" + name + "/trade.jpg");

            AssetLoader.AddTexture("ui_" + name + "_health_bar",       "gui/" + name + "/bars/health.jpg");
            AssetLoader.AddTexture("ui_" + name + "_spirit_bar",       "gui/" + name + "/bars/spirit.jpg");
            AssetLoader.AddTexture("ui_" + name + "_exp_bar",          "gui/" + name + "/bars/experience.jpg");
            AssetLoader.AddTexture("ui_" + name + "_party_health_bar", "gui/" + name + "/bars/party_health.jpg");
            AssetLoader.AddTexture("ui_" + name + "_party_spirit_bar", "gui/" + name + "/bars/party_spirit.jpg");

            loadButtonTextures(name, "char");
            loadButtonTextures(name, "exit");
            loadButtonTextures(name, "inv");
            loadButtonTextures(name, "opt");
            loadButtonTextures(name, "party");
            loadButtonTextures(name, "skills");
            loadButtonTextures(name, "trade");
        } else if(name.equalsIgnoreCase("menu")) {
            AssetLoader.AddTexture("ui_" + name + "_background", "gui/" + name + "/background.jpg");
            AssetLoader.AddTexture("ui_" + name + "_character",  "gui/" + name + "/character.jpg");
            AssetLoader.AddTexture("ui_" + name + "_credits",    "gui/" + name + "/credits.jpg");
            AssetLoader.AddTexture("ui_" + name + "_loading",    "gui/" + name + "/loading.jpg");
            AssetLoader.AddTexture("ui_" + name + "_login",      "gui/" + name + "/login.jpg");
            AssetLoader.AddTexture("ui_" + name + "_main",       "gui/" + name + "/main.jpg");
            AssetLoader.AddTexture("ui_" + name + "_register",   "gui/" + name + "/register.jpg");

            loadButtonTextures(name, "credits");
            loadButtonTextures(name, "exit");
            loadButtonTextures(name, "login");
            loadButtonTextures(name, "register");
        }
    }

    /**
     * Loads the button textures in the relative paths to the menu fold you specify.</br>
     * Keep in mind, this loads the click, hover, and idle versions of the button, no need to do it separately.
     *
     * @param uiName the name of the folder we search the GUI folder for
     * @param name name of the button you want to add.
     */
    private void loadButtonTextures(String uiName, String name) {
        AssetLoader.AddTexture("btn_" + uiName + "_" + name + "_click", "gui/" + uiName + "/buttons/" + name + "_click.jpg");
        AssetLoader.AddTexture("btn_" + uiName + "_" + name + "_hover", "gui/" + uiName + "/buttons/" + name + "_hover.jpg");
        AssetLoader.AddTexture("btn_" + uiName + "_" + name, "gui/" + uiName + "/buttons/" + name + "_norm.jpg");
    }

    private void loadNonUITextures(String name, int amount) {
        for(var i = 1; i <= amount; ++i)
            AssetLoader.AddTexture(name + i, name + "s/" + i + ".bmp");
    }
}
