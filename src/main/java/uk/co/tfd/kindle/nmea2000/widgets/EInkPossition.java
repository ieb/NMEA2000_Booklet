package uk.co.tfd.kindle.nmea2000.widgets;

import uk.co.tfd.kindle.nmea2000.Data;
import uk.co.tfd.kindle.nmea2000.DisplayConversion;
import uk.co.tfd.kindle.nmea2000.Store;
import uk.co.tfd.kindle.nmea2000.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

import static uk.co.tfd.kindle.nmea2000.Data.DataValue;

/**
 * Created by ieb on 10/06/2020.
 */

public class EInkPossition extends EInkTextBox {


    private String longitude = "---\u00B0--.---\u2032W";
    private String latitude = "--\u00B0--.---\u2032N";
    private String date = "-";


    public EInkPossition(boolean rotate, Map<String, Object> options, DisplayConversion.DisplayUnits displayUnits, Store store) {
        super(rotate, updateOptions(options), displayUnits, store);
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        String newLongitude = longitude;
        String newLatitude = latitude;
        String newDate = date;
        if (data instanceof Data.NMEA2KGnss) {
            Data.NMEA2KGnss fixData = (Data.NMEA2KGnss) data;
            newLongitude = this.displayUnits.toDispay(fixData.getLongitude(), null, Data.DataType.LONGITUDE);
            newLatitude = this.displayUnits.toDispay(fixData.getLatitude(), null, Data.DataType.LATITUDE);
            newDate = fixData.getFixDate();
        } else {
            throw new IllegalArgumentException("Wrong DataValue "+data.getClass()+" expected  "+Data.NMEA2KGnss.class.toString() );
        }
        if ( !newLongitude.equals(longitude) ||
                !newLatitude.equals(latitude) ||
                !newDate.equals(date) ) {
            longitude = newLongitude;
            latitude =  newLatitude;
            date = newDate;
            return true;
        }
        return false;
    }



    @Override
    void renderInstrument(Graphics2D g2) {
        Util.drawString(latitude, boxWidth / 2, boxHeight/2, normalFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
        Util.drawString(longitude, boxWidth / 2, boxHeight/2, normalFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
        Util.drawString(date, borderPadding, smallLineSpace, smallFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
        this.drawBaseLine("pos", "lat/lon", g2);

    }




}

