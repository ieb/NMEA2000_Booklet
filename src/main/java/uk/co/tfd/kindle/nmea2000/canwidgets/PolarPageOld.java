package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public class PolarPageOld extends JPanel implements CanMessageListener, CanWidget  {
    private static Logger log = LoggerFactory.getLogger(PolarPage.class);



    private final static BasicStroke normalStroke = new BasicStroke(2.0f);
    private final static BasicStroke heavyStroke = new BasicStroke(4.0f);

    private final static float dash1[] = {10.0f};
    private final static BasicStroke dashedStroke = new BasicStroke(2.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);
    private int fontSize;
    private Font largeValues;
    private Font smallValues;
    private Font labelsFont;
    private Font ringFont;
    private final boolean rotate;
    private double[] twaHistory = new double[10];
    private double[] stwHistory = new double[10];
    private double twa = CanMessageData.n2kDoubleNA;
    private double tws = CanMessageData.n2kDoubleNA;
    private double stw = CanMessageData.n2kDoubleNA;
    private double awa = CanMessageData.n2kDoubleNA;
    private double aws = CanMessageData.n2kDoubleNA;
    private double vmg = CanMessageData.n2kDoubleNA;
    private double polarSpeed = CanMessageData.n2kDoubleNA;
    private double polarSpeedRatio = CanMessageData.n2kDoubleNA;
    private Polar.PolarTarget upwindTarget = Polar.PolarTarget.polarTargetNA;
    private Polar.PolarTarget downwindTarget = Polar.PolarTarget.polarTargetNA;;
    private long lastUpdate = 0;
    private String out = "-.-";
    private int boxSize = 0;
    private double ringSize = 450/2;


    public PolarPageOld(boolean rotate) {
        this.rotate = rotate;
        log.info("Created polar page");
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
            //log.info("Repaint requested");
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
                vmg = performance.vmg;
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
                log.debug("Update Polar {} ", newOut);
                log.debug("Targets {} {} ", upwindTarget, downwindTarget);
            }
        } else {
            if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {

                twa = CanMessageData.n2kDoubleNA;
                tws = CanMessageData.n2kDoubleNA;
                stw = CanMessageData.n2kDoubleNA;
                awa = CanMessageData.n2kDoubleNA;
                aws = CanMessageData.n2kDoubleNA;
                vmg = CanMessageData.n2kDoubleNA;
                polarSpeed = CanMessageData.n2kDoubleNA;
                downwindTarget = Polar.PolarTarget.polarTargetNA;
                upwindTarget = Polar.PolarTarget.polarTargetNA;
                lastUpdate = System.currentTimeMillis();
                newOut = "-.-";
                log.info("Performance message timeout trigger: {}", message );
            }
        }
        if (!newOut.equals(this.out)) {
            //log.info("Redraw {} {} ", this.out, newOut);
            this.out = newOut;
            return true;
        }
        return false;

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

    public void plotData(Graphics2D g2) {
        // the ring radius is at 485
        g2.translate(-480,500);
        boxPerformance(g2);
        g2.translate(500,0);
        boxTarget(g2);
        g2.translate(-500,180);
        boxWindAparant(g2);
        g2.translate(500,0);
        boxWindTrue(g2);
        g2.translate(-20,-680);
    }

    private void boxPerformance(Graphics2D g2) {

        draw3CellBox(g2, "Performance",
                new String[] {
                        "polar %",
                        "polar kn",
                        "stw kn",
                },
                new String[] {
                        "%3.0f",
                        "%3.1f",
                        "%3.1f"
                },
                new double[] {
                        polarSpeedRatio,
                        stw,
                        polarSpeed
                },
                new double[] {
                        100.0,
                        CanMessageData.scaleToKnots,
                        CanMessageData.scaleToKnots
                },
                1.0
        );
    }

    private void draw3CellBox(Graphics2D g2,
                              String title,
                              String[] labels,
                              String[] formats,
                              double[] values,
                              double[] factors,
                              double mainValueScale) {
        // 3 cell box for speed.

        int boxWidth = 450;
        int boxHeight = 170;
        int cellSplit = (450*55)/100;
        int mainValueAlign = cellSplit/2;
        int subValueAlign = boxWidth-10;
        g2.translate(mainValueAlign, boxHeight/2-5);
        g2.scale(mainValueScale, mainValueScale);
        Util.drawString(formatValue(formats[0], values[0], factors[0]),
                0, 0,
                largeValues,
                Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        g2.scale(1.0/mainValueScale, 1.0/mainValueScale);
        g2.translate(-mainValueAlign, -boxHeight/2+5);

        Util.drawString(formatValue(formats[1], values[1], factors[1]),
                subValueAlign, boxHeight/4-15,
                smallValues,
                Util.HAlign.DECIMAL, Util.VAlign.CENTER, g2);
        Util.drawString(formatValue(formats[2], values[2], factors[2]),
                subValueAlign, 3*boxHeight/4-15,
                smallValues,
                Util.HAlign.DECIMAL, Util.VAlign.CENTER, g2);
        Util.drawString(title, 10, 5, labelsFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString(labels[0], 10, boxHeight-3, labelsFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        Util.drawString(labels[1], cellSplit+5, (boxHeight/2)-5, labelsFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        Util.drawString(labels[2], cellSplit+5, (boxHeight)-5, labelsFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);

        g2.drawRoundRect(0, 0, boxWidth, boxHeight, 15,15);
        g2.drawLine(cellSplit, 0, cellSplit, boxHeight);
        g2.drawLine(cellSplit, boxHeight/2, boxWidth, boxHeight/2);
    }
    private void boxTarget(Graphics2D g2) {
        Polar.PolarTarget target = upwindTarget;
        String label = "Upwind twa";
        if ( Math.abs(twa) > Math.PI/2) {
            target = downwindTarget;
            label = "Downwind twa";
        }
        double twaFactor = CanMessageData.scaleToDegrees;
        if ( target.twa < 0) {
            twaFactor = - twaFactor;
        }

        draw3CellBox(g2, "Target",
                new String[] {
                        label,
                        "vmg kn",
                        "stw kn"
                },
                new String[] {
                        "%3.0f",
                        "%3.1f",
                        "%3.1f"
                },
                new double[] {
                        target.twa,
                        target.vmg,
                        target.stw
                },
                new double[] {
                        twaFactor,
                        CanMessageData.scaleToKnots,
                        CanMessageData.scaleToKnots
                },
                1.0
        );
    }

    private void boxWindTrue(Graphics2D g2) {

        // 3 cell box for speed.
        double mainLabelScale = 1.0;
        if ( Math.abs(twa*CanMessageData.scaleToDegrees) >= 99) {
            mainLabelScale = 0.75;
        }
        double twaFactor = CanMessageData.scaleToDegrees;
        String format = "S%1.0f";
        String mainLabel = "twa Stbd";
        if ( twa < 0 ) {
            twaFactor = -CanMessageData.scaleToDegrees;
            format = "P%1.0f";
            mainLabel = "twa Port";
        }
        double vmgFactor = CanMessageData.scaleToKnots;
        if ( vmg < 0 ) {
            vmgFactor = -vmgFactor;
        }
        draw3CellBox(g2, "True Wind",
                new String[] {
                        mainLabel,
                        "tws kn",
                        "vmg kn"
                },
                new String[] {
                        format,
                        "%3.1f",
                        "%3.1f"
                },
                new double[] {
                        twa,
                        tws,
                        vmg
                },
                new double[] {
                        twaFactor,
                        CanMessageData.scaleToKnots,
                        vmgFactor
                },
                mainLabelScale
        );

    }

    private void boxWindAparant(Graphics2D g2) {

        // 3 cell box for speed.
        double mainValueScale = 1.0;
        if ( Math.abs(awa*CanMessageData.scaleToDegrees) >= 99) {
            mainValueScale = 0.75;
        }
        double awaFactor = CanMessageData.scaleToDegrees;
        String format = "S%1.0f";
        String mainLabel = "awa Stbd";
        if ( awa < 0 ) {
            awaFactor = -CanMessageData.scaleToDegrees;
            format = "P%1.0f";
            mainLabel = "twa Port";
        }
        double vmgFactor = CanMessageData.scaleToKnots;
        if ( vmg < 0 ) {
            vmgFactor = -vmgFactor;
        }
        draw3CellBox(g2, "Apparent Wind",
                new String[] {
                        mainLabel,
                        "tws kn",
                        "vmg kn"
                },
                new String[] {
                        format,
                        "%3.1f",
                        "%3.1f"
                },
                new double[] {
                        awa,
                        aws,
                        vmg
                },
                new double[] {
                        awaFactor,
                        CanMessageData.scaleToKnots,
                        vmgFactor
                },
                mainValueScale
        );

    }


    private String formatValue(String format, double value, double factor) {
        return  (value == CanMessageData.n2kDoubleNA)?"--":String.format(format, value*factor);
    }
    public int knotsScale(double kn) {
        double k = (kn*ringSize)/14.0;
        return (int) k;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    private void recalcSize() {
        int wBoxSize, hBoxSize;
        if ( rotate ) {
            wBoxSize = (int)(this.getWidth()/1.3);
            hBoxSize = (int)(this.getHeight()/2.2);
        } else {
            wBoxSize = (int)(this.getWidth()/2.2);
            hBoxSize = (int)(this.getHeight()/1.4);

        }
        int nBoxSize = Math.min(wBoxSize, hBoxSize);



        if ( nBoxSize != this.boxSize ) {
            log.debug("Box size w{} h{} n{} ", wBoxSize, hBoxSize, nBoxSize);
            this.boxSize = nBoxSize;



            double largeValuesSize = (0.8*((double) Util.DEFAULT_SCREEN_RESOLUTION/(double)Util.getScreenResolution()))*200;

            if ( Util.isKindle() ) {
                largeValuesSize = largeValuesSize*13/25;
            }


            double smallValuesSize = largeValuesSize/2.0;
            double ringSize = largeValuesSize/5.0;
            double labelFontSize = largeValuesSize/6.0;
            this.fontSize = (int)(largeValuesSize);

            // the sizes in the map must be floats to get pixel heights on
            // a kindle otherwise the nearest point size will be selected.
            Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.FAMILY, "Arial");
            attributes.put(TextAttribute.SIZE, (float)largeValuesSize);
            this.largeValues = new Font(attributes); // main values
            attributes.put(TextAttribute.SIZE, (float)smallValuesSize);
            this.smallValues = new Font(attributes);
            attributes.put(TextAttribute.SIZE, (float)labelFontSize);
            this.labelsFont = new Font(attributes);
            attributes.put(TextAttribute.SIZE, (float)ringSize);
            this.ringFont = new Font(attributes);
        }
    }
    @Override
    public void paintComponent(Graphics graphics) {
        recalcSize();

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
        plotData(g2);
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


    @Override
    public JComponent getJComponent() {
        return this;
    }


}
