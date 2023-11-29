package git.eclipse.server;

import git.eclipse.core.utils.connect.ServerData;
import git.eclipse.server.ui.Console;
import git.eclipse.server.ui.Control;
import git.eclipse.server.ui.Players;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Server implements Runnable {

    private final JFrame m_Frame;
    private final JPanel m_MainPanel;

    private final ServerData m_Data;

    private final Thread m_MainThread;
    private volatile boolean m_Running, m_Closing;

    private JPanel m_ConsoleTab, m_ControlTab, m_PlayersTab;

    public Server(String title, String motd, String ip, int port) {
        m_Data = new ServerData(title, ip, port, motd);
        m_Frame = new JFrame(m_Data.toString());
        m_Frame.setVisible(false);
        m_Frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                m_Closing = false;
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                m_Closing = true;
            }
        });

        m_MainPanel = new JPanel();
        m_MainThread = new Thread(this, "Main_Thread");
    }

    private void createUIComponents() {
        m_MainPanel.setLayout(new BorderLayout());
        m_MainPanel.setLocation(0, 0);
        m_MainPanel.setSize(new Dimension(m_Frame.getContentPane().getWidth(), m_Frame.getContentPane().getHeight()));

        final JTabbedPane pnlTabs = new JTabbedPane();
        m_MainPanel.add(pnlTabs, BorderLayout.CENTER);

        m_ConsoleTab = new Console();
        pnlTabs.add("Console", m_ConsoleTab);

        m_PlayersTab = new Players();
        pnlTabs.add("Players", m_PlayersTab);

        m_ControlTab = new Control();
        pnlTabs.add("Control", m_ControlTab);
    }

    private void setupFrame() {
        m_Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        m_Frame.setPreferredSize(new Dimension(475, 300));
        m_Frame.setLayout(null);
        m_Frame.pack();
        m_Frame.setLocationRelativeTo(null);
        m_Frame.setResizable(false);
        m_Frame.setVisible(true);

        m_Frame.add(m_MainPanel, BorderLayout.CENTER);
    }

    public void start() {
        setupFrame();
        createUIComponents();

        if(!m_Running) m_Running = true;
        m_MainThread.start();
    }

    private void dispose() {
        try {
            m_MainThread.join(1);
            System.exit(0);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        Console console = (Console) m_ConsoleTab;
        console.pushMessage("MoTD: " + m_Data.MotD);

        while(m_Running) {
            if(m_Closing || console.shouldClose()) {
                m_Running = false;
                continue;
            }

            while(!m_Closing) {

            }
        }

        dispose();
    }

    public String getIP() {
        return m_Data.IP;
    }

    public int getPort() {
        return m_Data.Port;
    }
}
