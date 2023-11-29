package git.eclipse.core.utils.connect;

import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket m_Socket;
    private Thread m_ClientThread;

    public ClientHandler(Socket socket) {
        m_Socket = socket;
        m_ClientThread = new Thread(this);
    }

    public void start() {
        
    }

    @Override
    public void run() {
        while(!m_Socket.isClosed()) {
            // Stuff to do
        }
    }

    public Thread getThread() {
        return m_ClientThread;
    }
}
