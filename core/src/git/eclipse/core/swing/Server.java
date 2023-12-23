package git.eclipse.core.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Server extends JFrame {
    private static Server m_Instance = null;
    private static Server Instance() { return m_Instance; }

    public static void PushMessage(String msg) {
        ((Console) Instance().m_ConsoleTab).pushMessage(msg);
    }
    public static String GetMotD() { return Instance().m_MotD; }

    private final JPanel m_MainPanel;
    private final JPanel m_ConsoleTab, m_ControlTab, m_PlayersTab;

    private String m_MotD;
    private volatile boolean m_Closing;

    public Server(String title, String motd) {
        super(title);
        m_MotD = motd;
        m_Closing = false;
        setVisible(false);
        addWindowListener(new WindowAdapter() {

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

        m_ConsoleTab = new Console();
        m_ControlTab = new Control();
        m_PlayersTab = new Players();

        if(m_Instance == null)
            m_Instance = this;
    }

    public void createUIComponents() {
        m_MainPanel.setLayout(new BorderLayout());
        m_MainPanel.setLocation(0, 0);
        m_MainPanel.setSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));

        final JTabbedPane pnlTabs = new JTabbedPane();
        m_MainPanel.add(pnlTabs, BorderLayout.CENTER);

        pnlTabs.add("Console", m_ConsoleTab);
        pnlTabs.add("Players", m_PlayersTab);
        pnlTabs.add("Control", m_ControlTab);
    }

    public void setupFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(475, 300));
        setLayout(null);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        add(m_MainPanel, BorderLayout.CENTER);
    }

    public void setMotD(String motd) {
        m_MotD = motd;
    }

    public Console getConsoleTab() {
        return (Console) m_ConsoleTab;
    }

    public Control getControlTab() {
        return (Control) m_ControlTab;
    }

    public Players getPlayersTab() {
        return (Players) m_PlayersTab;
    }

    public boolean isClosing() {
        return m_Closing;
    }

}
