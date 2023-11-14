package git.eclipse.server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            Server server = new Server("Eclipse Origins - Server", 800, 480);
            server.start(1101);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

}
