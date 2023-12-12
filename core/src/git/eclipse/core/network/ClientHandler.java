package git.eclipse.core.network;

import java.io.IOException;
import java.net.*;

public class ClientHandler implements Runnable {

    protected DatagramSocket m_Socket;
    protected InetAddress m_IPAddress;
    protected int m_Port;

    protected Thread m_Thread;
    protected volatile boolean m_Connected;

    public ClientHandler(String ipAddress, int port) {
        try {
            m_IPAddress = InetAddress.getByName(ipAddress);
            m_Port = port;

            m_Socket = new DatagramSocket();
            m_Socket.setSoTimeout(29000);

            m_Connected = true;
        } catch (UnknownHostException | SocketException e) {
            System.err.println(e.getMessage());
            m_Connected = false;
        }
    }

    public synchronized void start() {
        m_Thread = new Thread(this, "Client_Thread");
        m_Thread.start();
    }

    public synchronized void stop() {
        if(m_Connected) m_Connected = false;
    }

    @Override
    public void run() {
        while(m_Connected) {
            if(!m_Socket.isConnected() && m_Socket.isClosed()) {
                stop();
                continue;
            }

            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                m_Socket.receive(packet);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop();
            }
        }

        try {
            if(m_Socket != null) m_Socket.close();
            m_Thread.join(1);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, m_IPAddress, m_Port);
        try {
            m_Socket.send(packet);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean isConnected() {
        return m_Connected;
    }
}
