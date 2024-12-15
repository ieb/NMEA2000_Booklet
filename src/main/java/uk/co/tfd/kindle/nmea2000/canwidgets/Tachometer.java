package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;

import java.awt.*;

public class Tachometer extends BaseGauge  {
    private static final Logger log = LoggerFactory.getLogger(Tachometer.class);
    private String enginHoursOut = "-- h";
    private int engineStatus1 = 0xffffffff;
    private int engineStatus2 = 0xffffffff;
    private double fuelLevel;

    public Tachometer(boolean rotate) {
        super(rotate);
        setTitle("RPMx1000");
    }

    public void setRpm(double rpm, boolean valid) {

        String newOut = "-- rpm";
        if ( valid ) {
            newOut = String.format("%4.0f rpm", rpm);
        }
        if ( !newOut.equals(out)) {
            out = newOut;
            repaint();
        }

        targetNeedleAngle = (int)(-120.0+(rpm*0.06));

        if ( needleAngle != targetNeedleAngle ) {
            repaint();
            timer.restart();
        }
    }

    public void setFuelLevel(double fuelLevel, boolean valid) {
        if ( this.fuelLevel != fuelLevel) {
            this.fuelLevel = fuelLevel;
            repaint();
        }
    }

    public void setEngineHours(double engineHours, boolean valid) {
        String newOut = "-- h";
        if ( valid ) {
            newOut = String.format("%4.1f h", engineHours * CanMessageData.scaleSecondsToHours);
        }
        if ( !newOut.equals(enginHoursOut)) {
            enginHoursOut = newOut;
            repaint();
        }
    }

    public void setStatus1(int status1, boolean valid) {
        if (valid) {
            if ( engineStatus1 != status1 ) {
                engineStatus1 = status1;
                repaint();
            }
        } else if ( engineStatus1 != 0xffff ) {
            engineStatus1 = 0xfffff;
            repaint();
        }
    }

