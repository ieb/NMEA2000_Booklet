package uk.co.tfd.kindle.nmea2000.widgets;

/**
 * Created by ieb on 09/06/2020.
 */

import uk.co.tfd.kindle.nmea2000.Data;
import uk.co.tfd.kindle.nmea2000.DisplayConversion;
import uk.co.tfd.kindle.nmea2000.Store;
import uk.co.tfd.kindle.nmea2000.Util;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static uk.co.tfd.kindle.nmea2000.Data.*;


/**
 * Created by ieb on 09/06/2020.
 */
public class EInkLog extends EInkTextBox {


    private String trip = "-.-";
    private String log = "-.-";
    private DecimalFormat tripFormat = new DecimalFormat("trip 0.0");
    private DecimalFormat logFormat = new DecimalFormat("log 0.0");

    public EInkLog(boolean rotate, Map<String, Object> options, DisplayConversion.DisplayUnits displayUnits, Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        Map<String, String> labels = new HashMap<>();
        labels.put("bl","Log");
        labels.put("br","Nm");
        options.put("labels",labels);
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        String newTrip = trip;
        String newLog = log;
        if ( data instanceof Data.NMEA2KLog) {
            Data.NMEA2KLog logData = (Data.NMEA2KLog) data;
            newLog = this.displayUnits.toDispay(logData.getLog(), logFormat, DataType.DISTANCE);
            newTrip = this.displayUnits.toDispay(logData.getTrip(), tripFormat, DataType.DISTANCE);
        } else {
            throw new IllegalArgumentException("Wrong DataValue Key got "+data.getClass()+" expected one "+Data.NMEA2KLog.class.toString());
        }
        if ( !newTrip.equals(trip) || !newLog.equals(log)) {
            trip = newTrip;
            log = newLog;
            return true;
        }
        return false;
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        Util.drawString(trip, boxWidth / 2, boxHeight / 2, normalFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
        Util.drawString(log, boxWidth / 2, boxHeight/2, normalFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
        this.drawBaseLine("log", "Nm", g2);

    }


}
