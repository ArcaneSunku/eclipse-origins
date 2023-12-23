package git.eclipse.core.network;

import git.eclipse.core.game.Constants;
import git.eclipse.core.network.packets.Packet00Connect;
import git.eclipse.core.network.packets.Packet01Disconnect;
import git.eclipse.core.network.packets.PacketType;
import git.eclipse.core.swing.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler implements Runnable {
    protected List<ClientData> m_ConnectedPlayers;
    protected DatagramSocket m_Socket;
    protected InetAddress m_IP;
    protected int m_Port;

    protected Thread m_Thread;
    protected volatile boolean m_Running;

    public ServerHandler(String ip, int port) {
        try {
            m_IP = InetAddress.getByName(ip);
            m_Port = port;

            m_Socket = new DatagramSocket(m_Port, null);
            m_Socket.setSoTimeout(10000);

            m_ConnectedPlayers = new ArrayList<>();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void start() {
        m_Thread = new Thread(this, "Server_Thread");
        m_Running = m_Socket.isBound();
        m_Thread.start();
    }

    public synchronized void stop() {
        if(m_Running) m_Running = false;

        try {
            if(m_Socket != null && !m_Socket.isClosed()) m_Socket.close();
            m_Thread.join(1);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        while(m_Running) {
            if(m_Socket == null || m_Socket.isClosed()) {
                stop();
                continue;
            }

            byte[] data = new byte[Constants.MAX_PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                m_Socket.receive(packet);
            } catch (IOException ignored) { }

            if(m_Socket == null || packet.getAddress() == null) continue;
            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    private void parsePacket(byte[] data, InetAddress ip, int port) {
        String message = new String(data).trim();
        PacketType type = PacketType.LookupPacket(message.substring(0, 2));

        switch (type) {
            default:
            case INVALID: {

                break;
            }

            case CONNECT: {
                Packet00Connect packet = new Packet00Connect(data);
                ClientData cl = new ClientData();
                cl.IP = packet.getIP();
                cl.Port = packet.getPort();

                boolean alreadyConnected = false;
                for(ClientData client : m_ConnectedPlayers) {
                    if(cl.IP.equals(client.IP) && cl.Port == client.Port) {
                        alreadyConnected = true;
                        System.err.println("Failed to connect, this connection is already established!");
                    }
                }

                if(!alreadyConnected) {
                    m_ConnectedPlayers.add(cl);
                    Server.PushMessage(String.format("%s:%d connected!", cl.IP, cl.Port));
                }

                break;
            }

            case DISCONNECT: {
                Packet01Disconnect packet = new Packet01Disconnect(data);
                String dcIP = packet.getIP();
                int dcPort = packet.getPort();

                for(ClientData client : m_ConnectedPlayers) {
                    if(client.IP.equals(dcIP) && client.Port == dcPort) {
                        m_ConnectedPlayers.remove(client);
                        Server.PushMessage(String.format("%s:%d disconnected!", client.IP, client.Port));
                        break;
                    }
                }

                break;
            }
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

    public void sendDataToAll(byte[] data) {
        for(ClientData cl : m_ConnectedPlayers) {
            try {
                sendData(data, InetAddress.getByName(cl.IP), cl.Port);
            } catch (UnknownHostException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException("Problem while sending data to all clients!");
            }
        }
    }

    public boolean isRunning() {
        return m_Running;
    }

}
