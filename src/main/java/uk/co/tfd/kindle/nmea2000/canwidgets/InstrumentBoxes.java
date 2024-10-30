package uk.co.tfd.kindle.nmea2000.canwidgets;

import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;
import uk.co.tfd.kindle.nmea2000.can.Polar;

import javax.swing.*;
import java.awt.*;

public class InstrumentBoxes {
    public static class Performance3CellBox extends BaseBox3Cell {
        public Performance3CellBox() {
            super("",
                    new String[] {
                            "%",
                            "PSTW",
                            "STW",
                    },
                    new String[] {
                            "%3.0f",
                            "%3.1f",
                            "%3.1f"
                    },
                    new double[] {
                            100.0,
                            CanMessageData.scaleToKnots,
                            CanMessageData.scaleToKnots
                    },
                    1.0);
        }

        @Override
        public void update(double polarSpeedRatio, double polarSpeed, double stw) {
            super.update(polarSpeedRatio, polarSpeed, stw);
        }
    }
    public static class Target3CellBox extends BaseBox3Cell {
        public Target3CellBox() {
            super("",
                    new String[]{
                            "TTWA",
                            "TVMG",
                            "TSTW"
                    },
                    new String[]{
                            "%3.0f",
                            "%3.1f",
                            "%3.1f"
                    },
                    new double[]{
                            CanMessageData.scaleToDegrees,
                            CanMessageData.scaleToKnots,
                            CanMessageData.scaleToKnots
                    },
                    1.0
            );
        }


        public void update(Polar.PolarTarget upwindTarget, Polar.PolarTarget downwindTarget, double twa ) {
            Polar.PolarTarget target = upwindTarget;
            if (Math.abs(twa) > Math.PI / 2) {
                target = downwindTarget;
            }
            double twaFactor = CanMessageData.scaleToDegrees;
            if (target.twa < 0) {
                twaFactor = -twaFactor;
            }
            factors[0] = twaFactor;
            super.update(target.twa, target.vmg, target.stw);
        }


    }

    public static class TrueWind extends  BaseBox3Cell {
        public TrueWind() {

            super("",
                    new String[]{
                            "TWA",
                            "TWS",
                            "VMG"
                    },
                    new String[]{
                            "%1.0f",
                            "%3.1f",
                            "%3.1f"
                    },
                    new double[]{
                            CanMessageData.scaleToDegrees,
                            CanMessageData.scaleToKnots,
                            CanMessageData.scaleToKnots
                    },
                    1.0
            );
        }

        @Override
        public void update(double twa, double tws, double vmg) {
            if ( twa < 0 ) {
                factors[0] = -CanMessageData.scaleToDegrees;
                labels[0] = "TWA Port";
            } else {
                factors[0] = CanMessageData.scaleToDegrees;
                labels[0] = "TWA Starboard";
            }
            if ( vmg < 0 ) {
                factors[2] = -CanMessageData.scaleToKnots;
            } else {
                factors[2] = CanMessageData.scaleToKnots;
            }
            super.update(twa, tws, vmg);
        }
    }

    public static class ApparentWindBox extends BaseBox3Cell {
        public ApparentWindBox() {
            super( "",
                    new String[] {
                            "AWA",
                            "AWS",
                            "VMG"
                    },
                    new String[] {
                            "%1.0f",
                            "%3.1f",
                            "%3.1f"
                    },
                    new double[] {
                            CanMessageData.scaleToDegrees,
                            CanMessageData.scaleToKnots,
                            CanMessageData.scaleToKnots
                    },
                    1.0
            );

        }

        @Override
        public void update(double awa, double aws, double vmg) {
            if ( awa < 0 ) {
                factors[0] = -CanMessageData.scaleToDegrees;
                labels[0] = "AWA Port";
            } else {
                factors[0] = CanMessageData.scaleToDegrees;
                labels[0] = "AWA Starboard";
            }
            if ( vmg < 0 ) {
                factors[2] = -CanMessageData.scaleToKnots;
            } else {
                factors[2] = CanMessageData.scaleToKnots;
            }
            super.update(awa, aws, vmg);
        }
    }


