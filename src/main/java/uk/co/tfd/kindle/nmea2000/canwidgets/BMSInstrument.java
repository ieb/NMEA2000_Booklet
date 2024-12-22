package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;
import uk.co.tfd.kindle.nmea2000.can.ElectricalMessageHandler;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class BMSInstrument  extends  JPanel{
    private static final Logger log = LoggerFactory.getLogger(BMSInstrument.class);


    private final Font largeValues;
    private final Font smallValues;
    private final Font labelsFont;
    String title;
    private String out = "--";
    private ElectricalMessageHandler.PGN130829BMSRegO3 bmsReg03;
    private ElectricalMessageHandler.PGN130829BMSRegO4 bmsReg04;
    private ElectricalMessageHandler.PGN130829BMSRegO5 bmsReg05;
    private double packVoltage = CanMessageData.n2kDoubleNA;
    private double packCurrent = CanMessageData.n2kDoubleNA;

    public BMSInstrument() {
        // still cant fathom the logic with fonts. Sometimes
        // Util.createFonts is right, other times it needs specific adjustments.
        // her kindle needs a font size 4x smaller than osx, something to do with g2.scale
        // applie differently on fonts vs lines in a kindle, perhaps one of many bugs.
        largeValues = Util.createFont(Util.isKindle()?50.0f:100.0f);
        smallValues = Util.createFont(Util.isKindle()?20.0f:40.0f);
        labelsFont = Util.createFont(Util.isKindle()?13.0f:24.0f);
    }

    public void update(ElectricalMessageHandler.PGN130829BMSRegO3 bmsReg03) {
        this.bmsReg03 = bmsReg03;
        if ( bmsReg03 != null) {
            this.packVoltage = bmsReg03.packVoltage;
            this.packCurrent = bmsReg03.packCurrent;
        }
        redrawIfRequired();
    }

    public void update(ElectricalMessageHandler.PGN130829BMSRegO4 bmsReg04) {
        this.bmsReg04 = bmsReg04;
        redrawIfRequired();
    }
    public void update(ElectricalMessageHandler.PGN130829BMSRegO5 bmsReg05) {
        this.bmsReg05 = bmsReg05;
        redrawIfRequired();
    }
    public void update(double batteryVoltage, double batteryCurrent) {
        this.packVoltage = batteryVoltage;
        this.packCurrent = batteryCurrent;
        redrawIfRequired();
    }

    private void redrawIfRequired() {
        StringBuilder sb = new StringBuilder();
        sb.append(Arrays.toString(new double[]{packVoltage, packCurrent}));
        if (bmsReg03 != null) {
            sb.append(Arrays.toString(new double[]{bmsReg03.remainingChargeCapacity, bmsReg03.fullChargeCapacity }));
        } else {
            sb.append("null:");
        }
        if (bmsReg04 != null) {
            sb.append(Arrays.toString(bmsReg04.cellVoltage));
        } else {
            sb.append("null:");
        }
        if (bmsReg05 != null) {
            sb.append((String.join(",",bmsReg05.hwVersion)));
        } else {
            sb.append("null:");
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
        double scale = ((double) w) / 800;
        g2.scale(scale, scale);

        Color foreground = this.getForeground();
        Color background = this.getBackground();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.setBackground(foreground);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2.setColor(foreground);
        g2.setBackground(background);

        int boxWidth = 800-2;
        int boxHeight = 800-2;

        int lines = 0;
        int lineHeight = 45;
        double na = CanMessageData.n2kDoubleNA;

        outputField(g2, 10,(lines++)*lineHeight,"Pack Voltage",  "V",packVoltage, "%3.2f", 1.0, 0.0 );
        outputField(g2, 10,(lines++)*lineHeight,"Pack Current",  "A",packCurrent, "%3.2f", 1.0, 0.0 );
        outputUInt8Field(g2, 10,(lines++)*lineHeight,"SoC","%",bmsReg03==null?0xff:bmsReg03.stateOfCharge);
        outputField(g2, 10,(lines++)*lineHeight,"Charge","Ah", bmsReg03==null?na:bmsReg03.remainingChargeCapacity, "%3.1f", 1.0, 0.0 );
        outputField(g2, 10,(lines++)*lineHeight,"Capacity","Ah",bmsReg03==null?na:bmsReg03.fullChargeCapacity, "%3.1f", 1.0, 0.0 );
        outputUInt16Field(g2, 10,(lines++)*lineHeight,"Cycles","",bmsReg03==null?0xffff:bmsReg03.chargeCycles );
        outputUInt8Field(g2, 10,(lines++)*lineHeight,"Cells","",bmsReg03==null?0xff:bmsReg03.nCells);
        outputField(g2, 10,(lines++)*lineHeight,"Balance","mA",bmsReg03==null?na:bmsReg03.ballanceCurrent, "%3.0f", 1.0, 0.0 );
        if ( bmsReg03 != null ) {
            String[] temps = new String[bmsReg03.temperatures.length];
            for ( int i = 0; i < bmsReg03.temperatures.length; i++) {
                temps[i] = formatValue("%3.1f", bmsReg03.temperatures[i],1.0, -273.15);
            }
            outputField(g2, 10,(lines++)*lineHeight,"Temperatures",Arrays.toString(temps), "C");
        }
        if ( bmsReg03 != null && bmsReg04 != null) {
            if ( bmsReg03.nCells != 255) {
                for (int i = 0; i < bmsReg04.cellVoltage.length && i < bmsReg03.nCells; i++) {
                    String title = "Cell" + i;
                    if (i < 16) {
                        if ((bmsReg03.ballanceStatus0 & (1 << i)) == (1 << i)) {
                            title = title + " B";
                        }
                    } else {
                        if ((bmsReg03.ballanceStatus0 & (1 << i - 16)) == (1 << i - 16)) {
                            title = title + " B";
                        }
                    }
                    if (i < bmsReg04.cellVoltage.length) {
                        outputField(g2, 10, (lines++) * lineHeight, title, "V", bmsReg04.cellVoltage[i], "%3.3f", 1.0, 0.0);
                    }
                }
                if (bmsReg04.cellVoltage.length > 0) {
                    double minCellV = bmsReg04.cellVoltage[0];
                    double maxCellV = minCellV;
                    for (int i = 0; i < bmsReg04.cellVoltage.length && i < bmsReg03.nCells; i++) {
                        minCellV = Math.min(minCellV, bmsReg04.cellVoltage[i]);
                        maxCellV = Math.max(maxCellV, bmsReg04.cellVoltage[i]);
                    }
                    outputField(g2, 10, (lines++) * lineHeight, "Cell Diff", "V", maxCellV - minCellV, "%3.3f", 1.0, 0.0);
                }
            }

            int col1 = 10, col2 = col1+120, col3 = col2+120, col4 = col3+120, col5 = col4+220;
            int row1 = lines*lineHeight+25, row2 = row1+25, row3=row1+50, row4=row1+75, row5=row1+110;
            Util.drawString("Alarms",
                    col1, lines*lineHeight,
                    labelsFont,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
            Util.drawString("Cell",
                    col1, row1,
                    labelsFont,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
            Util.drawString("Pack",
                    col2, row1,
                    labelsFont,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
            Util.drawString("Charge",
                    col3, row1,
                    labelsFont,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
            Util.drawString("Discharge",
                    col4, row1,
                    labelsFont,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);

            int bitmap = bmsReg03.protectionStatus;
            if (bitmap != 0xffff) {
                // Cell overvoltage
                drawToggle(g2, col1, row2, "Volt+", bitmap, 0x01);
                // Cell undervoltage
                drawToggle(g2, col1, row3, "Volt-", bitmap, 0x02);
                // Pack overvoltage
                drawToggle(g2, col2, row2, "Volt+", bitmap, 0x04);
                // Pack undervoltage
                drawToggle(g2, col2, row3, "Volt-", bitmap, 0x08);
                // Charge overtemp
                drawToggle(g2, col3, row2, "Temp+", bitmap, 0x10);
                // Charge undertemp
                drawToggle(g2, col3, row3, "Temp-", bitmap, 0x20);
                // disCharge overtemp
                drawToggle(g2, col4, row2, "Temp+", bitmap, 0x40);
                // disCharge undertemp
                drawToggle(g2, col4, row3, "Temp-", bitmap, 0x80);
                // charge overcurrent
                drawToggle(g2, col3, row4, "Current-", bitmap, 0x100);
                // discharge overcurrent
                drawToggle(g2, col4, row4, "Current+", bitmap, 0x200);

                drawToggle(g2, col5, row2, "Short Circuit", bitmap, 0x400);
                drawToggle(g2, col5, row3, "IC Error", bitmap, 0x800);
                drawToggle(g2, col5, row4, "FetLock", bitmap, 0x1000);
            }

            g2.drawRoundRect(0, row1, boxWidth, 100, 15, 15);

            drawToggle(g2, col1, row5, "Charge Off", "Charge On",bmsReg03.fetControl, 0x01);
            drawToggle(g2, col3, row5, "Discharge Off", "Discharge On", bmsReg03.fetControl, 0x02);

            if ( bmsReg05 != null) {
                byte[] b = bmsReg05.hwVersion.getBytes(StandardCharsets.UTF_8);
                for(int i = 0; i < b.length; i++) {
                    if (b[i] != 0) {
                        log.info("HW Version {} {}",bmsReg05.hwVersion, Arrays.toString(bmsReg05.hwVersion.getBytes(StandardCharsets.UTF_8)));
                        Util.drawString(bmsReg05.hwVersion,
                                col5, row5,
                                labelsFont,
                                Util.HAlign.LEFT, Util.VAlign.TOP, g2);
                        break;
                    }
                }
            }

            //
            //x10|Current errors|16 bit:<br>
            // bit 0: Cell overvolt<br>
            // bit 1: Cell undervolt<br>
            // bit 2: Pack overvolt<br>
            // bit 3: Pack undervolt<br>
            // bit 4: Charge overtemp<br>
            // bit 5: Charge undertemp<br>
            // bit 6: Discharge overtemp<br>
            // bit 7: Discharge undertemp<br>
            // bit 8: Charge overcurrent<br>
            // bit 9: Discharge overcurrent<br>
            // bit 10: Short Circuit<br>
            // bit 11: Frontend IC error<br>
            // bit 12: Charge or Discharge FET locked by config (See register 0xE1 "MOSFET control")<br>
        }

        g2.drawRoundRect(0, 0, boxWidth, boxHeight, 15, 15);

        g2.scale(1.0 / scale, 1.0 / scale);


    }


    private void drawToggle(Graphics2D g2, int x, int y, String label, int bitmap, int check) {
        if ( (bitmap & check) == (check) ) {
            Util.drawString(label,
                    x, y,
                    labelsFont,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        }

    }
    private void drawToggle(Graphics2D g2, int x, int y, String offLable,  String onLable, int bitmap, int check) {
        if ( (bitmap & check) == (check) ) {
            Util.drawString(onLable,
                    x, y,
                    labelsFont,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        } else {
            Util.drawString(offLable,
                    x, y,
                    labelsFont,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);

        }

    }


    private void outputField(Graphics2D g2, int x, int y, String title, String units, double value, String format, double scale, double offset) {
        Util.drawString(title,
                x, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString(formatValue(format, value, scale, offset),
                x+350, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString(units,
                x+700, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);

    }

    private void outputField(Graphics2D g2, int x, int y, String title, String value, String units) {
        Util.drawString(title,
                x, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        if ( value == null ) {
            Util.drawString("--",
                    x+350, y,
                    smallValues,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        } else {
            Util.drawString(value.toString(),
                    x+350, y,
                    smallValues,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        }
        Util.drawString(units,
                x+700, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);

    }

    private void outputUInt8Field(Graphics2D g2, int x, int y, String title, String units, int value) {
        Util.drawString(title,
                x, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        if ( value == 0xff ) {
            Util.drawString("--",
                    x+350, y,
                    smallValues,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        } else {
            Util.drawString(String.valueOf(value),
                    x+350, y,
                    smallValues,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        }
        Util.drawString(units,
                x+700, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);
    }
    private void outputUInt16Field(Graphics2D g2, int x, int y, String title, String units, int value) {
        Util.drawString(title,
                x, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        if ( value == 0xffff ) {
            Util.drawString("--",
                    x+350, y,
                    smallValues,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        } else {
            Util.drawString(String.valueOf(value),
                    x+350, y,
                    smallValues,
                    Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        }
        Util.drawString(units,
                x+700, y,
                smallValues,
                Util.HAlign.LEFT, Util.VAlign.TOP, g2);
    }


    private String formatValue(String format, double value, double factor, double offset) {
        return (value == CanMessageData.n2kDoubleNA) ? "--" : String.format(format, (value * factor)+offset);
    }

}
