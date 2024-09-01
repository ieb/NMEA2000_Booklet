package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.SeaSmartHandler;
import uk.co.tfd.kindle.nmea2000.can.CanMessageProducer;
import uk.co.tfd.kindle.nmea2000.can.IsoMessageHandler;

import java.awt.event.ComponentListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public class CanInstruments {
    private static final Logger log = LoggerFactory.getLogger(CanInstruments.class);
    private final CanInstrument<BaseCanWidget> blank;
    Map<String, CanInstrument> map = new HashMap<String, CanInstrument>();
    private WidgetComponentLstener visibilityListener;




    public static class CanInstrument<T extends BaseCanWidget> {
        private final Class<T> widget;
        private final Constructor<T> constructor;


        public CanInstrument(Class<T> widget) throws NoSuchMethodException {
            this.widget = widget;
            this.constructor = this.widget.getConstructor(boolean.class);

        }
        public T create(boolean rotation) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return constructor.newInstance(rotation);
        }
    }


    public CanInstruments(CanMessageProducer canMessageProducer) throws NoSuchMethodException {
        this.visibilityListener = new WidgetComponentLstener(canMessageProducer);
        map.put("rpm", new CanInstrument(EngineView.EngineRpm.class));
        map.put("engineHours", new CanInstrument(EngineView.EngineHours.class));
        map.put("alternatorTemp", new CanInstrument(EngineView.AlternatorTemperature.class));
        map.put("fuelLevel", new CanInstrument(EngineView.FuelLevel.class));
        map.put("coolantTemp", new CanInstrument(EngineView.CoolantTemperature.class));
        map.put("exhaustTemp", new CanInstrument(EngineView.ExhaustTemperature.class));
        map.put("engineRoomTemp", new CanInstrument(EngineView.EngineRoomTemperature.class));

        map.put("stw", new CanInstrument(NavView.Speed.class));
        map.put("boatSet", new CanInstrument(NavView.BoatSet.class));
        map.put("cog", new CanInstrument(NavView.COG.class));
        map.put("sog", new CanInstrument(NavView.SOG.class));
        map.put("aws", new CanInstrument(NavView.ApparentWindSpeed.class));
        map.put("awa", new CanInstrument(NavView.ApparentWindAngle.class));
        map.put("tws", new CanInstrument(NavView.TrueWindSpeed.class));
        map.put("twa", new CanInstrument(NavView.TrueWindAngle.class));
        map.put("xte", new CanInstrument(NavView.CrossTrackError.class));
        map.put("dpt", new CanInstrument(NavView.Depth.class));
        map.put("log", new CanInstrument(NavView.DistanceLog.class));
        map.put("hdm", new CanInstrument(NavView.HeadingMagnetic.class));
        map.put("roll", new CanInstrument(NavView.Roll.class));
        map.put("rudderAngle", new CanInstrument(NavView.Rudder.class));
        map.put("boatDrift", new CanInstrument(NavView.BoatDrift.class));
        map.put("position", new CanInstrument(NavView.Position.class));

        map.put("status", new CanInstrument(SystemView.CanBusStatus.class));

        blank = new CanInstrument<>(BaseCanWidget.class);
        map.put("blank",blank);

    }

    public BaseCanWidget create(String key, boolean rotation) {
        CanInstrument i = map.get(key);
        if ( i != null ) {
            try {
                BaseCanWidget w =  i.create(rotation);
                log.info("Loading card key:{} Widget:{}",key, w.getClass());
                w.addAncestorListener(visibilityListener);
                return w;
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                log.error(e.getMessage(), e);
            } catch (InstantiationException e) {
                log.error(e.getMessage(), e);
            }
        }
        try {
            log.info("Card Not found, using blank key:{}",key);
            return blank.create(rotation);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public void addConfiguration(Map<String, Object> configuration) {
        // any custom instruments here.
        if (configuration.containsKey("instruments")) {
            Map<String, Map<String, Object>> instruments = (Map<String, Map<String, Object>>) configuration.get("instruments");
            for(Map.Entry<String, Map<String, Object>> e :  instruments.entrySet() ) {
                Map<String, Object> instrument = e.getValue();
                try {
                    Class<? extends BaseCanWidget> widgetClass = (Class<? extends BaseCanWidget>) Class.forName("uk.co.tfd.kindle.nmea2000.canwidgets." + instrument.get("widget"));
                    CanInstrument i = new CanInstrument(widgetClass);
                    map.put(e.getKey(), i);
                    log.info("Added instrument {} {} ",e.getKey(), i);
                } catch (Exception ex) {
                    log.error("Failed to add {} {} ",e.getKey(), ex.getMessage());
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }


}
