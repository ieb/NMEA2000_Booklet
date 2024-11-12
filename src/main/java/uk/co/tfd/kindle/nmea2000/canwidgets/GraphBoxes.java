package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GraphBoxes {

    public static class PerformanceGraph extends BaseGraph {
        public PerformanceGraph() {
            super("stw", 1000, 15000);
        }

        @Override
        protected String getActualDisplayValue() {
            if ( history[history.length - 1] == CanMessageData.n2kDoubleNA ) {
                return null;
            }
            if ( targetHistory[history.length - 1] > 0.01 ) {
                double polarRatio = 100.0 * (history[history.length - 1] / targetHistory[history.length - 1]);
                return String.format("%3.0f %%", polarRatio);
            }
            return null;
        }
        @Override
        protected String getTargetDisplayValue() {
            if ( targetHistory[history.length - 1] == CanMessageData.n2kDoubleNA ) {
                return null;
            }
            if ( targetHistory[history.length - 1] > 0.01 ) {
                double tstw = targetHistory[history.length - 1]*CanMessageData.scaleToKnots;
                return String.format("%2.1f kn", tstw);
            }
            return null;
        }

        @Override
        protected double toGraphAxisY(double v) {
            return v/CanMessageData.scaleToKnots;
        }

        @Override
        protected int getMaxGraphAxisY() {
            if ( actualValue == CanMessageData.n2kDoubleNA ) {
                return 5;
            }
            return (int)Math.ceil(actualValue *1.2*CanMessageData.scaleToKnots);
        }

        @Override
        protected int getMinGraphAxisY() {
            if ( actualValue == CanMessageData.n2kDoubleNA ) {
                return 0;
            }
             return (int)Math.floor(actualValue *0.8*CanMessageData.scaleToKnots);
        }

    }

    public static class DepthGraph extends BaseGraph {
        public DepthGraph() {
            super("dbt", 1000, 15000);
            drawArrow = false;
        }

        @Override
        protected String getActualDisplayValue() {
            return String.format("%2.1f m", -history[history.length - 1]);
        }
        @Override
        protected String getTargetDisplayValue() {
            return String.format("%2.1f m", -targetHistory[history.length-1]);
        }

        @Override
        protected double toGraphAxisY(double v) {
            return v;
        }
        @Override
        protected String toDisplayUnits(int i) {
            return super.toDisplayUnits(-i);
        }

        @Override
        protected int getMinGraphAxisY() {
            double maxDepth = -5.0;
            for(double d : history) {
                if ( d != CanMessageData.n2kDoubleNA) {
                    maxDepth = Math.min(maxDepth, d);
                }
            }
            if ( maxDepth > -15 ) {
                graphAxizYStep = 1;
            } else if ( maxDepth > -30 ) {
                graphAxizYStep = 2;
            } else if ( maxDepth > -70 ) {
                graphAxizYStep = 5;
            } else if ( maxDepth > -190 ) {
                graphAxizYStep = 10;
            }
            return (int)Math.ceil(maxDepth *1.1);
        }

        @Override
        protected int getMaxGraphAxisY() {
            return (int)0.0;
        }

    }
    /**
     * The max and min of an angular plot is a bit more problematic when the
     * angle goes through -180 to +180 or 360 to 0.
     * Probably need to use an offset on the values so that they are always +ve, but then
     * remove that offset for the scale.
     * The angular differences between readings are small so we can aproximate any means,
     * rather than using an polar mean.
     */
    public static class RelativeAngleGraph extends BaseGraph {
        public RelativeAngleGraph(String title) {
            super(title,1000, 15000);
            graphAxizYStep = 2;
        }

        @Override
        protected String getTargetDisplayValue() {
            return String.format("%3.0f", -targetValue*CanMessageData.scaleToDegrees);
        }


        @Override
        protected String getActualDisplayValue() {
            return String.format("%3.0f", -actualValue*CanMessageData.scaleToDegrees);
        }

        @Override
        protected String toDisplayUnits(int i) {
            return super.toDisplayUnits(-i);
        }

        @Override
        protected double toGraphAxisY(double yUnits) {
            return yUnits/CanMessageData.scaleToDegrees;
        }
        @Override
        protected int getMaxGraphAxisY() {
            // plus ten degrees.
            int max =  (int)Math.ceil(actualValue*CanMessageData.scaleToDegrees+10.0);
            if ( max > 0) {
                max = 0;
            } else if (max < -180) {
                max = -160;
            }
            return max;

        }

        @Override
        protected int getMinGraphAxisY() {
            // minus 10 degrees,
            int min = (int)Math.floor(actualValue*CanMessageData.scaleToDegrees-10.0);
            if (min > 0) {
                min = -30;
            } else if (min < -180) {
                min = -180;
            }
            return min;
        }


        protected void update(double twa, double upwindTargetTWA, double downwindTargetTWA) {
            if ( twa == CanMessageData.n2kDoubleNA ) {
                this.update(0, 0);
            } else if ( Math.abs(twa) < Math.PI/2) {
                this.update(-Math.abs(twa), -Math.abs(upwindTargetTWA));
            } else {
                this.update(-Math.abs(twa), -Math.abs(downwindTargetTWA));
            }
        }
    }
    public static abstract class BaseGraph extends JPanel {
        private static final Logger log = LoggerFactory.getLogger(PerformanceGraph.class);
        private final static BasicStroke heavyStroke = new BasicStroke(2.0f);

        private final long interval;
        private final long timePeriod;
        private String title;
        private Font labelFont;
        private Font titleFont;
        private final int graphOriginX = 40;
        private final int graphOriginY = 280;
        private final int graphWidth = 910-graphOriginX;
        private final int graphHeight = graphOriginY-10;
        private final int[] xPoints;
        private final int[] yPoints;
        protected final double[] history;
        protected final double[] targetHistory;
        private int maxAxisYUnits;
        private int minGraphAxisY;
        private double yScale;
        private double xScale;
        private long lastSample;
        protected double targetValue;
        protected double actualValue;
        protected int graphAxizYStep = 1;
        protected boolean drawArrow = true;


        public BaseGraph(String title, long interval, long timePeriod) {
            // font scaling still doesnt work as expected.
            labelFont = Util.createFont(Util.isKindle()?10.0f:20.0f);
            titleFont = Util.createFont(Util.isKindle()?15.0f:30.0f);
            xPoints = new int[201];
            yPoints = new int[201];
            history = new double[100];
            targetHistory = new double[100];
            this.title = title;
            this.interval = interval;
            this.timePeriod = timePeriod;
            xScale = (double)graphWidth/(double)(interval*history.length);
            double historyScale = (double)graphWidth/(double)(history.length-1);
            // xPoints and yPoints go from 0 to max to 0 again,
            // with 0 == lastEntry to complete the polygon
            for(int i = 0; i < history.length; i++) {
                history[i] = CanMessageData.n2kDoubleNA;
                targetHistory[i] = CanMessageData.n2kDoubleNA;
                xPoints[i] = graphOriginX + (int) (historyScale * (double)(i));
                xPoints[xPoints.length-2-i] = graphOriginX + (int) (historyScale * (double)(i));
                yPoints[i] = graphOriginY;
                yPoints[yPoints.length-2-i] = graphOriginY;
            }
            // setup the ends
            xPoints[xPoints.length-1] = xPoints[0];
            yPoints[yPoints.length-1] = yPoints[0];

            lastSample = System.currentTimeMillis();
            Timer t = new Timer((int)interval, new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    BaseGraph.this.save();
                }
            });
            t.start();

        }

        protected abstract int getMinGraphAxisY();

        protected abstract int getMaxGraphAxisY();

        protected abstract double toGraphAxisY(double v);

        protected String getActualDisplayValue() {
            return null;
        }
        protected String getTargetDisplayValue() {
            return null;
        }




        protected String toDisplayUnits(int i) {
            return String.valueOf(i);
        }


        protected void update(double actualValue, double targetValue) {
            if ( actualValue == CanMessageData.n2kDoubleNA || this.actualValue == CanMessageData.n2kDoubleNA) {
                this.actualValue = actualValue;
            } else  {
                this.actualValue = (this.actualValue * 2.0 + actualValue)/3.0;
            }
            if ( targetValue == CanMessageData.n2kDoubleNA || this.targetValue == CanMessageData.n2kDoubleNA) {
                this.targetValue = targetValue;
            } else  {
                this.targetValue = (this.targetValue * 2.0 + targetValue)/3.0;
            }
        }
        private void save() {
            for(int i = 2; i < history.length; i++) {
                history[i-1] = history[i];
                targetHistory[i-1] = targetHistory[i];
            }
            history[history.length-1] = actualValue;
            targetHistory[targetHistory.length-1] = targetValue;
            lastSample = System.currentTimeMillis();
            // calculate min and max using kn and rouding.
            maxAxisYUnits = getMaxGraphAxisY();
            minGraphAxisY = getMinGraphAxisY();
            yScale = -graphHeight/ toGraphAxisY((maxAxisYUnits - minGraphAxisY));
            double offset = toGraphAxisY(minGraphAxisY);;
            for(int i = 0; i < history.length; i++) {
                if ( history[i] == CanMessageData.n2kDoubleNA) {
                    yPoints[i] = graphOriginY;
                } else {
                    yPoints[i] = Math.max(graphOriginY-graphHeight,
                            Math.min(graphOriginY,
                                graphOriginY+(int)(yScale*(history[i]-offset))));
                }
                if ( targetHistory[i] == CanMessageData.n2kDoubleNA) {
                    yPoints[yPoints.length-2-i] = graphOriginY;
                } else {
                    yPoints[yPoints.length-2-i] = Math.max(graphOriginY-graphHeight,
                            Math.min(graphOriginY,
                                    graphOriginY+(int)(yScale*(targetHistory[i]-offset))));
                }
            }
            yPoints[yPoints.length-1] = yPoints[0];
            repaint();
        }



        @Override
        public void paint(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics;


            int w = this.getWidth();
            // because width is scale no need to take into account scaling again.
            double scale = ((double) w) / 1000;
            g2.scale(scale, scale);

            Color foreground = this.getForeground();
            Color background = this.getBackground();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.setBackground(foreground);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.setColor(foreground);
            g2.setBackground(background);


            g2.drawRect(graphOriginX,10, graphWidth, graphHeight);
            // label the y axis
            if ( maxAxisYUnits >= minGraphAxisY && ((maxAxisYUnits-minGraphAxisY)/graphAxizYStep) < 20 ) {
                for (int i = minGraphAxisY; i <= maxAxisYUnits; i += graphAxizYStep) {
                    int y = graphOriginY + (int) (yScale * toGraphAxisY((i - minGraphAxisY)));
                    g2.drawLine(graphOriginX, y, graphOriginX + graphWidth, y);
                    Util.drawString(toDisplayUnits(i), graphOriginX - 8, y, labelFont, Util.HAlign.RIGHT, Util.VAlign.CENTER, g2);
                    y = graphOriginY + (int) (yScale * (0.5 + i));
                    g2.drawLine(graphOriginX, y, graphOriginX - 4, y);
                }
            } else {
                log.info("Invalid Y axis settings min {}  should be less than max {} step {} ", minGraphAxisY, maxAxisYUnits, graphAxizYStep);
            }



            long lastMajor = (long)(Math.floor(lastSample/timePeriod)*timePeriod);
            long startOfHistory = lastSample-(history.length-2)*interval;

            GregorianCalendar g = new GregorianCalendar();
            for (; lastMajor > startOfHistory; lastMajor -= timePeriod) {
                int x = graphOriginX + (int)(xScale*(lastMajor-startOfHistory));
                g.setTimeInMillis(lastMajor);
                String xaxisLabel = String.format("%02d:%02d:%02d", g.get(Calendar.HOUR_OF_DAY), g.get(Calendar.MINUTE), g.get(Calendar.SECOND));
                g2.drawLine(x, graphOriginY, x,  graphOriginY - graphHeight);
                Util.drawString(xaxisLabel, x, graphOriginY+8, labelFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
            }
            g2.fillPolygon(xPoints, yPoints, xPoints.length);
            String actualDisplayValue = getActualDisplayValue();
            if ( actualDisplayValue != null) {
                int y = yPoints[history.length - 1];
                int x = xPoints[history.length - 1] + 3;
                Util.drawString(actualDisplayValue, x, y, labelFont, Util.HAlign.LEFT, Util.VAlign.CENTER, g2);
            }
            String targetDisplayValue = getTargetDisplayValue();
            if ( targetDisplayValue != null) {
                int y = yPoints[history.length];
                int x = xPoints[history.length] + 3;
                Util.drawString(targetDisplayValue, x, y, labelFont, Util.HAlign.LEFT, Util.VAlign.CENTER, g2);
            }
            if ( drawArrow ) {
                Stroke normalStroke = g2.getStroke();
                g2.setStroke(heavyStroke);
                if ((yPoints[history.length] - yPoints[history.length - 1]) > 40) {
                    int x = xPoints[history.length - 1] + 20;
                    int y = yPoints[history.length - 1];
                    g2.drawPolyline(new int[]{
                            x,
                            x,
                            x - 5,
                            x + 5,
                            x
                    }, new int[]{
                            y + 10,
                            y + 40,
                            y + 35,
                            y + 35,
                            y + 40

                    }, 5);
                } else if ((yPoints[history.length] - yPoints[history.length - 1]) < -40) {
                    int x = xPoints[history.length - 1] + 20;
                    int y = yPoints[history.length - 1];
                    g2.drawPolyline(new int[]{
                            x,
                            x,
                            x - 5,
                            x + 5,
                            x
                    }, new int[]{
                            y - 10,
                            y - 40,
                            y - 35,
                            y - 35,
                            y - 40
                    }, 5);

                }
                g2.setStroke(normalStroke);
            }
            Util.drawString(title, graphOriginX + 5, graphOriginY, titleFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);

            g2.scale(1.0/scale, 1.0/scale);

        }


        public void setTitle(String title) {
            this.title = title;
        }
    }

}
