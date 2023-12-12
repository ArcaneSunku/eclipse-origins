package git.eclipse.core.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerHandler implements Runnable {

    protected DatagramSocket m_Socket;
    protected int m_Port;

    protected Thread m_Thread;
    protected volatile boolean m_Running;

    public ServerHandler(int port) {
        try {
            m_Port = port;
            m_Socket = new DatagramSocket(m_Port);
            m_Socket.setSoTimeout(29000);
        } catch (SocketException e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void start() {
        m_Thread = new Thread(this, "Server_Thread");
        m_Running = true;
        m_Thread.start();
    }

    public synchronized void stop() {
        if(m_Running) m_Running = false;
    }

    @Override
    public void run() {
        while(m_Running) {
            if(m_Socket.isClosed()) {
                stop();
                continue;
            }

            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                m_Socket.receive(packet);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            if(m_Socket != null && !m_Socket.isClosed())
                m_Socket.close();

            m_Thread.join(1);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            m_Socket.send(packet);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean isRunning() {
        return m_Running;
    }
}
