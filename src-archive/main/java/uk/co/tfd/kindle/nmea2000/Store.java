package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ieb on 13/03/2022.
 */
public class Store extends Data.Observable {
    private static final Logger log = LoggerFactory.getLogger(Store.class);
    Map<String, Data.DataValue> state = new HashMap<String, Data.DataValue>();
    Map<String, List<Data.DataValue>> paths = new HashMap<String, List<Data.DataValue>>();
    private Timer timer;

    public Store() {

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calcStats();
            }
        });

    }



    private void loadStore() {

        for (Data.DataKey k : Data.DataKey.values.values()) {
            if (!state.containsKey(k.toString())) {
                if (Data.Unit.RAD.equals(k.units)) {
                    state.put(k.toString(), new Data.CircularDataValue(k, k.toString(), k.toString()));
                } else {
                    state.put(k.toString(), new Data.DoubleDataValue(k, k.toString(), k.toString()));
                }
            }
        }
        this.updateStatus("Store started");
    }



    public <T extends Data.DataValue> T get(Data.DataKey key) {
        if (state.containsKey(key.toString())) {
            return (T) state.get(key.toString());
        } else {
            throw new IllegalArgumentException("DataValue  not found at " + key);
        }
    }

    public Map<String, Object> update(String root, Map<String, Object> update, long timeOffset) {
        Map<String, Object> rejects = new HashMap<String, Object>();
        doUpdate(root, update, rejects, timeOffset);
        fireUpdate();
        return rejects;
    }


    /**
     * Updates the object stored in the store at a path, scanning into the tree until an object is found
     * in the store. scanning the tree scans through arrays and map.
     *
     * @param root
     * @param update
     * @param rejects
     */
    public void doUpdate(String root, Map<String, Object> update, Map<String, Object> rejects,  long timeOffset) {
        for (Map.Entry<String, Object> e : update.entrySet()) {
            String path = root + e.getKey();
            Object o = e.getValue();
            boolean used = false;
            if (paths.containsKey(path)) {
                for (Data.DataValue dv : paths.get(path)) {
                    dv.update(o, timeOffset);
                }
                used = true;
            }
            if (o instanceof Map) {
                doUpdate(path + ".", (Map<String, Object>) o, rejects, timeOffset);

            } else if (o instanceof List) {
                doUpdate(path + ".", (List<Object>) o, rejects, timeOffset);
            } else if (!used ) {
                rejects.put(path, o);
            }
        }
    }

    public void doUpdate(String root, List<Object> update, Map<String, Object> rejects,  long timeOffset) {
        for (int i = 0; i < update.size(); i++) {
            Object o = update.get(i);
            String path = root + i;
            if (o instanceof Map) {
                Map<String, Object> om = (Map<String, Object>) o;
                if (om.containsKey("id")) {
                    path = root + om.get("id");
                }
                if (paths.containsKey(path)) {
                    for (Data.DataValue dv : paths.get(path)) {
                        dv.update(om, timeOffset);
                    }
                }
                doUpdate(path + ".", (Map<String, Object>) o, rejects, timeOffset);
            } else if (o instanceof List) {
                doUpdate(path + ".", (List<Object>) o, rejects, timeOffset);
            } else {
                rejects.put(path, o);
            }
        }
    }


    public void updateFromServer(Map<String, Object> value, long timeOffset) {
        String path = (String) value.get("path");
        if (path != null) {
            if ( paths.containsKey(path)) {
                for(Data.DataValue dv : paths.get(path)) {
                    dv.update(value, timeOffset);
                }
            } else {
                log.warn("Ignoring {} {} ", path, value);
            }
        }
    }

    public void calcStats() {
        for (Map.Entry<String, Data.DataValue> e : state.entrySet()) {
            e.getValue().calcStats();
        }
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }


    public void addConfiguration(Map<String, Object> configuration) {
        if (configuration.containsKey("datavalues")) {
            Map<String, Map<String, Object>> instruments = (Map<String, Map<String, Object>>) configuration.get("datavalues");
            for (Map.Entry<String, Map<String, Object>> e : instruments.entrySet()) {
                try {
                    Map<String, Object> instrument = e.getValue();
                    log.info("Loading {} ", instrument);
                    Data.Unit units = Data.Unit.valueOf((String) instrument.get("unit"));
                    Data.DataType dataType = Data.DataType.valueOf((String) instrument.get("dataType"));
                    String description = (String) instrument.get("description");
                    Data.DataKey k = new Data.DataKey(e.getKey(), units, dataType, description);
                    String dataPath = e.getKey();
                    String sourcePath = e.getKey();
                    if ( instrument.containsKey("path") ) {
                        sourcePath = (String) instrument.get("path");
                    }
                    Class dataClass = Class.forName("uk.co.tfd.kindle.nmea2000.Data$" + (String) instrument.get("dataClass"));
                    Constructor constructor = dataClass.getConstructor(Data.DataKey.class, String.class, String.class);
                    Data.DataValue dv = (Data.DataValue) constructor.newInstance(k, dataPath, sourcePath);



                    state.put(dataPath, dv);
                    if ( !paths.containsKey(sourcePath) ) {
                        paths.put(sourcePath, new ArrayList<Data.DataValue>());
                    }
                    paths.get(sourcePath).add(dv);

                    //
                    if ( instrument.containsKey("keys")) {
                        List<String> keys = (List<String>) instrument.get("keys");
                        for (String dataKeyPath : keys) {
                            state.put(dataKeyPath, dv);
                        }
                    }

                    log.info("Added Data Path {} {} {} {} ", new Object[]{e.getKey(), sourcePath, dv, dataType});
                } catch (Exception ex) {
                    log.error("Unable to create datavalue {} {} ", e.getKey(), ex.getMessage());
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }
}
