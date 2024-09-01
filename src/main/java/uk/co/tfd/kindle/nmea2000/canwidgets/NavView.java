package uk.co.tfd.kindle.nmea2000.canwidgets;

import uk.co.tfd.kindle.nmea2000.can.*;

import java.text.DecimalFormat;
import java.util.Arrays;
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN127245Rudder.PGN));
            options.put("scale", CanMessageData.scaleToDegrees);
            DecimalFormat df = new DecimalFormat("#0.0");
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
                newOut = displayFormat(rudder.rudderPosition);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN127250Heading.PGN));
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
                if (heading.ref == N2KReference.HeadingReference.Magnetic) {
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN128259Speed.PGN));
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
                newOut = displayFormat(speed.waterReferenced);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN128267WaterDepth.PGN));
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
                newOut = displayFormat(depth.depthBelowTransducer);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN127257Attitude.PGN));
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
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
                newOut = displayFormat(attitude.roll);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN129283CrossTrackError.PGN));
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
                newOut = displayFormat(xte.xte);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN128275DistanceLog.PGN));
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
                newOut = displayFormat(log.log);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN129026COGSOGRapid.PGN));
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
                newOut = displayFormat(sog.sog);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN129026COGSOGRapid.PGN));
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
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
                newOut = displayFormat(cog.cog);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN130306Wind.PGN));
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
                if ( wind.windReference == N2KReference.WindReference.Apparent ) {
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN130306Wind.PGN));
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
                if ( wind.windReference == N2KReference.WindReference.Apparent ) {
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

    public static class TrueWindAngle extends BaseCanWidget {
        private long lastUpdate = 0;

        public TrueWindAngle(boolean rotate) {
            super(rotate, updateMap(new HashMap<>()));
            pgns = WindCalculator.getSourcePgns();
        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "TWA");
            labels.put("br", "deg");
            labels.put("tl", Arrays.toString(WindCalculator.getSourcePgns()));
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
                if ( wind.windReference == N2KReference.WindReference.TrueBoat ) {
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
            pgns = WindCalculator.getSourcePgns();

        }
        public static Map<String, Object> updateMap(Map<String, Object> options) {
            Map<String, String> labels = new HashMap<>();
            labels.put("bl", "TWS");
            labels.put("br", "kn");
            labels.put("tl", Arrays.toString(WindCalculator.getSourcePgns()));
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
                if ( wind.windReference == N2KReference.WindReference.TrueBoat ) {
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN129025RapidPosition.PGN));
            options.put("labels", labels);
            DecimalFormat df = new DecimalFormat("#0.0");
            options.put("withStats", false);
            return options;
        }

        public String formatLatitude(double latitude) {
            if ( latitude == CanMessageData.n2kDoubleNA) {
                return "--\u00B0--.---\u2032N";
            }
            String NS = "N";
            if ( latitude < 0) {
                latitude = -latitude;
                NS = "S";
            }
            double d = Math.floor(latitude);
            double m = (60.0*(latitude-d));
            return formatDegLat.format(d) + "\u00B0" + formatMin.format(m) + "\u2032" + NS;
        }


        public String formatLongitude(double longitude) {
            if ( longitude == CanMessageData.n2kDoubleNA) {
                return "---\u00B0--.---\u2032E";
            }
            String EW = "E";
            if (longitude < 0) {
                longitude = -longitude;
                EW = "W";
            }
            double d = Math.floor(longitude);
            double m = (60.0*(longitude-d));
            return formatDegLong.format(d) + "\u00B0" + formatMin.format(m) + "\u2032" + EW;
        }


        @Override
        public boolean needsUpdate(CanMessage message) {
            String newOut = out;
            if ( message instanceof NavMessageHandler.PGN129025RapidPosition) {
                NavMessageHandler.PGN129025RapidPosition position = (NavMessageHandler.PGN129025RapidPosition) message;
                newOut = formatLatitude(position.latitude)+"\n"+formatLongitude(position.longitude);
                lastUpdate = System.currentTimeMillis();
            } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
                if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
                    newOut = "--\u00B0--.---\u2032N\n\"---\u00B0--.---\u2032E";
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN130577DirectionData.PGN));
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
                newOut = displayFormat(direction.set);
                lastUpdate = System.currentTimeMillis();
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
            labels.put("tl", String.valueOf(NavMessageHandler.PGN130577DirectionData.PGN));
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
                newOut = displayFormat(direction.drift);
                lastUpdate = System.currentTimeMillis();
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
