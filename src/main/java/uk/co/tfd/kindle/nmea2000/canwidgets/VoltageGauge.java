package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class VoltageGauge extends BaseGauge  {
    private static final Logger log = LoggerFactory.getLogger(VoltageGauge.class);

    public VoltageGauge(boolean rotate) {
        super(rotate);
        this.setTitle("Voltage V");
        this.out = "-- V";
    }

    public void setVoltage(double voltage, boolean valid) {
        // 10 == -120.
        // 1 degree per degree.
        String newOut = "-- V";
        if ( valid ) {
            newOut = String.format("%4.2f V", voltage);
        }
        if ( !newOut.equals(out)) {
            out = newOut;
            repaint();
        }
        targetNeedleAngle = (int)(-120.0+((voltage-11.0)*240.0/4.0));
        if ( targetNeedleAngle < -120) {
            targetNeedleAngle = -120;
        } else if ( targetNeedleAngle > 120) {
            targetNeedleAngle = 120;
        }
        if ( needleAngle != targetNeedleAngle ) {
            repaint();
            timer.restart();
        }
    }

    @Override
    void drawDial(Graphics2D g2, int tickRadius) {
        Stroke defaultStroke = g2.getStroke();

        // 4v == 240 degrees, from 11 to 15
        double step = (240.0/40);

        g2.rotate(-120*Math.PI/180.0);
        // 100mv steps
        for (int i = 0; i < 41; i++) {
            int height;
            if ( i%10 == 0) {
                height = 13;
                g2.setStroke(thickTickStroke);
            } else if (i%5 == 0) {
                height = 10;
                g2.setStroke(medTickStroke);
            } else {
                height = 5;
            }
            g2.drawLine(0, -(tickRadius-height), 0, -tickRadius);
            g2.rotate(step*Math.PI/180.0);
            g2.setStroke(defaultStroke);
        }
        g2.rotate(-(120.0+step)*Math.PI/180.0);

    }

    @Override
    void annotateDial(Graphics2D g2, int digitRadius) {
        double cos30 = 0.8660254;
        double sin30 = 0.5;
        drawStringScaled("11",(int)-(cos30*digitRadius),(int)(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("12", (int)-(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("13", 0, -digitRadius, unitsFont, g2);
        drawStringScaled("14", (int)(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("15", (int)(cos30*digitRadius), (int)(sin30*digitRadius), unitsFont, g2);
    }

}
