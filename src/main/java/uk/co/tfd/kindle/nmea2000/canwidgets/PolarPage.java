package uk.co.tfd.kindle.nmea2000.canwidgets;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.FormattedFloatingDecimal;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public class PolarPage extends JPanel implements CanMessageListener, CanWidget  {
    private static Logger log = LoggerFactory.getLogger(PolarPage.class);



    private final static BasicStroke heavyStroke = new BasicStroke(2.0f);

    private final static float dash1[] = {10.0f};
    private final static BasicStroke dashedStroke = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);
    private final int fontSize;
    private final Font largeFont;
    private final Font normalFont;
    private final Font mediumFont;
    private final Font smallFont;
    private final Font tinyFont;
    private double[] twaHistory = new double[10];
    private double[] stwHistory = new double[10];
    private double twa = CanMessageData.n2kDoubleNA;
    private double tws = CanMessageData.n2kDoubleNA;
    private double stw = CanMessageData.n2kDoubleNA;
    private double awa = CanMessageData.n2kDoubleNA;
    private double aws = CanMessageData.n2kDoubleNA;
    private double polarSpeed = CanMessageData.n2kDoubleNA;
    private double polarSpeedRatio = CanMessageData.n2kDoubleNA;
    private Polar.PolarTarget upwindTarget = Polar.PolarTarget.polarTargetNA;
    private Polar.PolarTarget downwindTarget = Polar.PolarTarget.polarTargetNA;;
    private long lastUpdate = 0;
    private String out = "-.-";


    public PolarPage(boolean rotate) {
        this.setLayout(new BorderLayout());

        double large = (0.8*((double) Util.DEFAULT_SCREEN_RESOLUTION/(double)Util.getScreenResolution()))*200;

        if ( Util.isKindle() ) {
            large = large*15/25;
        }

        double normal = large/2.0;
        double medium = large/6.0;
        double small = large/8.0;
        double tiny = large/10.0;
        this.fontSize = (int)(large);

        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.FAMILY, "Arial");
        attributes.put(TextAttribute.SIZE, (float)large);
        this.largeFont = new Font(attributes);
        attributes.put(TextAttribute.SIZE, (float)normal);
        this.normalFont = new Font(attributes);
        attributes.put(TextAttribute.SIZE, (float)medium);
        this.mediumFont = new Font(attributes);
        attributes.put(TextAttribute.SIZE, (float)small);
        this.smallFont = new Font(attributes);
        attributes.put(TextAttribute.SIZE, (float)tiny);
        this.tinyFont = new Font(attributes);

    }




    @Override
    public int[] getPgns() {
        log.info("Loading PGNS {} ", Arrays.toString(PerformanceCalculator.PerformanceCanMessage.getSourcePGNS()));
        return PerformanceCalculator.PerformanceCanMessage.getSourcePGNS();
    }

    @Override
    public void onDrop(int pgn) {

    }

    @Override
    public void onUnhandled(int pgn) {

    }

    @Override
    public void onMessage(CanMessage message) {
        if ( needsUpdate(message)) {
            this.repaint();
        }
    }

    private boolean needsUpdate(CanMessage message) {
        String newOut = this.out;
        if ( message instanceof PerformanceCalculator.PerformanceCanMessage) {
            PerformanceCalculator.PerformanceCanMessage performance = (PerformanceCalculator.PerformanceCanMessage) message;
            if (!CanMessageData.isNa(performance.twa, performance.tws, performance.stw) ) {
                twa = performance.twa;
                tws = performance.tws;
                stw = performance.stw;
                awa = performance.awa;
                aws = performance.aws;
                polarSpeed = performance.polarSpeed;
                downwindTarget = performance.downwindTarget;
                upwindTarget = performance.upwindTarget;
                polarSpeedRatio = performance.polarSpeedRatio;
                lastUpdate = System.currentTimeMillis();
                // express the change as a string with the required precision
                newOut = String.format("%4.1f,%4.2f,%4.2f",
                        twa*CanMessageData.scaleToDegrees,
                        tws*CanMessageData.scaleToKnots,
                        stw*CanMessageData.scaleToKnots);
                log.info("Update Polar {} ", newOut);
                log.info("Targets {} {} ", upwindTarget, downwindTarget);
            }
        } else {
            if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {

                twa = CanMessageData.n2kDoubleNA;
                tws = CanMessageData.n2kDoubleNA;
                stw = CanMessageData.n2kDoubleNA;
                awa = CanMessageData.n2kDoubleNA;
                aws = CanMessageData.n2kDoubleNA;
                polarSpeed = CanMessageData.n2kDoubleNA;
                downwindTarget = Polar.PolarTarget.polarTargetNA;
                upwindTarget = Polar.PolarTarget.polarTargetNA;
                lastUpdate = System.currentTimeMillis();
                newOut = "-.-";
                log.info("Performance message timeout trigger: {}", message );
            }
        }
        if (!newOut.equals(this.out)) {
            log.info("Redraw {} {} ", this.out, newOut);
            this.out = newOut;
            return true;
        }
        return false;

    }

    private void drawRing(Graphics2D g2, String label, int cx, int cy, int radius) {
        int windowDeg = 0;
        if ( label != null) {
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D bounds = fm.getStringBounds(label, g2);

            int w = (int) (bounds.getWidth());
            int h = (int) (bounds.getHeight());
            g2.drawString(label, -w/2, (h/2)-(radius));
            windowDeg = (int)(180*Math.atan2(w+10, radius)/Math.PI);
        }
        // x y is top left, angle starts at 90 and arc is anti-clockwise. Its a joke of an api.
        // why not center x, center y, radius, 0 up and clockwise, like most other apis ????
        g2.drawArc(cx-(radius),cy-(radius), radius*2,radius*2,90+windowDeg,360-(windowDeg*2));
    }

    private void drawRadial(Graphics2D g2, int angle, int length, String label) {
        double a = (((double)angle)*Math.PI/180.0);
        g2.rotate(a);
        g2.drawLine(0,0, 0, -length );
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(label, g2);
        int w = (int) (bounds.getWidth());
        int h = (int) (bounds.getHeight());
        g2.drawString(label, -w/2, -(h/2)-(length));
        g2.rotate(-a);
    }
    private void plotCurve(Graphics2D g2, double[] twa, double[] stw) {
        for (int i = 0; i < twa.length; i++) {
            int l = knotsScale(stw[i]);
            int x1 = (int)(Math.cos(twa[i])*l);
            int y1 = (int)(Math.sin(twa[i])*l);
            g2.fillOval(x1-4, y1-4, 8, 8);
        }
    }

    private void plotVMG(Graphics2D g2, Polar.PolarTarget target) {
        if ( target == Polar.PolarTarget.polarTargetNA) {
            return;
        }
        double vmg = target.vmg;
        if ( Math.abs(target.twa) > Math.PI/2) {
            vmg = -vmg;
        }
        int y = knotsScale(vmg*CanMessageData.scaleToKnots);
        int x = (int) (Math.tan(target.twa)*1.0*y);
        g2.drawLine(0, -y, x, -y );
    }

    private void plotPolarSpeed(Graphics2D g2) {
        if ( CanMessageData.isNa(twa, stw, polarSpeed)) {
            return;
        }
        g2.rotate(twa);
        int y = knotsScale(polarSpeed*CanMessageData.scaleToKnots);
        int y1 = knotsScale(stw*CanMessageData.scaleToKnots);
        g2.drawLine(0,-y/2,0, -y);
        g2.drawArc(-(y),-(y), y*2,y*2,100,-20);
        y = (y * 8)/10;
        g2.drawArc(-(y),-(y), y*2,y*2,95,-10);
        g2.fillOval(-4, -y1-4, 8, 8);
        g2.rotate(-twa);
    }

    public void plotData(Graphics2D g2) {
        /*
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds("polarStw: ", g2);
        int h = (int) (bounds.getHeight());
        int w = (int) (bounds.getWidth());
        bounds = fm.getStringBounds("000.", g2);
        int wd = (int) (bounds.getWidth());
        int x = -200;
        int xd = -200+w+wd;
        g2.drawString("Actuals", x,300);
        g2.drawString("stw:", x,300+1*h);
        g2.drawString("polarStw:", x,300+2*h);
        g2.drawString("polar %:", x,300+3*h);

        Util.drawString(formatValue("%4.1f", stw, CanMessageData.scaleToKnots),
                xd, 300+1*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.1f", polarSpeed, CanMessageData.scaleToKnots),
                xd, 300+2*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.1f %%", polarSpeedRatio, 100),
                xd, 300+3*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);


        x=-200;
        xd=-200+2*wd;
        int xg=100;
        g2.drawString("twa:", x,300+5*h);
        g2.drawString("tws:", x+xg ,300+5*h);
        g2.drawString("awa:", x+2*xg,300+5*h);
        g2.drawString("aws:", x+3*xg,300+5*h);
        Util.drawString(formatValue("%.0f", twa, CanMessageData.scaleToDegrees),
                xd, 300+5*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.1f", tws, CanMessageData.scaleToKnots),
                xd+xg, 300+5*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.0f", awa, CanMessageData.scaleToDegrees),
                xd+2*xg, 300+5*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.1f", aws, CanMessageData.scaleToKnots),
                xd+3*xg, 300+5*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);






        bounds = fm.getStringBounds("vmg: ", g2);
        w = (int) (bounds.getWidth());
        x = -80;
        xd = -80+w+wd;
        
        g2.drawString("Downwind Target", x,300);
        g2.drawString("stw:", x,300+1*h);
        g2.drawString("vmg:", x,300+2*h);
        g2.drawString("twa:", x,300+3*h);

        Util.drawString(formatValue("%4.1f", downwindTarget.stw, CanMessageData.scaleToKnots),
                xd, 300+1*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.1f", downwindTarget.vmg, CanMessageData.scaleToKnots),
                xd, 300+2*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.0f", downwindTarget.twa, CanMessageData.scaleToDegrees),
                xd, 300+3*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);

        x = 60;
        xd = 60+w+wd;
        g2.drawString("Upwind Target", x,300);
        g2.drawString("stw:", x,300+1*h);
        g2.drawString("vmg:", x,300+2*h);
        g2.drawString("twa:", x,300+3*h);
        Util.drawString(formatValue("%4.1f", upwindTarget.stw, CanMessageData.scaleToKnots),
                xd, 300+1*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.1f", upwindTarget.vmg, CanMessageData.scaleToKnots),
                xd, 300+2*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        Util.drawString(formatValue("%4.0f", upwindTarget.twa, CanMessageData.scaleToDegrees),
                xd, 300+3*h,
                smallFont,
                Util.HAlign.DECIMAL, Util.VAlign.BOTTOM, g2);
        */

        g2.translate(-250,230);
        // 3 cell box for speed.
        Util.drawString(formatValue("%3.0f", polarSpeedRatio, 100),
                57, 45,
                normalFont,
                Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        Util.drawString(formatValue("%4.1f", stw, CanMessageData.scaleToKnots),
                160, 20,
                mediumFont,
                Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        Util.drawString(formatValue("%4.1f", polarSpeed, CanMessageData.scaleToKnots),
                160, 70,
                mediumFont,
                Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        Util.drawString("Performance", 10, 5, tinyFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString("polar %", 10, 100, tinyFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        Util.drawString("polar kn", 130, 50, tinyFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        Util.drawString("stw kn", 130, 100, tinyFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        g2.drawRoundRect(0, 0, 200, 100, 15,15);
        g2.drawLine(120, 0, 120, 100);
        g2.drawLine(120, 50, 200, 50);
        g2.translate(250,-230);

        g2.translate(-45,230);
        // 3 cell box for speed.
        Polar.PolarTarget target = upwindTarget;
        String label = "Upwind stw kn";
        if ( Math.abs(twa) > Math.PI/2) {
            target = downwindTarget;
            label = "Downwind stw kn";
        }
        Util.drawString(formatValue("%3.1f", target.stw, CanMessageData.scaleToKnots),
                55, 45,
                normalFont,
                Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        Util.drawString(formatValue("%3.0f", target.twa, CanMessageData.scaleToDegrees),
                180, 20,
                mediumFont,
                Util.HAlign.DECIMAL, Util.VAlign.CENTER, g2);
        Util.drawString(formatValue("%3.1f", target.vmg, CanMessageData.scaleToKnots),
                180, 70,
                mediumFont,
                Util.HAlign.DECIMAL, Util.VAlign.CENTER, g2);
        Util.drawString("Target", 10, 5, tinyFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString(label, 10, 100, tinyFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        Util.drawString("twa deg", 130, 50, tinyFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        Util.drawString("vmg kn", 130, 100, tinyFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        g2.drawRoundRect(0, 0, 200, 100, 15,15);
        g2.drawLine(120, 0, 120, 100);
        g2.drawLine(120, 50, 200, 50);
        g2.translate(45,-230);


    }

    private String formatValue(String format, double value, double factor) {
        return  (value == CanMessageData.n2kDoubleNA)?"--":String.format(format, value*factor);
    }
    public int knotsScale(double kn) {
        double k = (kn*450.0/2.0)/14.0;
        return (int) k;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void paintComponent(Graphics graphics) {

        Graphics2D g2 = (Graphics2D) graphics;
        Color foreground = this.getForeground();
        Color background = this.getBackground();
        g2.setColor(foreground);
        g2.setBackground(background);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.clearRect(0, 0 ,this.getWidth(),this.getHeight());

        int width = this.getWidth();
        g2.translate(width/2,width/2);
        int knotsScale = 900/14;
        Stroke defaultStroke = g2.getStroke();
        g2.setStroke(dashedStroke);
        for (int i = 2; i < 13; i+=2) {
            drawRing(g2, String.valueOf(i),0, 0, knotsScale(i));
        }
        drawRing(g2, String.valueOf(14),0, 0, knotsScale(14));
        g2.setStroke(defaultStroke);
        for (int i = 30; i < 180; i+=30) {
            drawRadial(g2, i, knotsScale(14.0), "S"+String.valueOf(i));
        }
        for (int i = -30; i > -180; i-=30) {
            drawRadial(g2, i, knotsScale(14.0), "P"+String.valueOf(-i));
        }
        //plotCurve(g2, twaHistory, stwHistory);
        g2.setStroke(heavyStroke);
        plotVMG(g2, upwindTarget);
        plotVMG(g2, downwindTarget);
        plotPolarSpeed(g2);
        plotData(g2);
        g2.setStroke(defaultStroke);
        g2.setColor(foreground);
        g2.setBackground(background);
    }


    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        repaint();
    }


    @Override
    public JComponent getJComponent() {
        return this;
    }


}
