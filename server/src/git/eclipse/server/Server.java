package git.eclipse.server;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private JFrame m_Window;
    private boolean m_Closing;

    private ServerSocket m_Server;
    private final List<Socket> m_ConnectedClients;

    private Thread m_Thread;
    private volatile boolean m_Running;

    public Server(String title, int width, int height) {
        Dimension dimension = new Dimension(width, height);
        m_ConnectedClients = new ArrayList<>();
        m_Running = false;

        m_Window = new JFrame(title);
        m_Window.setVisible(false);

        m_Window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                m_Closing = false;
            }

            @Override
            public void windowClosing(WindowEvent e) {
                m_Closing = true;
            }

            @Override
            public void windowClosed(WindowEvent e) {
                m_Closing = true;
            }
        });

        m_Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_Window.setMinimumSize(dimension);
        m_Window.setMaximumSize(dimension);
        m_Window.setSize(dimension);
        m_Window.pack();
        m_Window.setLayout(new BorderLayout());
        m_Window.setLocationRelativeTo(null);

    }

    private void populate_window(JFrame frame) {
        final JPanel m_Panel = new JPanel();
        final JButton m_Start, m_Stop;

        m_Start = new JButton("Start");
        m_Stop = new JButton("Stop");

        m_Start.setLocation(100, 150);
        m_Stop.setLocation(100, m_Start.getY() + m_Start.getHeight() + 10);

        

        m_Panel.setLayout(new BorderLayout());
        m_Panel.add(m_Start, BorderLayout.NORTH);
        m_Panel.add(m_Stop, BorderLayout.EAST);

        frame.add(m_Panel, BorderLayout.CENTER);
    }

    public synchronized void start(int port) throws IOException {
        if(m_Running) return;

        m_Server = new ServerSocket(port, 25);
        m_Thread = new Thread(this, "Server_Thread");

        m_Running = true;
        m_Thread.start();
    }

    public synchronized void stop() {
        try {
            if(m_Running)
                m_Running = false;

            m_Thread.join(1L);
            m_Server.close();

            m_Window.dispose();
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        populate_window(m_Window);
        m_Window.setVisible(true);

        while(m_Running) {
            if(m_Closing) {
                m_Running = false;
                continue;
            }
        }

        stop();
    }

}
