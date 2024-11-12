package uk.co.tfd.kindle.nmea2000.canwidgets;

import uk.co.tfd.kindle.nmea2000.can.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class EngineView {
    public static class EngineRpm extends BaseCanWidget {

        private long lastUpdate = 0;

        public EngineRpm(boolean rotate) {

            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127488RapidEngineData.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Engine");
            labels.put("br", "RPM");
            options.put("labels", labels);
            options.put("withStats", false);
            options.put("dataFormat", new DecimalFormat("#0"));
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if (message instanceof EngineMessageHandler.PGN127488RapidEngineData) {
                EngineMessageHandler.PGN127488RapidEngineData engine = (EngineMessageHandler.PGN127488RapidEngineData) message;
                if (engine.engineSpeed != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(engine.engineSpeed);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }
    }
    public static class FuelLevel extends BaseCanWidget {


        private long lastUpdate = 0;

        public FuelLevel(boolean rotate) {

            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127505FluidLevel.PGN};

        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Fuel");
            labels.put("br", "%");
            options.put("labels", labels);
            options.put("withStats", false);
            options.put("scale", 0.01); // because the % in the format multiplies by 100.
            options.put("dataFormat", new DecimalFormat("#0 %"));
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof EngineMessageHandler.PGN127505FluidLevel) {
                EngineMessageHandler.PGN127505FluidLevel tank = (EngineMessageHandler.PGN127505FluidLevel) message;
                if ( tank.fluidType == N2KReference.TankType.Fuel && tank.fluidLevel != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(tank.fluidLevel);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }
    }

    public static class CoolantTemperature extends BaseCanWidget {


        private long lastUpdate = 0;

        public CoolantTemperature(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127489EngineDynamicParam.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Coolant");
            labels.put("br", "C");
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("offset", CanMessageData.offsetCelcius);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof EngineMessageHandler.PGN127489EngineDynamicParam) {
                EngineMessageHandler.PGN127489EngineDynamicParam engine = (EngineMessageHandler.PGN127489EngineDynamicParam) message;
                if ( engine.engineCoolantTemperature != CanMessageData.n2kDoubleNA ) {
                    newOut = displayFormat(engine.engineCoolantTemperature);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
                if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }

    }

    public static class AlternatorTemperature extends BaseCanWidget {


        private long lastUpdate = 0;

        public AlternatorTemperature(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127489EngineDynamicParam.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Alternator");
            labels.put("br", "C");
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("offset", CanMessageData.offsetCelcius);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof EngineMessageHandler.PGN127489EngineDynamicParam) {
                EngineMessageHandler.PGN127489EngineDynamicParam engine = (EngineMessageHandler.PGN127489EngineDynamicParam) message;
                if ( engine.engineOilTemperature != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(engine.engineOilTemperature);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }
    }

    public static class ExhaustTemperature extends BaseCanWidget {


        private long lastUpdate = 0;

        public ExhaustTemperature(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN130312Temperature.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Exhaust");
            labels.put("br", "C");
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("offset", CanMessageData.offsetCelcius);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof EngineMessageHandler.PGN130312Temperature) {
                EngineMessageHandler.PGN130312Temperature temperature = (EngineMessageHandler.PGN130312Temperature) message;
                if ( temperature.source == N2KReference.TemperatureSource.ExhaustGasTemperature
                 && temperature.actualTemperature != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(temperature.actualTemperature);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }
    }

    public static class EngineRoomTemperature extends BaseCanWidget {


        private long lastUpdate = 0;

        public EngineRoomTemperature(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN130312Temperature.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Exhaust");
            labels.put("br", "C");
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("offset", CanMessageData.offsetCelcius);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof EngineMessageHandler.PGN130312Temperature) {
                EngineMessageHandler.PGN130312Temperature temperature = (EngineMessageHandler.PGN130312Temperature) message;
                if ( temperature.source == N2KReference.TemperatureSource.EngineRoomTemperature
                    && temperature.actualTemperature != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(temperature.actualTemperature);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }
    }

    public static class EngineHours extends BaseCanWidget {


        private long lastUpdate = 0;

        public EngineHours(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{EngineMessageHandler.PGN127489EngineDynamicParam.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Hours");
            labels.put("br", "h");
            options.put("dataFormat", new DecimalFormat("#0.0"));
            options.put("labels", labels);
            options.put("scale", CanMessageData.scaleSecondsToHours);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof EngineMessageHandler.PGN127489EngineDynamicParam) {
                EngineMessageHandler.PGN127489EngineDynamicParam engine = (EngineMessageHandler.PGN127489EngineDynamicParam) message;
                if ( engine.engineHours != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(engine.engineHours);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }
    }


    public static class EngineBatteryVoltage extends BaseCanWidget {


        private long lastUpdate = 0;

        public EngineBatteryVoltage(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{ElectricalMessageHandler.PGN127508DCBatteryStatus.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Engine Battery");
            labels.put("br", "V");
            options.put("dataFormat", new DecimalFormat("#0.00"));
            options.put("labels", labels);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof ElectricalMessageHandler.PGN127508DCBatteryStatus) {
                ElectricalMessageHandler.PGN127508DCBatteryStatus dc = (ElectricalMessageHandler.PGN127508DCBatteryStatus) message;
                if ( dc.instance == 0 && dc.batteryVoltage != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(dc.batteryVoltage);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }
    }

    public static class ServiceBatteryVoltage extends BaseCanWidget {


        private long lastUpdate = 0;

        public ServiceBatteryVoltage(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{ElectricalMessageHandler.PGN127508DCBatteryStatus.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Service Battery");
            labels.put("br", "V");
            options.put("dataFormat", new DecimalFormat("#0.00"));
            options.put("labels", labels);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof ElectricalMessageHandler.PGN127508DCBatteryStatus) {
                ElectricalMessageHandler.PGN127508DCBatteryStatus dc = (ElectricalMessageHandler.PGN127508DCBatteryStatus) message;
                if ( dc.instance == 1 && dc.batteryVoltage != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(dc.batteryVoltage);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus) {
                if (System.currentTimeMillis() - lastUpdate > 30000) {
                    newOut = "-.-";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }
    }

}