package dev.atomixsoft.solar_eclipse.server.console;

import dev.atomixsoft.solar_eclipse.core.event.EventBus;
import dev.atomixsoft.solar_eclipse.core.event.interfaces.EventConsumer;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;
import dev.atomixsoft.solar_eclipse.server.console.events.CommandEvent;
import dev.atomixsoft.solar_eclipse.server.console.events.CommandListener;

import java.io.PrintStream;
import java.util.Scanner;

// My initial attempt to get a "proper" console server working.
// I don't do backend development so it's been put on the back burner for later use, maybe?
public class ConsoleThread implements Runnable, EventConsumer<ShutdownEvent> {

    private final EventBus m_EventBus;
    private final Runnable m_ShutdownCallback;
    private boolean m_Running;

    public ConsoleThread(EventBus eventBus, Runnable shutdownCallback) {
        m_EventBus = eventBus;
        m_ShutdownCallback = shutdownCallback;
    }

    private void initialize() {
        m_EventBus.register(ShutdownEvent.class, this);
        m_EventBus.register(CommandEvent.class, new CommandListener());

        m_Running = true;
    }

    @Override
    public void accept(ShutdownEvent event) {
        if(event.handled) return;

        m_Running = false;
        m_ShutdownCallback.run();

        event.handled = true;
    }

    @Override
    public void run() {
        initialize();

        Scanner in = new Scanner(System.in);

        while(m_Running) {
            System.out.print("> ");

            if(!in.hasNextLine())
                continue;

            String cmd = in.nextLine().trim();
            if(cmd.isEmpty())
                continue;

            m_EventBus.post(new CommandEvent(cmd));
        }
    }

}
