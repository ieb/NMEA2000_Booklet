package uk.co.tfd.kindle.nmea2000.widgets;

import org.slf4j.LoggerFactory;
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
public class EInkBMP280 extends EInkTextBox {


    private DecimalFormat dilutionFormat = new DecimalFormat("#0.0");
    private String pressure;
    private String humidity;
    private String temperature;

    public EInkBMP280(boolean rotate, Map<String, Object> options, DisplayConversion.DisplayUnits displayUnits, Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        options.put("dataFormat", new DecimalFormat("#0.0"));
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {

        if ( data instanceof Data.BMP280) {
            Data.BMP280 bmp280 = (Data.BMP280) data;
            String newPressure = this.displayUnits.toDispay(bmp280.getPressure(), dataFormat, Data.DataType.ATMOSPHERICPRESSURE); ;
            String newHumidity = this.displayUnits.toDispay(bmp280.getHumidity(), dataFormat, Data.DataType.HUMIDITY);
            String newTemperature = this.displayUnits.toDispay(bmp280.getTemp(), dataFormat, Data.DataType.TEMPERATURE);
            if ( !newPressure.equals(pressure) ||
                    !newHumidity.equals(humidity) ||
                    !newTemperature.equals(temperature)
                    ) {
                pressure = newPressure;
                humidity =  newHumidity;
                temperature = newTemperature;
                return true;
            }
            return false;
        } else {
            throw new IllegalArgumentException("Wrong DataTyoe Key got "+data.getClass()+" expected "+Data.BMP280.class.toString() );
        }
    }

    @Override
    void renderInstrument(Graphics2D g2) {

        Util.drawString(pressure+" mbar", borderPadding, (int)(mediumLineSpace*0.1), mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString(temperature+" C", borderPadding, (int)(0.75*mediumLineSpace+mediumLineSpace*0.1), mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString(humidity + " %rh", borderPadding, (int) (2 * 0.75 * mediumLineSpace + mediumLineSpace * 0.1), mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
    }


}
