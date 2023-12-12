package git.eclipse.server.ui;

import git.eclipse.server.EclipseServer;
import git.eclipse.server.Main;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Console extends JPanel {
    private JEditorPane m_ConsolePane;
    private JTextField m_ConsoleInput;

    private boolean shouldClose;

    public Console() {
        super();
        shouldClose = false;
        createUIComponents();

        setLayout(new BorderLayout());
        setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

        final JScrollPane scrollPane = new JScrollPane(m_ConsolePane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
        add(m_ConsoleInput, BorderLayout.SOUTH);
    }

    private void createUIComponents() {
        m_ConsolePane = new JEditorPane();
        m_ConsolePane.setEditable(false);
        m_ConsolePane.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

        m_ConsoleInput = new JTextField();
        m_ConsoleInput.setPreferredSize(new Dimension(getWidth(), 22));
        m_ConsoleInput.setMinimumSize(m_ConsoleInput.getPreferredSize());
        m_ConsoleInput.setMaximumSize(m_ConsoleInput.getPreferredSize());
        m_ConsoleInput.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

        m_ConsoleInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = m_ConsoleInput.getText();

                    if(input.equals("/cls")) {
                        clearConsole();
                    } else if(input.equals("/motd")) {
                        pushMessage(EclipseServer.GetMotD());
                    } else if(input.equals("/exit")) {
                        shouldClose = true;
                    }else {
                        pushMessage(input);
                    }

                    m_ConsoleInput.setText("");
                }
            }
        });
    }

    public void pushMessage(String msg) {
        String collectiveMessage = m_ConsolePane.getText() +
                msg + "\n";

        m_ConsolePane.setText(collectiveMessage);
    }

    public void clearConsole() {
        m_ConsolePane.setText("");
    }

    public boolean shouldClose() {
        return shouldClose;
    }

}
