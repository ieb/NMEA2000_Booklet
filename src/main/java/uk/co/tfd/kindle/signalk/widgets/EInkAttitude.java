package uk.co.tfd.kindle.signalk.widgets;


import uk.co.tfd.kindle.signalk.Data;

import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static uk.co.tfd.kindle.signalk.Data.AttitudeDataValue;
import static uk.co.tfd.kindle.signalk.Data.DataValue;

/**
 * Created by ieb on 10/06/2020.
 */
public class EInkAttitude extends EInkTextBox {


    private String pitch = "-";
    private String roll = "-";

    public EInkAttitude(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits) {
        super(rotate, updateOptions(options), displayUnits );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        Map<String, String> labels = new HashMap<>();
        labels.put("bl","attitude");
        labels.put("br","deg");
        options.put("labels",labels);
        options.put("dataType", Data.DataType.RELATIVEANGLE);
        options.put("dataFormat", new DecimalFormat("0.0 \u00B0"));
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {
        if (Data.DataKey.NAVIGATION_ATTITUDE.equals(data.key)) {
            if ( data instanceof AttitudeDataValue) {
                AttitudeDataValue attitudeData = (AttitudeDataValue) data;
                String newPitch = "pitch: " + this.displayUnits.toDispay(attitudeData.getPitch(), dataFormat, dataType);
                String newRoll =  "roll:" + this.displayUnits.toDispay(attitudeData.getRoll(), dataFormat, dataType);
                if ( !newPitch.equals(pitch) || !newRoll.equals(roll)) {
                    pitch = newPitch;
                    roll = newRoll;
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException("Wrong DataValue Key got "+data.key+" expected "+Data.DataKey.NAVIGATION_ATTITUDE );
        }
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        this.twoLineLeft(pitch, roll, g2);
    }


}