    public static class BaseBox3Cell extends JPanel {

        private final Font largeValues;
        private final Font smallValues;
        private final Font labelsFont;
        double mainValueScale;
        String[] formats;
        private final double[] values = new double[] { CanMessageData.n2kDoubleNA, CanMessageData.n2kDoubleNA, CanMessageData.n2kDoubleNA};
        final double[] factors;
        String title;
        final String[] labels;
        private String out = "--";

        public BaseBox3Cell(String title, String[] labels, String[] formats, double[] factors, double mainValueScale) {
            // still cant fathom the logic with fonts. Sometimes
            // Util.createFonts is right, other times it needs specific adjustments.
            // her kindle needs a font size 4x smaller than osx, something to do with g2.scale
            // applie differently on fonts vs lines in a kindle, perhaps one of many bugs.
            largeValues = Util.createFont(Util.isKindle()?50.0f:100.0f);
            smallValues = Util.createFont(Util.isKindle()?25.0f:50.0f);
            labelsFont = Util.createFont(Util.isKindle()?13.0f:24.0f);
            this.title = title;
            this.labels = labels;
            this.formats = formats;
            this.factors = factors;
            this.mainValueScale = mainValueScale;
        }

        public void update(double largeValue, double small1, double small2) {
            values[0] = largeValue;
            values[1] = small1;
            values[2] = small2;
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 3; i++) {
                sb.append(formatValue(formats[i],values[i], factors[i]));
            }
            if ( !out.equals(sb.toString()) ) {
                out = sb.toString();
                repaint();
            }
        }

        @Override
        public void paintComponent(Graphics graphics) {

            Graphics2D g2 = (Graphics2D) graphics;

            int w = this.getWidth();
            // because width is scale no need to take into account scaling again.
            double scale = ((double) w) / 450;
            g2.scale(scale, scale);

            Color foreground = this.getForeground();
            Color background = this.getBackground();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.setBackground(foreground);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.setColor(foreground);
            g2.setBackground(background);

            int boxWidth = 449;
            int boxHeight = 149;
            int cellSplit = (449 * 55) / 100;
            int mainValueAlignX = (cellSplit / 2);
            int mainValueAlignY = boxHeight / 2 - 20;
            int subValueAlign = boxWidth - 10;
            g2.translate(mainValueAlignX, mainValueAlignY);
            g2.scale(mainValueScale, mainValueScale);
            Util.drawString(formatValue(formats[0], values[0], factors[0]),
                    0, 0,
                    largeValues,
                    Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
            g2.scale(1.0 / mainValueScale, 1.0 / mainValueScale);
            g2.translate(-mainValueAlignX, -mainValueAlignY);

            Util.drawString(formatValue(formats[1], values[1], factors[1]),
                    subValueAlign, boxHeight / 4 ,
                    smallValues,
                    Util.HAlign.DECIMAL, Util.VAlign.CENTER, g2);
            Util.drawString(formatValue(formats[2], values[2], factors[2]),
                    subValueAlign, 3 * boxHeight / 4 ,
                    smallValues,
                    Util.HAlign.DECIMAL, Util.VAlign.CENTER, g2);
            Util.drawString(title, 10, 5, labelsFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
            Util.drawString(labels[0], 10, boxHeight - 3, labelsFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
            Util.drawString(labels[1], cellSplit + 5, (boxHeight / 2) - 5, labelsFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
            Util.drawString(labels[2], cellSplit + 5, (boxHeight) - 5, labelsFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);

            g2.drawRoundRect(0, 0, boxWidth, boxHeight, 15, 15);
            g2.drawLine(cellSplit, 0, cellSplit, boxHeight);
            g2.drawLine(cellSplit, boxHeight / 2, boxWidth, boxHeight / 2);

            g2.scale(1.0 / scale, 1.0 / scale);


        }

        private String formatValue(String format, double value, double factor) {
            return (value == CanMessageData.n2kDoubleNA) ? "--" : String.format(format, value * factor);
        }
    }

}

