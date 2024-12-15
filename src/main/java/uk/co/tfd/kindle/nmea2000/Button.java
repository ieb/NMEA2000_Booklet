package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Button extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(Button.class);
    public final String id;
    private String text;
    private Color background;
    private Color forground;
    private boolean pressed;
    private boolean highlight;
    private Action action;
    private final Font buttonFont;
    private final Font buttonHoverFont;


    public Button(String id, String text) {
        this.id = id;
        this.text = text;
        this.buttonFont = Util.createFont( 12.0f);
        this.buttonHoverFont = Util.createExtraBoldFont(14.0f);
        background = Color.WHITE;
        forground = Color.BLACK;
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    ActionEvent ae = new ActionEvent(Button.this,
                            ActionEvent.ACTION_FIRST,
                            "click",
                            System.currentTimeMillis(),
                            0);
                    action.actionPerformed(ae);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                highlight = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                highlight = false;
                pressed = false;
                repaint();
            }
        });

    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        background = bg;
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        forground = fg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g2.setColor(background);
        g2.setBackground(forground);
        g2.fillRect(0,0,w,h);
        g2.setColor(forground);
        g2.setBackground(background);
        Font f = buttonFont;
        if ( pressed ) {
            g2.setStroke(new BasicStroke(4.0f));
        } else {
            g2.setStroke(new BasicStroke(2.0f));

        }
        if ( highlight) {
            f = buttonHoverFont;
        }
        Util.drawString(text, w/2, h/2, f, Util.HAlign.CENTER, Util.VAlign.CENTER, g2  );
        g2.drawRoundRect(2,2,w-4,h-4,15, 15);
    }

    public void setText(String text) {
        this.text = text;
        this.repaint();
    }

}
