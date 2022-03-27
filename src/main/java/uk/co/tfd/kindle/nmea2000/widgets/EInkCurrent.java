package uk.co.tfd.kindle.nmea2000.widgets;

import uk.co.tfd.kindle.nmea2000.Data;
import uk.co.tfd.kindle.nmea2000.DisplayConversion;
import uk.co.tfd.kindle.nmea2000.Store;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.co.tfd.kindle.nmea2000.Data.DataValue;

/**
 * Created by ieb on 10/06/2020.
 */
public class EInkCurrent extends EInkTextBox {


    private String drift = "-";
    private String set = "-";
    private DecimalFormat driftFormat = new DecimalFormat("0.0 Kn");
    private DecimalFormat setFormat = new DecimalFormat("0.0 \u00B0T");

    public EInkCurrent(boolean rotate, Map<String, Object> options, DisplayConversion.DisplayUnits displayUnits, Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        Map<String, String> labels = new HashMap<>();
        labels.put("bl","Current");
        options.put("labels",labels);
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        if (data instanceof Data.NMEA2KCurrent) {
            Data.NMEA2KCurrent pilotData = (Data.NMEA2KCurrent) data;
            String newDrift = this.displayUnits.toDispay(pilotData.getDrift(), driftFormat, Data.DataType.SPEED);
            String newSet = this.displayUnits.toDispay(pilotData.getSet(), setFormat, Data.DataType.BEARING);
            if (!newDrift.equals(drift) || !newSet.equals(set)) {
                drift = newDrift;
                set = newSet;
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("Wrong DataValue Key got "+data.getClass()+" expected "+ Data.NMEA2KCurrent.class.toString());
        }
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        this.twoLineLeft(drift, set, g2);
    }


}
