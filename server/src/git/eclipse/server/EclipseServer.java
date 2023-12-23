package git.eclipse.server;

import git.eclipse.core.network.ServerData;
import git.eclipse.core.network.ServerHandler;
import git.eclipse.core.swing.Console;
import git.eclipse.core.swing.Server;

public class EclipseServer implements Runnable {

    private final ServerData m_Data;
    private final Thread m_Thread;
    private final Server m_Frame;

    private ServerHandler m_Server;
    private volatile boolean m_Running;

    public EclipseServer(ServerData data) {
        m_Data = data;
        m_Running = false;
        m_Frame = new Server(data.toString(), data.MotD);
        m_Thread = new Thread(this, "Main_Thread");
    }

    public synchronized void start() {
        if(m_Running) return;

        m_Server = new ServerHandler(m_Data.IP.getHostName(), m_Data.Port);
        m_Server.start();

        m_Running = true;
        m_Thread.start();
    }

    public synchronized void stop() {
        if(!m_Running) return;
        m_Running = false;
    }

    private void process() {
        Console console = m_Frame.getConsoleTab();
    }

    private void dispose() {
        if(m_Running) m_Running = false;
        try {
            if(!m_Frame.isClosing())
                m_Frame.dispose();

            if(m_Server.isRunning())
                m_Server.stop();

            m_Thread.join(1);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        m_Frame.setupFrame();
        m_Frame.createUIComponents();

        Console console = m_Frame.getConsoleTab();
        console.pushMessage(m_Data.MotD);

        while(m_Running) {
            if(m_Frame.isClosing() || console.shouldClose()) {
                stop();
                continue;
            }

            process();
        }

        dispose();
    }
}
