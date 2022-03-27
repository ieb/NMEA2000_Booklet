package uk.co.tfd.kindle.nmea2000.widgets;

import uk.co.tfd.kindle.nmea2000.Data;
import uk.co.tfd.kindle.nmea2000.DisplayConversion;
import uk.co.tfd.kindle.nmea2000.Store;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by ieb on 15/06/2020.
 */
public class EInkRatio extends EInkTextBox {

    public EInkRatio(boolean rotate, Map<String, Object> options, DisplayConversion.DisplayUnits displayUnits, Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        ((Map<String, String>) options.get("labels")).put("br", "%");
        options.put("withStats", true);
        options.put("dataFormat", new DecimalFormat("#0.0"));
        return options;
    }

    @Override
    public void onUpdate(Data.DataValue d) {
        if (d.isType(Data.DataType.PERCENTAGE) ) {
            super.onUpdate(d);
        }
    }


}