package git.eclipse.core.network;

import git.eclipse.core.game.Constants;
import git.eclipse.core.network.packets.Packet;
import git.eclipse.core.network.packets.Packet00Connect;
import git.eclipse.core.network.packets.Packet01Disconnect;
import git.eclipse.core.network.packets.PacketType;

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
            m_Socket.setSoTimeout(10000);

            m_Socket.connect(m_IPAddress, m_Port);
        } catch (UnknownHostException | SocketException e) {
            m_Connected = false;
            System.err.println(e.getMessage());
        }
    }

    public synchronized void start() {
        m_Connected = false;
        m_Thread = new Thread(this, "Client_Thread");
        m_Thread.start();
    }

    public synchronized void stop() {
        if(m_Connected) m_Connected = false;

        try {
            closeSocket();
            m_Thread.join(1);
        } catch (InterruptedException e) {

        }
    }

    @Override
    public void run() {
        initialize();

        while(m_Connected) {
            assert m_Socket != null;
            if(!m_Socket.isConnected() || m_Socket.isClosed()) {
                stop();
                continue;
            }

            byte[] data = new byte[Constants.MAX_PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            try {
                m_Socket.receive(packet);
                if(m_Socket == null || packet.getAddress() == null) continue;

                parseData(data);
            } catch (IOException ignored) { }
        }
    }

    private void initialize() {
        try {
            InetAddress localAddress = Inet4Address.getLocalHost();
            Packet00Connect connectPacket = new Packet00Connect(localAddress.getHostAddress(), m_Socket.getLocalPort());

            m_Connected = pingServer();
            sendData(connectPacket.getData());
        } catch (UnknownHostException ignored) { }
    }

    private boolean pingServer() {
        boolean connected;
        do {
            connected = testServer();
        } while(!connected);

        return connected;
    }

    private void closeSocket() {
        try {
            assert(m_Socket != null);
            if(!m_Socket.isClosed()) {
                InetAddress localAddress = Inet4Address.getLocalHost();
                Packet01Disconnect disconnectPacket = new Packet01Disconnect(localAddress.getHostAddress(), m_Socket.getLocalPort());
                sendData(disconnectPacket.getData());

                m_Socket.close();
            }
        } catch (UnknownHostException ignored) { }
    }

    private boolean testServer() {
        sendData("ping".getBytes());

        byte[] data = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            m_Socket.receive(packet);

            String message = new String(packet.getData()).trim();
            return message.equalsIgnoreCase("pong");
        } catch (IOException ignored) { return false; }
    }

    private void parseData(byte[] data) {
        String message = new String(data).trim();
        if(message.isEmpty()) return;

        Packet packet = null;
        PacketType type = PacketType.LookupPacket(message.substring(0, 2));

        switch (type) {
            default:
            case INVALID: break;


            case DISCONNECT:

                break;
        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress(m_IPAddress, m_Port));
        try {
            m_Socket.send(packet);
        } catch (Exception ignored) { }
    }

    public boolean isConnected() {
        return m_Connected;
    }
}
