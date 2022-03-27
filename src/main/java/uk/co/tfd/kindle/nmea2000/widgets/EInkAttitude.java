package uk.co.tfd.kindle.nmea2000.widgets;


import uk.co.tfd.kindle.nmea2000.Data;
import uk.co.tfd.kindle.nmea2000.DisplayConversion;
import uk.co.tfd.kindle.nmea2000.Store;
import uk.co.tfd.kindle.nmea2000.Util;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.co.tfd.kindle.nmea2000.Data.DataValue;

/**
 * Created by ieb on 10/06/2020.
 */
public class EInkAttitude extends EInkTextBox {


    private String pitch = "-";
    private String roll = "-";
    private DecimalFormat pitchDataFormat = new DecimalFormat("A0.0\u00B0;F0.0\u00B0");
    private DecimalFormat rollDataFormat = new DecimalFormat("S0.0\u00B0;P0.0\u00B0");

    public EInkAttitude(boolean rotate, Map<String, Object> options, DisplayConversion.DisplayUnits displayUnits, Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        options.put("dataType", Data.DataType.RELATIVEANGLE);
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        if ( data instanceof Data.NMEA2KAttitude) {
            Data.NMEA2KAttitude attitudeData = (Data.NMEA2KAttitude) data;
            String newPitch = "pitch: " + this.displayUnits.toDispay(attitudeData.getPitch(), pitchDataFormat, Data.DataType.RELATIVEANGLE);
            String newRoll =  "roll : " + this.displayUnits.toDispay(attitudeData.getRoll(), rollDataFormat, Data.DataType.RELATIVEANGLE);
            if ( !newPitch.equals(pitch) || !newRoll.equals(roll)) {
                pitch = newPitch;
                roll = newRoll;
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("Wrong Class Key got "+data.getClass()+" expected "+Data.NMEA2KAttitude.class.toString() );
        }
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        Util.drawString(pitch, boxWidth / 2, boxHeight / 2, normalFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
        Util.drawString(roll, boxWidth / 2, boxHeight/2, normalFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
        this.drawBaseLine("attitude", "deg", g2);
    }


}
