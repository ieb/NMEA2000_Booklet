package uk.co.tfd.kindle.nmea2000.canwidgets;

import uk.co.tfd.kindle.nmea2000.can.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class NavView {
    public static class Rudder extends BaseCanWidget {
        private long lastUpdate = 0;

        public Rudder(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN127245Rudder.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Rudder");
            labels.put("br", "deg");
            options.put("labels", labels);
            options.put("scale", CanMessageData.scaleToDegrees);
            DecimalFormat df = new DecimalFormat("#0");
            df.setPositivePrefix("S");
            df.setNegativePrefix("P");
            options.put("dataFormat", df);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN127245Rudder) {
                NavMessageHandler.PGN127245Rudder rudder = (NavMessageHandler.PGN127245Rudder) message;
                if (rudder.rudderPosition != CanMessageData.n2kDoubleNA){
                    newOut = displayFormat(rudder.rudderPosition);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class HeadingMagnetic extends BaseCanWidget {
        private long lastUpdate = 0;

        public HeadingMagnetic(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN127250Heading.PGN};
        }

        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Heading ");
            labels.put("br", "deg mag");
            options.put("labels", labels);
            options.put("scale", CanMessageData.scaleToDegrees);
            options.put("dataFormat", new DecimalFormat("#0"));
            options.put("withStats", false);
            return options;
        }

        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if (message instanceof NavMessageHandler.PGN127250Heading) {
                NavMessageHandler.PGN127250Heading heading = (NavMessageHandler.PGN127250Heading) message;
                if (heading.ref == N2KReference.HeadingReference.Magnetic && heading.heading != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(heading.heading);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class Speed extends BaseCanWidget {
        private long lastUpdate = 0;

        public Speed(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN128259Speed.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Speed ");
            labels.put("br", "kn");
            options.put("labels", labels);
            options.put("scale", CanMessageData.scaleToKnots);
            options.put("dataFormat", new DecimalFormat("#0.00"));
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN128259Speed) {
                NavMessageHandler.PGN128259Speed speed = (NavMessageHandler.PGN128259Speed) message;
                if (speed.waterReferenced != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(speed.waterReferenced);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class Depth extends BaseCanWidget {
        private long lastUpdate = 0;

        public Depth(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN128267WaterDepth.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Depth ");
            labels.put("br", "m");
            options.put("labels", labels);
            options.put("dataFormat", new DecimalFormat("#0.0"));
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN128267WaterDepth) {
                NavMessageHandler.PGN128267WaterDepth depth = (NavMessageHandler.PGN128267WaterDepth) message;
                if (depth.depthBelowTransducer != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(depth.depthBelowTransducer);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class Roll extends BaseCanWidget {
        private long lastUpdate = 0;

        public Roll(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN127257Attitude.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Roll");
            labels.put("br", "deg");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0");
            df.setPositivePrefix("S");
            df.setNegativePrefix("P");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToDegrees);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN127257Attitude) {
                NavMessageHandler.PGN127257Attitude attitude = (NavMessageHandler.PGN127257Attitude) message;
                if (attitude.roll != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(attitude.roll);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class CrossTrackError extends BaseCanWidget {
        private long lastUpdate = 0;

        public CrossTrackError(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN129283CrossTrackError.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "XTE");
            labels.put("br", "Nm");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToNm);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN129283CrossTrackError) {
                NavMessageHandler.PGN129283CrossTrackError xte = (NavMessageHandler.PGN129283CrossTrackError) message;
                if (xte.xte != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(xte.xte);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class DistanceLog extends BaseCanWidget {
        private long lastUpdate = 0;

        public DistanceLog(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN128275DistanceLog.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Log");
            labels.put("br", "Nm");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
            options.put("dataFormat", df);
            options.put("scale",CanMessageData.scaleToNm);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN128275DistanceLog) {
                NavMessageHandler.PGN128275DistanceLog log = (NavMessageHandler.PGN128275DistanceLog) message;
                if (log.log != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(log.log);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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


    public static class SOG extends BaseCanWidget {
        private long lastUpdate = 0;

        public SOG(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN129026COGSOGRapid.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "SOG");
            labels.put("br", "Kn");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToKnots);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN129026COGSOGRapid) {
                NavMessageHandler.PGN129026COGSOGRapid sog = (NavMessageHandler.PGN129026COGSOGRapid) message;
                if (sog.sog != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(sog.sog);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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


    public static class COG extends BaseCanWidget {
        private long lastUpdate = 0;

        public COG(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN129026COGSOGRapid.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "COG");
            labels.put("br", "deg");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToDegrees);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN129026COGSOGRapid) {
                NavMessageHandler.PGN129026COGSOGRapid cog = (NavMessageHandler.PGN129026COGSOGRapid) message;
                if (cog.cog != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(cog.cog);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class ApparentWindAngle extends BaseCanWidget {
        private long lastUpdate = 0;

        public ApparentWindAngle(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN130306Wind.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "AWA");
            labels.put("br", "deg");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0");
            df.setNegativePrefix("P");
            df.setPositivePrefix("S");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToDegrees);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN130306Wind) {
                NavMessageHandler.PGN130306Wind wind = (NavMessageHandler.PGN130306Wind) message;
                if ( wind.windReference == N2KReference.WindReference.Apparent && wind.windAngle != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(wind.windAngle);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class ApparentWindSpeed extends BaseCanWidget {
        private long lastUpdate = 0;

        public ApparentWindSpeed(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN130306Wind.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "AWS");
            labels.put("br", "kn");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToKnots);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN130306Wind) {
                NavMessageHandler.PGN130306Wind wind = (NavMessageHandler.PGN130306Wind) message;
                if ( wind.windReference == N2KReference.WindReference.Apparent && wind.windSpeed != CanMessageData.n2kDoubleNA  ) {
                    newOut = displayFormat(wind.windSpeed);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class TrueWindAngle extends BaseCanWidget {
        private long lastUpdate = 0;

        public TrueWindAngle(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = WindCalculator.PGN130306Wind.getSourcePGNS();
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "TWA");
            labels.put("br", "deg");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0");
            df.setNegativePrefix("P");
            df.setPositivePrefix("S");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToDegrees);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof WindCalculator.PGN130306Wind) {
                WindCalculator.PGN130306Wind wind = (WindCalculator.PGN130306Wind) message;
                if ( wind.windReference == N2KReference.WindReference.TrueBoat && wind.windAngle != CanMessageData.n2kDoubleNA ) {
                    newOut = displayFormat(wind.windAngle);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class TrueWindSpeed extends BaseCanWidget {
        private long lastUpdate = 0;

        public TrueWindSpeed(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = WindCalculator.PGN130306Wind.getSourcePGNS();

        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "TWS");
            labels.put("br", "kn");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToKnots);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof WindCalculator.PGN130306Wind) {
                WindCalculator.PGN130306Wind wind = (WindCalculator.PGN130306Wind) message;
                if ( wind.windReference == N2KReference.WindReference.TrueBoat && wind.windSpeed != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(wind.windSpeed);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class Position extends BaseCanWidget {
        private DecimalFormat formatDegLat = new DecimalFormat("00");
        private DecimalFormat formatDegLong = new DecimalFormat("000");
        private DecimalFormat formatMin = new DecimalFormat("00.000");
        private long lastUpdate = 0;

        public Position(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[]{NavMessageHandler.PGN129025RapidPosition.PGN};
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Position");
            labels.put("br", "kn");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
            options.put("withStats", false);
            return options;
        }

        public String formatLatitude(double latitude) {
            if ( latitude == CanMessageData.n2kDoubleNA) {
                return "--\u00B0 --.---\u2032 N";
            }
            String NS = "N";
            if ( latitude < 0) {
                latitude = -latitude;
                NS = "S";
            }
            double d = Math.floor(latitude);
            double m = (60.0*(latitude-d));
            return formatDegLat.format(d) + "\u00B0 " + formatMin.format(m) + "\u2032 " + NS;
        }


        public String formatLongitude(double longitude) {
            if ( longitude == CanMessageData.n2kDoubleNA) {
                return "---\u00B0 --.---\u2032 E";
            }
            String EW = "E";
            if (longitude < 0) {
                longitude = -longitude;
                EW = "W";
            }
            double d = Math.floor(longitude);
            double m = (60.0*(longitude-d));
            return formatDegLong.format(d) + "\u00B0 " + formatMin.format(m) + "\u2032 " + EW;
        }


        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN129025RapidPosition) {
                NavMessageHandler.PGN129025RapidPosition position = (NavMessageHandler.PGN129025RapidPosition) message;
                if ( position.longitude != CanMessageData.n2kDoubleNA && position.longitude != CanMessageData.n2kDoubleNA ) {
                    newOut = formatLatitude(position.latitude) + "\n" + formatLongitude(position.longitude);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
                    newOut = "--\u00B0 --.---\u2032 N\n---\u00B0 --.---\u2032 E";
                }
            }
            if (!newOut.equals(this.out)) {
                this.out = newOut;
                return true;
            }
            return false;
        }

    }

    public static class BoatSet extends BaseCanWidget {
        private long lastUpdate = 0;

        public BoatSet(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[] {NavMessageHandler.PGN130577DirectionData.PGN};

        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Set");
            labels.put("br", "deg");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToDegrees);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN130577DirectionData) {
                NavMessageHandler.PGN130577DirectionData direction = (NavMessageHandler.PGN130577DirectionData) message;
                if (direction.set != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(direction.set);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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

    public static class BoatDrift extends BaseCanWidget {
        private long lastUpdate = 0;

        public BoatDrift(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = new int[] {NavMessageHandler.PGN130577DirectionData.PGN};

        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "Drift");
            labels.put("br", "Kn");
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
            options.put("dataFormat", df);
            options.put("scale", CanMessageData.scaleToKnots);
            options.put("withStats", false);
            return options;
        }
        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN130577DirectionData) {
                NavMessageHandler.PGN130577DirectionData direction = (NavMessageHandler.PGN130577DirectionData) message;
                if (direction.drift != CanMessageData.n2kDoubleNA) {
                    newOut = displayFormat(direction.drift);
                    lastUpdate = System.currentTimeMillis();
                }
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
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
