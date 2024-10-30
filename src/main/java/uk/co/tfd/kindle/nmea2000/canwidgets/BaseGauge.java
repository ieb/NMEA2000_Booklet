package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseGauge extends JPanel  {
    private static final Logger log = LoggerFactory.getLogger(BaseGauge.class);
    final Timer timer;
    private final Font vpFont;
    private final boolean rotate;
    private double needleRpm = 0;
    int needleAngle = -120;
    private long lastUpdate = System.currentTimeMillis();
    int targetNeedleAngle = -120;
    private int startupState = 0;
    Font dialFont;
    Font unitsFont;
    String out = "-- %";
    String title;
    final BasicStroke medTickStroke;
    final BasicStroke thickTickStroke;

    public BaseGauge(boolean rotate) {
        this.rotate = rotate;
        this.title = "Voltage V";
        dialFont = Util.createFont(10.0f);
        unitsFont = Util.createFont(14.0f);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.FAMILY,"Serif");
        attributes.put(TextAttribute.WEIGHT,3.0f);
        attributes.put(TextAttribute.SIZE, Util.isKindle()?4.24f:8.0f);
        vpFont = new Font(attributes);
        thickTickStroke = new BasicStroke(2.5f);
        medTickStroke = new BasicStroke(1.5f);

        timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if ( startupState == 0) {
                    needleAngle = needleAngle +5;
                    repaint();
                    if ( needleAngle >= 120 ) {
                        startupState = 1;
                    }
                } else if (startupState == 1) {
                    needleAngle = needleAngle - 5;
                    repaint();
                    if ( needleAngle <= 0) {
                        startupState = 3;
                    }
                } else {
                    int change = Math.abs(needleAngle - targetNeedleAngle) / 5;
                    if (change < 1) {
                        change = 1;
                    }
                    if (needleAngle > targetNeedleAngle) {
                        needleAngle = needleAngle - change;
                        repaint();
                    } else if (needleAngle < targetNeedleAngle) {
                        needleAngle = needleAngle + change;
                        repaint();
                    } else {
                        timer.stop();
                    }
                }
            }
        });
        timer.start();

    }

    public void restart() {
        startupState = 0;
        timer.restart();
    }



    abstract void drawDial(Graphics2D g2, int tickRadius);
    abstract void annotateDial(Graphics2D g2, int digitRadius);


    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = this.getWidth();
        // because width is scale no need to take into account scaling again.
        double scale = ((double)w)/200;
        w = 200;
        int h = 200;

        g2.scale(scale, scale);

        Color foreground = this.getForeground();
        Color background = this.getBackground();
        g2.setColor(background);
        g2.setBackground(foreground);

        g2.fillArc(0, 0, w, h, 0,360);
        g2.setColor(foreground);
        g2.setBackground(background);

        int outerRadius = (w-5)/2;
        int radius = outerRadius-12;
        g2.translate(w/2, h/2);
        // outer
        if ( rotate ) {
            g2.rotate(-Math.PI/2.0);
        }

        g2.drawArc(-outerRadius, -outerRadius, 2*outerRadius, 2*outerRadius, 0,360);
        g2.drawArc(-radius, -radius, 2*radius, 2*radius, 0,360);
        int tickRadius = radius - 5;

        drawDial( g2, tickRadius);
        int digitRadius =  radius - 30;
        annotateDial(g2, digitRadius);

        drawStringExpanded("VOLVO PENTA", 0, 20+(digitRadius)/2, vpFont, g2);
        drawStringScaled(title, 0, -(digitRadius-2)/2, dialFont,  g2);
        drawStringScaled(out, 0, 5+(digitRadius)/2, unitsFont, g2);

        annotateDial(g2, digitRadius);


        g2.fillArc(-15, -15, 30, 30, 0,360);



        g2.rotate(needleAngle*Math.PI/180.0);
        int needleRadius = radius - 20;
        g2.setStroke(new BasicStroke(4.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g2.drawLine(0,0, 0, -needleRadius);
        g2.rotate(-needleAngle*Math.PI/180.0);

        if ( rotate ) {
            g2.rotate(Math.PI/2.0);
        }

        g2.translate(-w/2, -h/2);
        g2.scale(-scale, -scale);



    }

    private void drawStringExpanded(String text, int x, int y, Font vpFont, Graphics2D g) {
        double scale = Util.isKindle()?0.5:1.0;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x,y);
        g2.scale(2.0*scale, scale);
        Util.drawString(text, 0, 0, vpFont, Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        g2.scale(1.0/(2.0*scale), 1.0/(scale));
        g2.translate(-x,-y);
        g2.dispose();
    }

    /**
     * Setting the font size is not enough to get the correct font in this widget on th Kindle
     * So this should scale the font on its center while drawing.
     * @param text
     * @param x
     * @param y
     * @param vpFont
     * @param g
     */
    void drawStringScaled(String text, int x, int y, Font vpFont, Graphics2D g) {
        double scale = Util.isKindle()?0.5:1.0;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x,y);
        g2.scale(scale, scale);
        Util.drawString(text, 0, 0, vpFont, Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        g2.scale(1.0/scale, 1.0/scale);
        g2.translate(-x,-y);
        g2.dispose();
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
