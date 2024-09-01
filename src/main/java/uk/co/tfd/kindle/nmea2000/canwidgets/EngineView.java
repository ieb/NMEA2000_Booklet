package uk.co.tfd.kindle.nmea2000.canwidgets;

import uk.co.tfd.kindle.nmea2000.can.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class EngineView {
    public static class EngineRpm extends BaseCanWidget {

        public EngineRpm(boolean rotate) {

            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127488RapidEngineData.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Engine");
            labels.put("br", "RPM");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN127488RapidEngineData.PGN));
            options.put("labels", labels);
            options.put("withStats", false);
            options.put("dataFormat", new DecimalFormat("#0"));
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN127488RapidEngineData) {
                EngineMessageHandler.PGN127488RapidEngineData engine = (EngineMessageHandler.PGN127488RapidEngineData) message;
                String out = displayFormat(engine.engineSpeed);
                if ( !out.equals(this.out)) {
                    this.out = out;
                    return true;
                }
            }
            return false;
        }

    }
    public static class FuelLevel extends BaseCanWidget {


        public FuelLevel(boolean rotate) {

            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127505FluidLevel.PGN};

        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Fuel");
            labels.put("br", "%");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN127505FluidLevel.PGN));
            options.put("labels", labels);
            options.put("withStats", false);
            options.put("scale", 0.01); // because the % in the format multiplies by 100.
            options.put("dataFormat", new DecimalFormat("#0 %"));
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN127505FluidLevel) {
                EngineMessageHandler.PGN127505FluidLevel tank = (EngineMessageHandler.PGN127505FluidLevel) message;
                if ( tank.fluidType == N2KReference.TankType.Fuel) {
                    String out = displayFormat(tank.fluidLevel);
                    if ( !out.equals(this.out)) {
                        this.out = out;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class CoolantTemperature extends BaseCanWidget {


        public CoolantTemperature(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127489EngineDynamicParam.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Coolant");
            labels.put("br", "C");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN127489EngineDynamicParam.PGN));
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("offset", CanMessageData.offsetCelcius);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN127489EngineDynamicParam) {
                EngineMessageHandler.PGN127489EngineDynamicParam engine = (EngineMessageHandler.PGN127489EngineDynamicParam) message;
                String out = displayFormat(engine.engineCoolantTemperature);
                if ( !out.equals(this.out)) {
                    this.out = out;
                    return true;
                }
            }
            return false;
        }

    }

    public static class AlternatorTemperature extends BaseCanWidget {


        public AlternatorTemperature(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127489EngineDynamicParam.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Alternator");
            labels.put("br", "C");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN127489EngineDynamicParam.PGN));
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("offset", CanMessageData.offsetCelcius);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN127489EngineDynamicParam) {
                EngineMessageHandler.PGN127489EngineDynamicParam engine = (EngineMessageHandler.PGN127489EngineDynamicParam) message;
                String out = displayFormat(engine.engineOilTemperature);
                if ( !out.equals(this.out)) {
                    this.out = out;
                    return true;
                }
            }
            return false;
        }
    }

    public static class ExhaustTemperature extends BaseCanWidget {


        public ExhaustTemperature(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN130312Temperature.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Exhaust");
            labels.put("br", "C");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN130312Temperature.PGN));
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("offset", CanMessageData.offsetCelcius);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN130312Temperature) {
                EngineMessageHandler.PGN130312Temperature temperature = (EngineMessageHandler.PGN130312Temperature) message;
                if ( temperature.source == N2KReference.TemperatureSource.ExhaustGasTemperature ) {
                    String out = displayFormat(temperature.actualTemperature);
                    if ( !out.equals(this.out)) {
                        this.out = out;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class EngineRoomTemperature extends BaseCanWidget {


        public EngineRoomTemperature(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN130312Temperature.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Exhaust");
            labels.put("br", "C");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN130312Temperature.PGN));
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("offset", CanMessageData.offsetCelcius);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN130312Temperature) {
                EngineMessageHandler.PGN130312Temperature temperature = (EngineMessageHandler.PGN130312Temperature) message;
                if ( temperature.source == N2KReference.TemperatureSource.EngineRoomTemperature ) {
                    String out = displayFormat(temperature.actualTemperature);
                    if ( !out.equals(this.out)) {
                        this.out = out;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class EngineHours extends BaseCanWidget {


        public EngineHours(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127489EngineDynamicParam.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Hours");
            labels.put("br", "h");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN127489EngineDynamicParam.PGN));
            options.put("dataFormat", new DecimalFormat("#0.0"));
            options.put("labels", labels);
            options.put("scale", CanMessageData.scaleSecondsToHours);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN127489EngineDynamicParam) {
                EngineMessageHandler.PGN127489EngineDynamicParam engine = (EngineMessageHandler.PGN127489EngineDynamicParam) message;
                String out = displayFormat(engine.engineHours);
                if ( !out.equals(this.out)) {
                    this.out = out;
                    return true;
                }
            }
            return false;
        }
    }


    public static class EngineBatteryVoltage extends BaseCanWidget {


        public EngineBatteryVoltage(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127508DCBatteryStatus.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Engine Battery");
            labels.put("br", "V");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN127508DCBatteryStatus.PGN));
            options.put("dataFormat", new DecimalFormat("#0.00"));
            options.put("labels", labels);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN127508DCBatteryStatus) {
                EngineMessageHandler.PGN127508DCBatteryStatus dc = (EngineMessageHandler.PGN127508DCBatteryStatus) message;
                if ( dc.instance == 0) {
                    String out = displayFormat(dc.batteryVoltage);
                    if ( !out.equals(this.out)) {
                        this.out = out;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class ServiceBatteryVoltage extends BaseCanWidget {


        public ServiceBatteryVoltage(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127508DCBatteryStatus.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Service Battery");
            labels.put("br", "V");
            labels.put("tl", String.valueOf(EngineMessageHandler.PGN127508DCBatteryStatus.PGN));
            options.put("dataFormat", new DecimalFormat("#0.00"));
            options.put("labels", labels);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            if ( message instanceof EngineMessageHandler.PGN127508DCBatteryStatus) {
                EngineMessageHandler.PGN127508DCBatteryStatus dc = (EngineMessageHandler.PGN127508DCBatteryStatus) message;
                if ( dc.instance == 1) {
                    String out = displayFormat(dc.batteryVoltage);
                    if ( !out.equals(this.out)) {
                        this.out = out;
                        return true;
                    }
                }
            }
            return false;
        }
    }

}