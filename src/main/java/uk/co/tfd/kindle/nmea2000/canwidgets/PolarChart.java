package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ieb on 20/06/2020.
 */
public class PolarChart extends JPanel {
    private static Logger log = LoggerFactory.getLogger(PolarPage.class);



    private final static BasicStroke normalStroke = new BasicStroke(2.0f);
    private final static BasicStroke heavyStroke = new BasicStroke(4.0f);

    private final static float dash1[] = {10.0f};
    private final static BasicStroke dashedStroke = new BasicStroke(2.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);
    private Font ringFont;
    private double twa = CanMessageData.n2kDoubleNA;
    private double tws = CanMessageData.n2kDoubleNA;
    private double stw = CanMessageData.n2kDoubleNA;
    private double awa = CanMessageData.n2kDoubleNA;
    private double aws = CanMessageData.n2kDoubleNA;
    private double vmg = CanMessageData.n2kDoubleNA;
    private double polarSpeed = CanMessageData.n2kDoubleNA;
    private Polar.PolarTarget upwindTarget = Polar.PolarTarget.polarTargetNA;
    private Polar.PolarTarget downwindTarget = Polar.PolarTarget.polarTargetNA;;
    private String out = "-.-";
    private double ringSize = 450/2;


    public PolarChart() {
        ringFont = Util.createFont(Util.isKindle()?16.0f:24.0f);
    }


    public void updatePerformance(PerformanceCalculator.PerformanceCanMessage performance, boolean valid) {
        String newOut;
        if ( ! valid ) {
            twa = CanMessageData.n2kDoubleNA;
            tws = CanMessageData.n2kDoubleNA;
            stw = CanMessageData.n2kDoubleNA;
            awa = CanMessageData.n2kDoubleNA;
            aws = CanMessageData.n2kDoubleNA;
            vmg = CanMessageData.n2kDoubleNA;
            polarSpeed = CanMessageData.n2kDoubleNA;
            downwindTarget = Polar.PolarTarget.polarTargetNA;
            upwindTarget = Polar.PolarTarget.polarTargetNA;
            newOut = "-.-";
        } else {
            twa = performance.twa;
            tws = performance.tws;
            stw = performance.stw;
            awa = performance.awa;
            aws = performance.aws;
            vmg = performance.vmg;
            polarSpeed = performance.polarSpeed;
            downwindTarget = performance.downwindTarget;
            upwindTarget = performance.upwindTarget;
            // express the change as a string with the required precision
            newOut = String.format("%4.1f,%4.2f,%4.2f",
                    twa*CanMessageData.scaleToDegrees,
                    tws*CanMessageData.scaleToKnots,
                    stw*CanMessageData.scaleToKnots);
        }
        if (!newOut.equals(this.out)) {
            //log.info("Redraw {} {} ", this.out, newOut);
            this.out = newOut;
            repaint();
        }
    }



    private void drawRing(Graphics2D g2, String label, int cx, int cy, int radius) {
        g2.setStroke(dashedStroke);
        g2.drawArc(cx-(radius),cy-(radius), radius*2,radius*2,0,360);
        g2.setStroke(normalStroke);
    }
    private void labelRing(Graphics2D g2, String label, int cx, int cy, int radius) {
        Util.drawString(label, 0, -radius, ringFont, Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        Util.drawString(label, 0, radius, ringFont, Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
    }

    private void drawRadial(Graphics2D g2, int angle, int length, String label) {
        double a = (((double)angle)*Math.PI/180.0);
        g2.rotate(a);
        g2.drawLine(0,0, 0, -length );
        Util.drawString(label, 0, -length, ringFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
        g2.rotate(-a);
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
        g2.setStroke(heavyStroke);
        g2.drawLine(0, -y, x, -y );
        g2.setStroke(normalStroke);
    }

    private void plotPolarSpeed(Graphics2D g2) {
        if ( CanMessageData.isNa(twa, stw, polarSpeed)) {
            return;
        }
        g2.rotate(twa);
        int y = knotsScale(polarSpeed*CanMessageData.scaleToKnots);
        int y1 = knotsScale(stw*CanMessageData.scaleToKnots);
        g2.setStroke(heavyStroke);
        g2.drawLine(0,-y/2,0, -y);
        g2.drawArc(-(y),-(y), y*2,y*2,100,-20);
        y = (y * 8)/10;
        g2.drawArc(-(y),-(y), y*2,y*2,95,-10);
        g2.fillArc(-10, -y1-10,20, 20, 0, 360);
        g2.setStroke(normalStroke);
        g2.rotate(-twa);
    }

    public int knotsScale(double kn) {
        double k = (kn*ringSize)/14.0;
        return (int) k;
    }


    @Override
    public void paintComponent(Graphics graphics) {

        Graphics2D g2 = (Graphics2D) graphics;
        Color foreground = this.getForeground();
        Color background = this.getBackground();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.setBackground(foreground);
        g2.fillRect(0, 0 ,this.getWidth(),this.getHeight());
        g2.setColor(foreground);
        g2.setBackground(background);


        //log.info("Screen size{} screen resolition {} ", Toolkit.getDefaultToolkit().getScreenSize(), Toolkit.getDefaultToolkit().getScreenResolution());
        //log.info("Window size {} {} ", this.getWidth(), this.getHeight());
        //  Kindle PW4 sizejava.awt.Dimension[width=1072,height=1448] screen resolition 300

        int width = this.getWidth();
        double screenScale = (double)width/1072.0;
        this.ringSize = 1072.0*0.45;
        g2.scale(screenScale, screenScale);

        // 14kn == 485 radius

        g2.translate(1072/2,1072/2);
        for (int i = 2; i < 13; i+=2) {
            drawRing(g2, String.valueOf(i),0, 0, knotsScale(i));
        }
        drawRing(g2, String.valueOf(14),0, 0, knotsScale(14));
        for (int i = 30; i < 180; i+=30) {
            drawRadial(g2, i, knotsScale(14.0), "S"+String.valueOf(i));
        }
        for (int i = -30; i > -180; i-=30) {
            drawRadial(g2, i, knotsScale(14.0), "P"+String.valueOf(-i));
        }
        for (int i = 2; i < 13; i+=2) {
            labelRing(g2, String.valueOf(i),0, 0, knotsScale(i));
        }
        labelRing(g2, String.valueOf(14),0, 0, knotsScale(14));

        //plotCurve(g2, twaHistory, stwHistory);
        plotVMG(g2, upwindTarget);
        plotVMG(g2, downwindTarget);
        plotPolarSpeed(g2);
        g2.setColor(foreground);
        g2.setBackground(background);
        //log.info("Rendering done");
        g2.scale(1.0/screenScale, 1.0/screenScale);
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



}
