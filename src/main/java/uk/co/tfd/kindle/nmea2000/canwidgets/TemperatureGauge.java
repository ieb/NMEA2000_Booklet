package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.*;

import java.awt.*;

public class TemperatureGauge extends BaseGauge {
    private static final Logger log = LoggerFactory.getLogger(TemperatureGauge.class);

    public TemperatureGauge(boolean rotate) {
        super(rotate);
        this.setTitle("Temperature °C");
        this.out = "-- °C";

    }

    public void setTemperature(double temperature, boolean valid) {
        // 10 == -120.
        // 2 degree per degree.
        double celcius = temperature+CanMessageData.offsetCelcius;
        String newOut = "-- °C";
        if ( valid ) {
            newOut = String.format("%4.0f °C", celcius);
        }
        if ( !newOut.equals(out)) {
            out = newOut;
            repaint();
        }
        targetNeedleAngle = (int)(-120.0+((celcius)-10.0)*2.0);
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

        // 120 C for 240 degrees, from 11 to 15

        double step = 240.0/120.0;

        g2.rotate(-120*Math.PI/180.0);
        for (int i = 0; i < 121; i++) {
            int height;
            if ( i%15 == 0) {
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
        // vertical
        g2.rotate(-(120.0+step)*Math.PI/180.0);

    }


    @Override
    void annotateDial(Graphics2D g2, int digitRadius) {
        double cos30 = 0.8660254;
        double sin30 = 0.5;
        drawStringScaled("10",(int)-(cos30*digitRadius),(int)(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("25", (int)-(digitRadius), 0, unitsFont, g2);
        drawStringScaled("40", (int)-(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("55", (int)-(sin30*digitRadius), (int)-(cos30*digitRadius), unitsFont, g2);
        drawStringScaled("70", 0, -digitRadius, unitsFont, g2);
        drawStringScaled("85", (int)(sin30*digitRadius), (int)-(cos30*digitRadius), unitsFont, g2);
        drawStringScaled("100", (int)(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("115", digitRadius, 0, unitsFont, g2);
        drawStringScaled("130", (int)(cos30*digitRadius), (int)(sin30*digitRadius), unitsFont, g2);

    }

}
