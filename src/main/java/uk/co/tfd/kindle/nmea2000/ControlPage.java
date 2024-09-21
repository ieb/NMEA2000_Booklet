package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by ieb on 20/06/2020.
 */
public class ControlPage extends JPanel implements StatusUpdates.StatusUpdateListener, CanMessageListener {
    private static Logger log = LoggerFactory.getLogger(ControlPage.class);
    private final JButton invertButton;
    private final JButton exitButton;
    private final JLabel title;
    private final JLabel instructions;
    private final JPanel titles;
    private final JTextArea statusMessages;
    private final JPanel buttons;
    private final JButton connectButton;
    private final JButton disconnectButton;

    @Override
    public int[] getPgns() {
        log.info("Requested pgns");
        return new int[] {
                WindCalculator.PGN130306Wind.PGN
        };
    }

    @Override
    public void onDrop(int pgn) {

    }

    @Override
    public void onUnhandled(int pgn) {

    }

    @Override
    public void onMessage(CanMessage message) {
        if (message instanceof NavMessageHandler.PGN130306Wind) {
            NavMessageHandler.PGN130306Wind wind = (NavMessageHandler.PGN130306Wind) message;
            if (wind.windReference == N2KReference.WindReference.Apparent) {
                log.info("Got wind {}", wind);
            }
        } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
        }
    }


    public interface ControlHook {
        void invertColors();

        void exit();
    }


    public static class ThemePanel extends JPanel {

        public ThemePanel(LayoutManager layoutManager) {
            super(layoutManager);
        }
        @Override
        public void setForeground(Color fg) {
            super.setForeground(fg);
            for(Component c: this.getComponents()) {
                c.setForeground(fg);
            }
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
            for(Component c: this.getComponents()) {
                c.setBackground(bg);
            }
        }

    }

    private java.util.List<String> status = new ArrayList<String>();

    public ControlPage(ControlHook controlHook) {
        this.setLayout(new BorderLayout());

        invertButton = new JButton("Invert Colors");
        invertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlHook.invertColors();
            }
        });
        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlHook.exit();
            }
        });

        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");


        title = new JLabel("NMEA2000 Eink for Kindle");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        instructions = new JLabel("Swipe left or right for pages, and up for this screen");
        instructions.setHorizontalAlignment(JLabel.CENTER);
        instructions.setFont(new Font("Arial", Font.PLAIN, 10));

        titles = new ThemePanel(new GridLayout(2,1));
        titles.add(title);
        titles.add(instructions);

        this.add(titles, BorderLayout.PAGE_START);

        statusMessages = new JTextArea();
        statusMessages.setFont(new Font("Arial", Font.PLAIN, 10));
        statusMessages.setLineWrap(true);
        statusMessages.setEditable(false);
        statusMessages.setHighlighter(null);

        this.add(statusMessages, BorderLayout.CENTER);

        buttons = new ThemePanel(new BorderLayout());
        buttons.add(invertButton, BorderLayout.LINE_START);
        buttons.add(connectButton, BorderLayout.LINE_START);
        buttons.add(disconnectButton, BorderLayout.LINE_START);
        buttons.add(exitButton, BorderLayout.LINE_END);
        this.add(buttons, BorderLayout.PAGE_END);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        statusMessages.addMouseListener(l);
        super.addMouseListener(l);
    }

    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        statusMessages.addMouseMotionListener(l);
        super.addMouseMotionListener(l);
    }

    public void setTheme(MainScreen.Theme theme) {
        this.invertButton.setForeground(theme.getControlForeground());
        this.exitButton.setForeground(theme.getControlForeground());
    }


    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        for(Component c: this.getComponents()) {
            c.setForeground(fg);
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        for(Component c: this.getComponents()) {
            c.setBackground(bg);
        }
    }


    @Override
    public void onStatusChange(String text) {
        log.info("Status: {} ",text);
        statusMessages.append(text + "\n");
        Document d = statusMessages.getDocument();

        try {
            while (statusMessages.getLineCount() > 20) {
                d.remove(0, statusMessages.getLineEndOffset(0));
            }
        } catch ( BadLocationException e) {
            log.error(e.getMessage(), e);
        }
    }

}
