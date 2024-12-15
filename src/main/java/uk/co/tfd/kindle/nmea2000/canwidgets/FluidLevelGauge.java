package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class FluidLevelGauge extends BaseGauge  {
    private static final Logger log = LoggerFactory.getLogger(FluidLevelGauge.class);

    public FluidLevelGauge(boolean rotate) {
        super(rotate);
        this.setTitle("Tank %");
        this.out = "-- %";

    }
    public void setFuelLevel(double fuelLevel, boolean valid) {
        // 10 == -120.
        // 1 degree per degree.
        String newOut = "-- %";
        if ( valid ) {
            newOut = String.format("%4.0f %%", fuelLevel);
        }
        if ( !newOut.equals(out)) {
            out = newOut;
            repaint();
        }
        targetNeedleAngle = (int)(-120.0+(fuelLevel*240.0/100.0));
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
        g2.rotate(-120*Math.PI/180.0);
        double step = (240.0/100);
        for (int i = 0; i < 101; i+=5) {
            int height;
            if ( i%25 == 0) {
                height = 12;
                g2.setStroke(thickTickStroke);
            } else if (i%10 == 0) {
                height = 10;
                g2.setStroke(medTickStroke);
            } else {
                height = 5;
            }
            g2.drawLine(0, -(tickRadius-height), 0, -tickRadius);
            g2.rotate(5.0*step*Math.PI/180.0);
            g2.setStroke(defaultStroke);
        }
        // vertical
        g2.rotate(-(120.0+5.0*step)*Math.PI/180.0);

    }

    @Override
    void annotateDial(Graphics2D g2, int digitRadius) {
        double cos30 = 0.8660254;
        double sin30 = 0.5;
        drawStringScaled("0",(int)-(cos30*digitRadius),(int)(sin30*digitRadius), unitsFont,  g2);
        drawStringScaled("25", (int)-(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont,  g2);
        drawStringScaled("50", 0, -digitRadius, unitsFont, g2);
        drawStringScaled("75", (int)(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("100", (int)(cos30*digitRadius), (int)(sin30*digitRadius), unitsFont,  g2);
    }


}
