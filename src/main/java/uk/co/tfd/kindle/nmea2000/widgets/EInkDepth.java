package uk.co.tfd.kindle.nmea2000.widgets;

import uk.co.tfd.kindle.nmea2000.Data;
import uk.co.tfd.kindle.nmea2000.DisplayConversion;
import uk.co.tfd.kindle.nmea2000.Store;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public class EInkDepth extends EInkTextBox {

    public EInkDepth(boolean rotate, Map<String, Object> options, DisplayConversion.DisplayUnits displayUnits, Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        ((Map<String, String> )options.get("labels")).put("br", "m");
        options.put("withStats", true);
        options.put("dataFormat", new DecimalFormat("#0.00"));
        return options;
    }
    @Override
    public void onUpdate(Data.DataValue d) {
        if (d.isType(Data.DataType.DEPTH) ) {
            super.onUpdate(d);
        }
    }

}