    public void setStatus2(int status2, boolean valid) {
        if (valid) {
            if ( engineStatus2 != status2 ) {
                engineStatus2 = status2;
                repaint();
            }
        } else if ( engineStatus2 != 0xffff ) {
            engineStatus2 = 0xfffff;
            repaint();
        }
    }
    @Override
    void drawDial(Graphics2D g2, int tickRadius) {
        g2.rotate(-120*Math.PI/180.0);
        Stroke defaultStroke = g2.getStroke();
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
            g2.rotate(6*Math.PI/180.0);
            g2.setStroke(defaultStroke);
        }
        // vertical
        g2.rotate(-126*Math.PI/180.0);

    }



    @Override
    void annotateDial(Graphics2D g2, int digitRadius) {
        double cos30 = 0.8660254;
        double sin30 = 0.5;


        int x = -14-digitRadius/2;
        int y = 0;
        if ((engineStatus1 & 0x02) == 0x02) {
            drawEngineOverTemperature(g2, x, y);
        } else if ((engineStatus1 & 0x400) == 0x400) {
            drawPreheat(g2, x, y);
        } else if ((engineStatus2 & 0x40) == 0x40) {
            drawEngineStarting(g2, x, y);
        } else if ((engineStatus1 & 0x04) == 0x04) {
            drawOilPressureWarning(g2, x, y);
        } else if ((engineStatus2 & 0x80) == 0x80) {
            drawEngineStopping(g2, x, y);
        }
        if (engineStatus1 == 0xffff || (engineStatus1) == 0x01  ) {
            drawSystemFault(g2, digitRadius/2,  -20-digitRadius/2);
        }
        if (engineStatus1 == 0xffff || (engineStatus1 & 0x20) == 0x20) {
            drawBatteryLow(g2, -digitRadius/2,-17-digitRadius/2 );
        }
        if (engineStatus1 == 0xffff || fuelLevel < 0.1 ) {
            drawFuelLoweWarning(g2, 10+digitRadius/2, 0);
        }

        // keep this at the end to avoid a drawing problem.
        drawStringScaled("0",(int)-(cos30*digitRadius),(int)(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("1", (int)-(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont, g2);
        drawStringScaled("2", 0, -digitRadius, unitsFont,  g2);
        drawStringScaled("3", (int)(cos30*digitRadius), (int)-(sin30*digitRadius), unitsFont,  g2);
        drawStringScaled("4", (int)(cos30*digitRadius), (int)(sin30*digitRadius), unitsFont,  g2);
        drawStringScaled(enginHoursOut, 0, 40+digitRadius/2, unitsFont,  g2);

/*
        if ((status1&0x01) == 0x01 ) statusMesssagesList.add("Check Engine");
        if ((status1 & 0x02) == 0x02) statusMesssagesList.add("Over Temperature");
        if ((status1 & 0x04) == 0x04) statusMesssagesList.add("Low Oil Pressure");
        if ((status1 & 0x08) == 0x08) statusMesssagesList.add("Low Oil Level");
        if ((status1 & 0x10) == 0x10) statusMesssagesList.add("Low Fuel Pressure");
        if ((status1 & 0x20) == 0x20) statusMesssagesList.add("Low System Voltage");
        if ((status1 & 0x40) == 0x40) statusMesssagesList.add("Low Coolant Level");
        if ((status1 & 0x80) == 0x80) statusMesssagesList.add("Water Flow");
        if ((status1 & 0x100) == 0x100) statusMesssagesList.add("Water In Fuel");
        if ((status1 & 0x200) == 0x200) statusMesssagesList.add("Charge Indicator");
        if ((status1 & 0x400) == 0x400) statusMesssagesList.add("Preheat Indicator");
        if ((status1 & 0x800) == 0x800) statusMesssagesList.add("High Boost Pressure");
        if ((status1 & 0x1000) == 0x1000) statusMesssagesList.add("Rev Limit Exceeded");
        if ((status1 & 0x2000) == 0x2000) statusMesssagesList.add("EGR System");
        if ((status1 & 0x4000) == 0x4000) statusMesssagesList.add("Throttle Position Sensor");
        if ((status1 & 0x8000) == 0x8000) statusMesssagesList.add("Emergency Stop");
        if ((status2 & 0x01) == 0x01) statusMesssagesList.add("Warning Level 1");
        if ((status2 & 0x02) == 0x02) statusMesssagesList.add("Warning Level 2");
        if ((status2 & 0x04) == 0x04) statusMesssagesList.add("Power Reduction");
        if ((status2 & 0x08) == 0x08) statusMesssagesList.add(" Maintenance Needed");
        if ((status2 & 0x10) == 0x10) statusMesssagesList.add("Engine Comm Error");
        if ((status2 & 0x20) == 0x20) statusMesssagesList.add("Sub or Secondary Throttle");
        if ((status2 & 0x40) == 0x40) statusMesssagesList.add("Neutral Start Protect");
        if ((status2 & 0x80) == 0x80) statusMesssagesList.add("Engine Shutting Down");
*/
    }

    private void drawFuelLoweWarning(Graphics2D g2, int x, int y) {
        Stroke defaultStoke = g2.getStroke();
        //fuel
        g2.translate(x, y);
        g2.setStroke(medTickStroke);
        g2.drawRoundRect(-6,-12, 12, 24, 4, 4);
        g2.drawLine(-9,12, 9, 12);
        g2.drawLine(-6,-3, 6, -3);
        g2.drawPolyline(new int[] {-14,-14,-8, -14 }, new int[] {-8,4,0, -4} , 4);
        g2.drawArc(5,-3, 4,4, 90, -80);
        g2.drawLine(9,-1, 9, 8 );
        g2.drawArc(9,6, 4,4, 180, 190);
        g2.drawLine(13, 8, 12, -5 );
        g2.setStroke(thickTickStroke);
        g2.drawArc(5, -9, 8,8, 10, 80);
        g2.setStroke(defaultStoke);

        g2.translate(-x, -y);
    }

    private void drawBatteryLow(Graphics2D g2, int x, int y) {
        Stroke defaultStoke = g2.getStroke();
        g2.translate(x, y);
        g2.setStroke(medTickStroke);
        g2.drawRect(-12,-8, 24,14);
        g2.drawLine(-10,-9, -5, -9);
        g2.drawLine(10,-9, 4, -9);
        g2.drawLine(-9,-2, -4, -2);
        g2.drawLine(9,-2, 5, -2);
        g2.drawLine(7,-4, 7, 0);
        g2.setStroke(defaultStoke);
        g2.fillPolygon(new int[] {14,15,16, 17 }, new int[] {-11,1,1, -11} , 4);
        g2.fillArc(14,3, 4, 4, 0 , 360);

        g2.translate(-x, -y);
    }

    private void drawSystemFault(Graphics2D g2, int x, int y) {
        Stroke defaultStoke = g2.getStroke();
        g2.translate(x,y );
        g2.scale(0.5, 0.5);
        g2.setStroke(thickTickStroke);
        g2.drawPolyline(new int[] {-16,0,16, -16 }, new int[] {20,-16,20, 20} , 4);
        g2.setStroke(defaultStoke);
        g2.fillPolygon(new int[] {-3,-2,2, 3 }, new int[] {-4,8,8, -4} , 4);
        g2.fillArc(-3,10, 6, 6, 0 , 360);
        g2.scale(2.0, 2.0);
        g2.translate(-x, -y);
    }

    private void drawEngineOverTemperature(Graphics2D g2, int x, int y) {
        g2.translate(x,y);
        drawEngineIcon(g2);
        g2.drawArc(-10, -5, 5, 6, 0, -90);
        g2.drawArc(-5, -5, 5, 6, 180, 180);
        g2.drawArc(0, -5, 5, 6, 180, 180);
        g2.drawArc(5, -5, 5, 6, 180, 90);

        g2.fillArc(12, 4, 6, 6, 0, 360);
        g2.drawLine(14,5, 14,0);
        g2.drawLine(15,5, 15,0);
        g2.drawLine(16,5, 16,0);
        g2.drawLine(14,-1, 14,-8);
        g2.drawLine(16,-1, 16,-8);
        g2.drawArc(14, -10, 2, 2, 180, -180);
        g2.translate(-x, -y);
    }

    private void drawEngineStopping(Graphics2D g2, int x, int y) {
        g2.translate(x,y);
        drawEngineIcon(g2);
        g2.scale(0.7,0.7);
        Util.drawString("Stop", 0,-2, dialFont, Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        g2.scale(1.0/0.7,1.0/0.7);
        g2.translate(-x, -y);
    }

    private void drawOilPressureWarning(Graphics2D g2, int x, int y) {
        g2.translate(x,y);
        drawEngineIcon(g2);
        g2.drawArc(-8, -14, 8, 12, 0, -60);
        g2.drawArc(-3, -3, 6, 6, 145, 250);
        g2.drawArc(0, -14, 8, 12, 180, 60);
        g2.fillPolygon(new int[] {-16,-12,-16}, new int[]{-3,0,3}, 3);
        g2.fillPolygon(new int[] {16,12,16}, new int[]{-3,0,3}, 3);
        g2.translate(-x, -y);
    }

    private void drawEngineStarting(Graphics2D g2, int x, int y) {
        g2.translate(x,y);
        drawEngineIcon(g2);
        g2.drawArc(-7, -7, 14, 14, 180, -180);
        g2.drawLine(-8,0,-6,0);
        g2.fillPolygon(new int[] {5,7,9}, new int[]{0,2,0} , 3);
        g2.translate(-x, -y);
    }


    private  void drawPreheat(Graphics2D g2, int x, int y) {
        g2.translate(x,y);
        drawEngineIcon(g2);
        g2.drawArc(-10, -5, 9, 10, 90, -90);
        g2.drawArc(-6, -5, 5, 10, 0, -180);
        g2.drawArc(-6, -5, 12, 10, 180, -180);
        g2.drawArc(1, -5, 5, 10, 0, -180);
        g2.drawArc(1, -5, 9, 10, 180, -90);
        g2.translate(-x, -y);
    }

    private void drawEngineIcon(Graphics2D g2) {
        Stroke defaultStoke = g2.getStroke();
        g2.setStroke(thickTickStroke);
        g2.drawArc(-10,-10, 20,20, 120,300);
        g2.drawPolyline(new int[]{-5,-5,5,5}, new int[] {-9,-15,-15,-9}, 4);
        g2.setStroke(defaultStoke);
    }




}
