package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static uk.co.tfd.kindle.nmea2000.Util.*;

/**
 * Created by ieb on 20/06/2020.
 */
public class ControlPage extends JPanel implements StatusUpdates.StatusUpdateListener, CanMessageListener {
    private static Logger log = LoggerFactory.getLogger(ControlPage.class);
    private final Button invertButton;
    private final Button exitButton;
    private final JLabel title;
    private final JLabel instructions;
    private final JLabel connection;
    private final JTextArea statusMessages;
    private final Button[] pageButtons = new Button[12];
    private final Font titleFont;
    private final Font instructionsFont;
    private final Font statusMessageFont;

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
                log.debug("Got wind {}", wind);
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

        this.setLayout(null);
        this.setBounds(0, 0, scaleKindle(KINDLE_FRAME_WIDTH), scaleKindle(KINDLE_FRAME_HEIGHT) );



        this.titleFont = Util.createFont( 20.0f); // main values
        this.instructionsFont = Util.createFont( 12.0f);
        this.statusMessageFont = Util.createFont( 10.0f);


        title = new JLabel("NMEA2000 Eink for Kindle ");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(titleFont);
        this.add(title);
        Dimension s = title.getPreferredSize();
        title.setBounds(scaleKindle(15), scaleKindle(15), s.width, s.height );


        instructions = new JLabel("Swipe down to get back to this screen.");
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructions.setFont(instructionsFont);
        this.add(instructions);
        s = instructions.getPreferredSize();
        instructions.setBounds(scaleKindle(15), scaleKindle(72), s.width, s.height );
        Action action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Action performed {} ", e);
            }
        };


        for (int i = 0, k = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++, k++) {
                pageButtons[k] = addButton(i,j, "page_"+k,"empty");
                pageButtons[k].setVisible(false);
            }
        }

        connection = new JLabel("Disconnected");
        connection.setFont(instructionsFont);
        this.add(connection);
        s = connection.getPreferredSize();
        connection.setBounds(scaleKindle(65), scaleKindle(980), scaleKindle(934), s.height );


        statusMessages = new JTextArea();
        statusMessages.setFont(statusMessageFont);
        statusMessages.setLineWrap(true);
        statusMessages.setEditable(false);
        statusMessages.setHighlighter(null);

        JScrollPane statusMessagesScroll = new JScrollPane(statusMessages);
        statusMessagesScroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        this.add(statusMessagesScroll);
        statusMessagesScroll.setBounds(scaleKindle(65), scaleKindle(1059), scaleKindle(934),scaleKindle(200));



        invertButton = new Button("invertColors", "Invert Colors");
        invertButton.setAction(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controlHook.invertColors();
            }
        });
        this.add(invertButton);
        s = invertButton.getPreferredSize();
        int buttonSize =  scaleKindle(100);
        invertButton.setBounds(scaleKindle(600), scaleKindle(1276), buttonSize*2, buttonSize );

        exitButton = new Button("exit", "Exit");
        exitButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlHook.exit();
            }
        });
        this.add(exitButton);
        s = exitButton.getPreferredSize();
        exitButton.setBounds(scaleKindle(850), scaleKindle(1276), buttonSize*2, buttonSize );

        //Util.addMouseTracker(this);
    }

    public void updateConnectionMessage(String connectionMessage) {
        connection.setText(connectionMessage);
    }

    public void addMenuItem(int location, String title, Action action) {
        pageButtons[location].setText(title);
        pageButtons[location].setAction(action);
        pageButtons[location].setVisible(true);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Util.drawGrid(g);
        //Util.testFontSizes(g);
    }



    public Button addButton(int r, int c,String id, String title) {
        Button button = new Button(id, title);
        this.add(button);
        button.setBounds(scaleKindle(100+219*c), scaleKindle(130+219*r), scaleKindle(170), scaleKindle(170) );
        return button;
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
        for(Component c: this.getComponents()) {
            if ( c instanceof JButton ) {
                JButton b = (JButton) c;
                b.setForeground(theme.getControlForeground());
                b.setBackground(theme.getControlBackground());
                b.setOpaque(true);
                //b.setBorderPainted(false);
            }
        }
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
        Date dt = new Date();

        statusMessages.append(String.format("%02d:%02d:%02d.%03d %s\n",
                dt.getHours(),
                dt.getMinutes(),
                dt.getSeconds(),
                dt.getTime()%1000,
                text));
        Document d = statusMessages.getDocument();

        try {
            while (statusMessages.getLineCount() > 40) {
                d.remove(0, statusMessages.getLineEndOffset(0));
            }
        } catch ( BadLocationException e) {
            log.error(e.getMessage(), e);
        }
        statusMessages.setCaretPosition(d.getLength());
    }

}
