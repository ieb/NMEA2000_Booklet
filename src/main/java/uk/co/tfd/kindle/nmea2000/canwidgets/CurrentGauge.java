package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class CurrentGauge extends BaseGauge  {
    private static final Logger log = LoggerFactory.getLogger(CurrentGauge.class);

    public CurrentGauge(boolean rotate) {
        super(rotate);
        this.setTitle("Current A");
        this.out = "-- A";
    }

    public void setCurrent(double current, boolean valid) {
        // 10 == -120.
        // 1 degree per degree.
        String newOut = "-- A";
        if ( valid ) {
            newOut = String.format("%4.2f A", current);
        }
        if ( !newOut.equals(out)) {
            out = newOut;
            repaint();
        }
        // range is -60 to +60
        targetNeedleAngle = (int)(-120.0+((current+60)*240.0/120.0));
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

        // 120A == 240 degrees, from -60 to +60
        double step = (240.0/120);

        g2.rotate(-120*Math.PI/180.0);
        // 100mv steps
        for (int i = 0; i < 121; i++) {
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
        drawStringScaled("-60",(int)-(cos30*digitRadius),(int)(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("-30", (int)-(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("0", 0, -digitRadius, unitsFont, g2);
        drawStringScaled("30", (int)(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("60", (int)(cos30*digitRadius), (int)(sin30*digitRadius), unitsFont, g2);
    }

}
