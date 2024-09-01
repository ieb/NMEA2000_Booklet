package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.CanMessage;
import uk.co.tfd.kindle.nmea2000.can.EngineMessageHandler;
import uk.co.tfd.kindle.nmea2000.can.IsoMessageHandler;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SystemView {
        public static class CanBusStatus extends BaseCanWidget {

            private static final Logger log = LoggerFactory.getLogger(uk.co.tfd.kindle.nmea2000.canwidgets.EngineView.EngineRpm.class);

            public CanBusStatus(boolean rotate) {

                super(rotate, updateMap(new HashMap<>()));
                pgns = new int[]{IsoMessageHandler.CanBusStatus.PGN};
            }
            public static Map<String, Object> updateMap(Map<String, Object> options) {
                Map<String, String> labels = new HashMap<>();
                labels.put("bl", "CanBus");
                options.put("labels", labels);
                options.put("withStats", false);
                return options;
            }
            @Override
            public boolean needsUpdate(CanMessage message) {
                if ( message instanceof IsoMessageHandler.CanBusStatus) {
                    IsoMessageHandler.CanBusStatus status = (IsoMessageHandler.CanBusStatus) message;
                    String out = String.format("Recieved %d", status.recieved.get());
                    if ( !out.equals(this.out)) {
                        this.out = out;
                        return true;
                    }
                }
                return false;
            }

        }

    }
