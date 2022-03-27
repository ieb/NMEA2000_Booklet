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
public class EInkFix extends EInkTextBox {


    private String methodQuality;
    private String horizontalDilution;
    private String type;
    private String satellites;
    private String integrity;
    private DecimalFormat dilutionFormat = new DecimalFormat("0.##");

    public EInkFix(boolean rotate, Map<String, Object> options, DisplayConversion.DisplayUnits displayUnits, Store store) {
        super(rotate, updateOptions(options), displayUnits, store );
    }

    private static Map<String, Object> updateOptions(Map<String, Object> options) {
        List<String> sources = new ArrayList<String>();
        return options;
    }


    @Override
    boolean formatOutput(DataValue data) {


        LoggerFactory.getLogger(this.getClass()).info("Update {}  ", data);

        if ( data instanceof Data.NMEA2KGnss) {
    /*
    navigation.gnss.methodQuality (text) fix
    navigation.gnss.horizontalDilution (float) fix
    navigation.gnss.type (text) fix
    navigation.gnss.satellites (int) fix
    navigation.gnss.integrity (text) fix
    */
            Data.NMEA2KGnss fixData = (Data.NMEA2KGnss) data;
            String newMethodQuality = fixData.getMethodQuality();
            String newHorizontalDilution =  dilutionFormat.format(fixData.getHorizontalDilution());
            String newType = fixData.getFixType();
            String newSatellites =  String.valueOf(fixData.getSatellites());
            String newIntegrity =  fixData.getIntegrity();
            if ( !newMethodQuality.equals(methodQuality) ||
                    !newHorizontalDilution.equals(horizontalDilution) ||
                    !newType.equals(type) ||
                    !newSatellites.equals(satellites) ||
                    !newIntegrity.equals(integrity)
                    ) {
                methodQuality = newMethodQuality;
                horizontalDilution =  newHorizontalDilution;
                type = newType;
                satellites = newSatellites;
                integrity = newIntegrity;
                return true;
            }
            return false;
        } else {
            throw new IllegalArgumentException("Wrong DataTyoe Key got "+data.getClass()+" expected "+Data.NMEA2KGnss.class.toString() );
        }
    }

    @Override
    void renderInstrument(Graphics2D g2) {
        LoggerFactory.getLogger(this.getClass()).info("Render Fix {} {} {} {} {} ", new Object[] {methodQuality, type, satellites, horizontalDilution, integrity});
        Util.drawString(methodQuality, borderPadding, (int)(mediumLineSpace*0.1), mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString(type, borderPadding, (int)(0.75*mediumLineSpace+mediumLineSpace*0.1), mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString("sat:"+satellites, borderPadding, (int)(2*0.75*mediumLineSpace+mediumLineSpace*0.1), mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString("hdop:" + horizontalDilution, borderPadding,(int)(3*0.75*mediumLineSpace+mediumLineSpace*0.1), mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        this.drawBaseLine("fix", integrity, g2);
    }


}
