package uk.co.tfd.kindle.nmea2000.can;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class N2KReference {
    private final static Logger log = LoggerFactory.getLogger(N2KReference.class);
    public enum TimeSource  {
        UNDEFINED(-1, "Undefined"),
        GPS(0, "GPS"),
        GLONASS(1, "GLONASS"),
        RadioStation(2, "RadioStation"),
        LocalCesiumClock(3, "LocalCesiumClock"),
        LocalRubidiumClock( 4, "LocalRubidiumClock"),
        LocalCrystalClock( 5, "LocalCrystalClock"),
        None(15, "None");
        
        public final int id;
        public final String name;
        TimeSource(int id, String name) {
            this.id = id;
            this.name = name;
        }
        
        
        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static TimeSource lookup(int value) {
            for(TimeSource e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("Timesource missing value {} ",  value);
            return UNDEFINED;
        }
    }

    public enum RudderDirectionOrder  {
        UNDEFINED(-1, "Undefined"),
        NoDirectionOrder(0, "NoDirectionOrder"),
        MoveToStarboard(1, "MoveToStarboard"),
        MoveToPort(2, "MoveToPort"),
        Unavailable(7, "Unavailable");
        public final int id;
        public final String name;
        RudderDirectionOrder(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static RudderDirectionOrder lookup(int value) {
            for(RudderDirectionOrder e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("RudderDirectionOrder missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum HeadingReference {
        UNDEFINED(-1, "Undefined"),
        TRue(0, "True"),
        Magnetic(1, "Magnetic"),
        Error(2, "Error"),
        Unavailable(3, "Unavailable");
        public final int id;
        public final String name;
        HeadingReference(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static HeadingReference lookup(int value) {
            for(HeadingReference e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("HeadingReference missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum VariationSource {
        UNDEFINED(-1, "Undefined", 0),
        Manual(0, "manual", 1),
        Chart(1, "chart", 2),
        Table(2, "table", 3),
        Calc(3, "calc", 4),
        Wmm2000(4, "wmm2000", 5),
        Wmm2005(5, "wmm2005", 6),
        Wmm2010(6, "wmm2010", 7),
        Wmm2015(7, "wmm2015", 8),
        Wmm2020(8, "wmm2020", 9),
        Unknown_9(9, "unknown_9", 0),
        Unknown_10(10, "unknown_10", 0),
        Unknown_11(11, "unknown_11", 0),
        Unknown_12(12, "unknown_12", 0),
        Unknown_13(13, "unknown_13", 0),
        Unknown_14(14, "unknown_14", 0),
        Unknown_15(15, "unknown_15", 0);
        public final int id;
        public final String name;
        public final int priority;
        VariationSource(int id, String name, int priority) {
            this.id = id;
            this.name = name;
            this.priority = priority;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static VariationSource lookup(int value) {
            for(VariationSource e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("VariationSource missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum SwrtType {
        UNDEFINED(-1, "Undefined"),
        PaddleWheel(0, "Paddle wheel"),
        PitotTube(1, "Pitot tube"),
        DopplerLog(2, "Doppler log"),
        Ultrasound(3, "Ultrasound"),
        ElectroMagnetic(4, "Electro magnetic"),
        Error(254, "Error"),
        Unavailable(255, "Unavailable");
        public final int id;
        public final String name;
        SwrtType(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static SwrtType lookup(int value) {
            for(SwrtType e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("SwrtType missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum GnssMode {
        UNDEFINED(-1, "Undefined"),
        Mode1D(0, "1D"),
        Mode2D(1, "2D"),
        Mode3D(2, "3D"),
        Auto(3, "Auto"),
        Mode4(4, "??"),
        Mode5(5, "??"),
        Mode6(6, "??");
        public final int id;
        public final String name;
        GnssMode(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static GnssMode lookup(int value) {
            for(GnssMode e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("GnssMode missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum GnssType {
        UNDEFINED(-1, "Undefined", 0),
        GPS(0, "GPS", 7),
        GLONASS(1, "GLONASS", 5),
        GPSGLONASS(2, "GPSGLONASS", 6),
        GPSSBASWAAS(3, "GPSSBASWAAS", 9),
        GPSSBASWAASGLONASS(4, "GPSSBASWAASGLONASS", 10),
        Chayka(5, "Chayka", 7),
        Integrated(6, "integrated", 5),
        Surveyed(7, "surveyed", 4),
        Galileo(8, "Galileo", 7),
        Unknown_9(9, "unknown_9", 0),
        Unknown_10(10, "unknown_10", 0),
        Unknown_11(11, "unknown_11", 0),
        Unknown_12(12, "unknown_12", 0),
        Unknown_13(13, "unknown_13", 0),
        Unknown_14(14, "unknown_14", 0),
        Unknown_15(15, "unknown_15", 0);
        public final int id;
        public final String name;
        public final int priority;
        GnssType(int id, String name, int priority) {
            this.id = id;
            this.name = name;
            this.priority = priority;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+" priority:"+priority+")";
        }
        public static GnssType lookup(int value) {
            for(GnssType e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("GnssType missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum GnssMethod {
        UNDEFINED(-1, "Undefined"),
        NoGNSS(0, "noGNSS"),
        GNSSfix(1, "GNSSfix"),
        DGNSS(2, "DGNSS"),
        PreciseGNSS(3, "PreciseGNSS"),
        RTKFixed(4, "RTKFixed"),
        RTKFloat(5, "RTKFloat"),
        Error(14, "Error"),
        Unavailable(15, "Unavailable");
        public final int id;
        public final String name;
        GnssMethod(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static GnssMethod lookup(int value) {
            for(GnssMethod e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("GnssMethod missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum GnssIntegrity {
        UNDEFINED(-1, "Undefined"),
        NoIntegrityChecking(0, "No integrity checking"),
        Safe(1, "Safe"),
        Caution(2, "Caution"),
        Unsafe(3, "Unsafe");
        public final int id;
        public final String name;
        GnssIntegrity(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static GnssIntegrity lookup(int value) {
            for(GnssIntegrity e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("GnssIntegrity missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum ResidualMode {
        UNDEFINED(-1, "Undefined"),
        Autonomous(0, "Autonomous"),
        Differential(1, "Differential"),
        Estimated(2, "Estimated"),
        Simulator(3, "Simulator"),
        Manual(4, "Manual"),
        Unknown_5(5, "unknown_5"),
        Unknown_6(6, "unknown_6"),
        Unknown_7(7, "unknown_7"),
        Unknown_8(8, "unknown_8"),
        Unknown_9(9, "unknown_9"),
        Unknown_10(10, "unknown_10"),
        Unknown_11(11, "unknown_11"),
        Unknown_12(12, "unknown_12"),
        Unknown_13(13, "unknown_13"),
        Unknown_14(14, "unknown_14"),
        Unknown_15(15, "unknown_15");
        public final int id;
        public final String name;
        ResidualMode(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static ResidualMode lookup(int value) {
            for(ResidualMode e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("ResidualMode missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum XteMode {
        UNDEFINED(-1, "Undefined"),
        Autonomous(0, "Autonomous"),
        Differential(1, "Differential"),
        Estimated(2, "Estimated"),
        Simulator(3, "Simulator"),
        Manual(4, "Manual"),
        Unknown_5(5, "unknown_5"),
        Unknown_6(6, "unknown_6"),
        Unknown_7(7, "unknown_7"),
        Unknown_8(8, "unknown_8"),
        Unknown_9(9, "unknown_9"),
        Unknown_10(10, "unknown_10"),
        Unknown_11(11, "unknown_11"),
        Unknown_12(12, "unknown_12"),
        Unknown_13(13, "unknown_13"),
        Unknown_14(14, "unknown_14"),
        Unknown_15(15, "unknown_15");
        public final int id;
        public final String name;
        XteMode(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static XteMode lookup(int value) {
            for(XteMode e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("XteMode missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum YesNo {
        UNDEFINED(-1, "Undefined"),
        No(0, "No"),
        Yes(1, "Yes"),
        Maybee2(2, "??"),
        Maybee3(3, "??");
        public final int id;
        public final String name;
        YesNo(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static YesNo lookup(int value) {
            for(YesNo e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("YesNo missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum WindReference {
        UNDEFINED(-1, "Undefined"),
        TrueGround(0, "True Ground"),
        MagneticGround(1, "Magnetic Ground"),
        Apparent(2, "Apparent"),
        TrueBoat(3, "True Boat"),
        TrueWater(4, "True Water");
        public final int id;
        public final String name;
        WindReference(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static WindReference lookup(int value) {
            for(WindReference e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("WindReference missing value {} ",  value);
            return UNDEFINED;
        }
    }
    public enum TemperatureSource {
        UNDEFINED(-1, "Undefined"),
        SeaTemperature(0, "Sea Temperature"),
        OutsideTemperature(1, "Outside Temperature"),
        InsideTemperature(2, "Inside Temperature"),
        EngineRoomTemperature(3, "Engine Room Temperature"),
        MainCabinTemperature(4, "Main Cabin Temperature"),
        LiveWellTemperature(5, "Live Well Temperature"),
        BaitWellTemperature(6, "Bait Well Temperature"),
        RefrigerationTemperature(7, "Refrigeration Temperature"),
        HeatingSystemTemperature(8, "Heating System Temperature"),
        DewPointTemperature(9, "Dew Point Temperature"),
        ApparentWindChillTemperature(10, "Apparent Wind Chill Temperature"),
        TheoreticalWindChillTemperature(11, "Theoretical Wind Chill Temperature"),
        HeatIndexTemperature(12, "Heat Index Temperature"),
        FreezerTemperature(13, "Freezer Temperature"),
        ExhaustGasTemperature(14, "Exhaust Gas Temperature"),
        ShaftSealTemperature(15, "Shaft Seal Temperature");
        public final int id;
        public final String name;
        TemperatureSource(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static TemperatureSource lookup(int value) {
            for(TemperatureSource e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("TemperatureSource missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum HumiditySource {
        UNDEFINED(-1, "Undefined"),
        Inside(0, "Inside"),
        Outside(1, "Outside");
        public final int id;
        public final String name;
        HumiditySource(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static HumiditySource lookup(int value) {
            for(HumiditySource e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("HumiditySource missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum PressureSource {
        UNDEFINED(-1, "Undefined"),
        Atmospheric(0, "Atmospheric"),
        Water(1, "Water"),
        Steam(2, "Steam"),
        CompressedAir(3, "Compressed Air"),
        Hydraulic(4, "Hydraulic"),
        Filter(5, "Filter"),
        AltimeterSetting(6, "AltimeterSetting"),
        Oil(7, "Oil"),
        Fuel(8, "Fuel");
        public final int id;
        public final String name;
        PressureSource(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static PressureSource lookup(int value) {
            for(PressureSource e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("PressureSource missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum DcSourceType {
        UNDEFINED(-1, "Undefined"),
        Battery(0, "Battery"),
        Alternator(1, "Alternator"),
        Convertor(2, "Convertor"),
        SolarCell(3, "Solar cell"),
        WindGenerator(4, "Wind generator");
        public final int id;
        public final String name;
        DcSourceType(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static DcSourceType lookup(int value) {
            for(DcSourceType e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("DcSourceType missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum BatteryVoltage {
        UNDEFINED(-1, "Undefined"),
        BatteryVoltage6V(0, "6V"),
        BatteryVoltage12V(1, "12V"),
        BatteryVoltage24V(2, "24V"),
        BatteryVoltage32V(3, "32V"),
        BatteryVoltage36(4, "36V"),
        BatteryVoltage42V(5, "42V"),
        BatteryVoltage48V(6, "48V");
        public final int id;
        public final String name;
        BatteryVoltage(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static BatteryVoltage lookup(int value) {
            for(BatteryVoltage e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("BatteryVoltage missing value {} ",  value);
            return UNDEFINED;
        }

    }

    public enum BatteryType {
        UNDEFINED(-1, "Undefined"),
        Flooded(0, "Flooded"),
        Gell(1, "Gell"),
        AGM(2, "AGM"),
        LiFePO4(3, "LiFePO4"); // made this up, not official.
        public final int id;
        public final String name;
        BatteryType(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static BatteryType lookup(int value) {
            for(BatteryType e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("BatteryType missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum BatteryChemistry {
        UNDEFINED(-1, "Undefined"),
        Pb(0, "Pb"),
        Li(1, "Li"),
        NiCd(2, "NiCd"),
        ZnO(3, "ZnO"),
        NiMH(4, "NiMH");
        public final int id;
        public final String name;
        BatteryChemistry(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static BatteryChemistry lookup(int value) {
            for(BatteryChemistry e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("BatteryChemistry missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum SteeringMode {
        UNDEFINED(-1, "Undefined"),
        MainSteering(0, "Main Steering"),
        NonFollowUpDevice(1, "Non-Follow-Up Device"),
        FollowUpDevice(2, "Follow-Up Device"),
        HeadingControlStandalone(3, "Heading Control Standalone"),
        HeadingControl(4, "Heading Control"),
        TrackControl(5, "Track Control"),
        Mode6(6, "??"),
        Mode7(7, "??");
        public final int id;
        public final String name;
        SteeringMode(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static SteeringMode lookup(int value) {
            for(SteeringMode e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("SteeringMode missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum TurnMode {
        UNDEFINED(-1, "Undefined"),
        RudderLimitControlled(0, "Rudder limit controlled"),
        TurnRateControlled(1, "Turn rate controlled"),
        RadiusControlled(2, "Radius controlled"),
        Mode3(3, "??"),
        Mode4(4, "??"),
        Mode5(5, "??"),
        Mode6(6, "??"),
        Mode7(7, "??");
        public final int id;
        public final String name;
        TurnMode(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static TurnMode lookup(int value) {
            for(TurnMode e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("TurnMode missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum DirectionReference {
        UNDEFINED(-1, "Undefined"),
        True(0, "True"),
        Magnetic(1, "Magnetic"),
        Error(2, "Error"),
        Dir3(3, "??");
        public final int id;
        public final String name;
        DirectionReference(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static DirectionReference lookup(int value) {
            for(DirectionReference e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("DirectionReference missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum DirectionRudder {
        UNDEFINED(-1, "Undefined"),
        NoOrder(0, "No Order"),
        Movetostarboard(1, "Move to starboard"),
        Movetoport(2, "Move to port"),
        Direction3(3, "??"),
        Direction4(4, "??"),
        Direction5(5, "??"),
        Direction6(6, "??"),
        Direction7(7, "??");
        public final int id;
        public final String name;
        DirectionRudder(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static DirectionRudder lookup(int value) {
            for(DirectionRudder e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("RudderDirection XX missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum TankType {
        UNDEFINED(-1, "Undefined"),
        Fuel(0, "Fuel"),
        Water(1, "Water"),
        GreyWater(2, "Grey water"),
        LiveWell(3, "Live well"),
        Oil(4, "Oil"),
        BlackWater(5, "Black water");
        public final int id;
        public final String name;
        TankType(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static TankType lookup(int value) {
            for(TankType e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("TankType missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum ManufacturerCode {
        UNDEFINED(-1, "Undefined"),
        UndefinedManufacturer(0, "Undefined Inc."),// a bug in the device or message.
        ARKSEnterprisesInc(69, "ARKS Enterprises, Inc."),
        FWMurphyEnovationControls(78, "FW Murphy/Enovation Controls"),
        TwinDisc(80, "Twin Disc"),
        KohlerPowerSystems(85, "Kohler Power Systems"),
        HemisphereGPSInc(88, "Hemisphere GPS Inc"),
        BEPMarine(116, "BEP Marine"),
        Airmar(135, "Airmar"),
        Maretron(137, "Maretron"),
        Lowrance(140, "Lowrance"),
        MercuryMarine(144, "Mercury Marine"),
        NautibusElectronicGmbH(147, "Nautibus Electronic GmbH"),
        BlueWaterData(148, "Blue Water Data"),
        Westerbeke(154, "Westerbeke"),
        OffshoreSystemsUKLtd(161, "Offshore Systems (UK) Ltd."),
        EvinrudeBRP(163, "Evinrude/BRP"),
        CPACSystemsAB(165, "CPAC Systems AB"),
        XantrexTechnologyInc(168, "Xantrex Technology Inc."),
        YanmarMarine(172, "Yanmar Marine"),
        VolvoPenta(174, "Volvo Penta"),
        HondaMarine(175, "Honda Marine"),
        CarlingTechnologiesIncMoritzAerospace(176, "Carling Technologies Inc. (Moritz Aerospace)"),
        BeedeInstruments(185, "Beede Instruments"),
        FloscanInstrumentCoInc(192, "Floscan Instrument Co. Inc."),
        Nobletec(193, "Nobletec"),
        MysticValleyCommunications(198, "Mystic Valley Communications"),
        Actia(199, "Actia"),
        HondaMarine0(200, "Honda Marine"),
        DisenosYTechnologia(201, "Disenos Y Technologia"),
        DigitalSwitchingSystems(211, "Digital Switching Systems"),
        XintexAtena(215, "Xintex/Atena"),
        EMMINETWORK(224, "EMMI NETWORK S.L."),
        HondaMarine2(225, "Honda Marine"),
        ZF(228, "ZF"),
        Garmin(229, "Garmin"),
        YachtMonitoringSolutions(233, "Yacht Monitoring Solutions"),
        SailormadeMarineTelemetry(235, "Sailormade Marine Telemetry/Tetra Technology LTD"),
        Eride(243, "Eride"),
        HondaMarine3(250, "Honda Marine"),
        HondaMotorCompanyLTD(257, "Honda Motor Company LTD"),
        Groco(272, "Groco"),
        Actisense(273, "Actisense"),
        AmphenolLTWTechnology(274, "Amphenol LTW Technology"),
        Navico(275, "Navico"),
        HamiltonJet(283, "Hamilton Jet"),
        SeaRecovery(285, "Sea Recovery"),
        CoelmoSRLItaly(286, "Coelmo SRL Italy"),
        BEPMarine2(295, "BEP Marine"),
        EmpirBus(304, "Empir Bus"),
        NovAtel(305, "NovAtel"),
        SleipnerMotor(306, "Sleipner Motor AS"),
        MBWTechnologies(307, "MBW Technologies"),
        FischerPanda(311, "Fischer Panda"),
        ICOM(315, "ICOM"),
        Qwerty(328, "Qwerty"),
        Dief(329, "Dief"),
        BöningAutomationstechnologie(341, "Böning Automationstechnologie GmbH & Co. KG"),
        KoreanMaritimeUniversity(345, "Korean Maritime University"),
        ThraneandThrane(351, "Thrane and Thrane"),
        Mastervolt(355, "Mastervolt"),
        FischerPandaGenerators(356, "Fischer Panda Generators"),
        VictronEnergy(358, "Victron Energy"),
        RollsRoyceMarine(370, "Rolls Royce Marine"),
        ElectronicDesign(373, "Electronic Design"),
        NorthernLights(374, "Northern Lights"),
        Glendinning(378, "Glendinning"),
        BandG(381, "B & G"),
        RosePointNavigationSystems(384, "Rose Point Navigation Systems"),
        JohnsonOutdoorsMarine(385, "Johnson Outdoors Marine Electronics Inc Geonav"),
        Capi2(394, "Capi 2"),
        BeyondMeasure(396, "Beyond Measure"),
        LivorsiMarine(400, "Livorsi Marine"),
        ComNav(404, "ComNav"),
        Chetco(409, "Chetco"),
        FusionElectronics(419, "Fusion Electronics"),
        StandardHorizon(421, "Standard Horizon"),
        TrueHeadingAB(422, "True Heading AB"),
        EgersundMarine(426, "Egersund Marine Electronics AS"),
        EmTrakMarineElectronics(427, "em-trak Marine Electronics"),
        TohatsuCoJP(431, "Tohatsu Co, JP"),
        DigitalYacht(437, "Digital Yacht"),
        ComarSystems(438, "Comar Systems Limited"),
        Cummins(440, "Cummins"),
        VDO(443, "VDO (aka Continental-Corporation)"),
        ParkerHannifin(451, "Parker Hannifin aka Village Marine Tech"),
        AlltekMarine(459, "Alltek Marine Electronics Corp"),
        SANGIORGIO(460, "SAN GIORGIO S.E.I.N"),
        VeethreeElectronics(466, "Veethree Electronics & Marine"),
        HumminbirdMarine(467, "Humminbird Marine Electronics"),
        SITEX(470, "SI-TEX Marine Electronics"),
        SeaCross(471, "Sea Cross Marine AB"),
        GME(475, "GME aka Standard Communications Pty LTD"),
        HumminbirdMarine2(476, "Humminbird Marine Electronics"),
        OceanSat(478, "Ocean Sat BV"),
        ChetcoDigitial(481, "Chetco Digitial Instruments"),
        Watcheye(493, "Watcheye"),
        LcjCapteurs(499, "Lcj Capteurs"),
        Attwood(502, "Attwood Marine"),
        Naviop(503, "Naviop S.R.L."),
        VesperMarine(504, "Vesper Marine Ltd"),
        MarinesoftLTD(510, "Marinesoft Co. LTD"),
        NoLandEngineering(517, "NoLand Engineering"),
        TransasUSA(518, "Transas USA"),
        NationalInstrumentsKorea(529, "National Instruments Korea"),
        OnwaMarine(532, "Onwa Marine"),
        Marinecraft(571, "Marinecraft (South Korea)"),
        McMurdo(573, "McMurdo Group aka Orolia LTD"),
        Advansea(578, "Advansea"),
        KVH(579, "KVH"),
        SanJoseTechnology(580, "San Jose Technology"),
        YachtControl(583, "Yacht Control"),
        SuzukiMotorCorporation(586, "Suzuki Motor Corporation"),
        USCoastGuard(591, "US Coast Guard"),
        ShipModule(595, "Ship Module aka Customware"),
        AquaticAV(600, "Aquatic AV"),
        AventicsGmbH(605, "Aventics GmbH"),
        Intellian(606, "Intellian"),
        SamwonIT(612, "SamwonIT"),
        ArltTecnologies(614, "Arlt Tecnologies"),
        BavariaYacts(637, "Bavaria Yacts"),
        DiverseYachtServices(641, "Diverse Yacht Services"),
        Wema(644, "Wema U.S.A dba KUS"),
        Garmin2(645, "Garmin"),
        ShenzhenJiuzhouHimunication(658, "Shenzhen Jiuzhou Himunication"),
        RockfordCorp(688, "Rockford Corp"),
        JLAudio(704, "JL Audio"),
        Autonnic(715, "Autonnic"),
        YachtDevices(717, "Yacht Devices"),
        REAPSystems(734, "REAP Systems"),
        AuElectronicsGroup(735, "Au Electronics Group"),
        LxNav(739, "LxNav"),
        DaeMyung(743, "DaeMyung"),
        Woosung(744, "Woosung"),
        ClarionUS(773, "Clarion US"),
        HMISystems(776, "HMI Systems"),
        OceanSignal(777, "Ocean Signal"),
        Seekeeper(778, "Seekeeper"),
        PolyPlanar(781, "Poly Planar"),
        FischerPandaDE(785, "Fischer Panda DE"),
        BroydaIndustries(795, "Broyda Industries"),
        CanadianAutomotive(796, "Canadian Automotive"),
        TidesMarine(797, "Tides Marine"),
        Lumishore(798, "Lumishore"),
        StillWater(799, "Still Water Designs and Audio"),
        BJTechnologiesBeneteau(802, "BJ Technologies (Beneteau)"),
        GillSensors(803, "Gill Sensors"),
        BlueWaterDesalination(811, "Blue Water Desalination"),
        FLIR(815, "FLIR"),
        UndheimSystems(824, "Undheim Systems"),
        TeamSurv(838, "TeamSurv"),
        FellMarine(844, "Fell Marine"),
        Oceanvolt(847, "Oceanvolt"),
        Prospec(862, "Prospec"),
        DataPanelCorp(868, "Data Panel Corp"),
        L3Technologies(890, "L3 Technologies"),
        RhodanMarineSystems(894, "Rhodan Marine Systems"),
        NexfourSolutions(896, "Nexfour Solutions"),
        ASAElectronics(905, "ASA Electronics"),
        MarinesCo(909, "Marines Co (South Korea)"),
        Nauticon(911, "Nautic-on"),
        Ecotronix(930, "Ecotronix"),
        TimbolierIndustries(962, "Timbolier Industries"),
        TJCMicro(963, "TJC Micro"),
        CoxPowertrain(968, "Cox Powertrain"),
        BlueSeas(969, "Blue Seas"),
        TeleflexMarine(1850, "Teleflex Marine (SeaStar Solutions)"),
        Raymarine(1851, "Raymarine"),
        Navionics(1852, "Navionics"),
        JapanRadioCo(1853, "Japan Radio Co"),
        NorthstarTechnologies(1854, "Northstar Technologies"),
        Furuno(1855, "Furuno"),
        Trimble(1856, "Trimble"),
        Simrad(1857, "Simrad"),
        Litton(1858, "Litton"),
        KvasarAB(1859, "Kvasar AB"),
        MMP(1860, "MMP"),
        VectorCantech(1861, "Vector Cantech"),
        YamahaMarine(1862, "Yamaha Marine"),
        FariaInstruments(1863, "Faria Instruments"),
        Somebody(1273, "Somebody ???");
        public final int id;
        public final String name;
        ManufacturerCode(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static ManufacturerCode lookup(int value) {
            for(ManufacturerCode e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("ManufacturerCode missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum Industry {
        UNDEFINED(-1, "Undefined"),
        Global(0, "Global"),
        Highway(1, "Highway"),
        Agriculture(2, "Agriculture"),
        Construction(3, "Construction"),
        Marine(4, "Marine"),
        Industrial(5, "Industrial"),
        Unknown(15, "Unknown");
        public final int id;
        public final String name;
        Industry(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static Industry lookup(int value) {
            for(Industry e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("Industry missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum SeatalkPilotMode {
        UNDEFINED(-1, "Undefined"),
        Standby(64, "Standby"),
        Auto(66, "Auto"),
        Wind(70, "Wind"),
        Track(74, "Track");
        public final int id;
        public final String name;
        SeatalkPilotMode(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static SeatalkPilotMode lookup(int value) {
            for(SeatalkPilotMode e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("SeatalkPilotMode missing value {} ", value);
            return UNDEFINED;
        }

    }
    public enum SeatalkKeystroke {
        UNDEFINED(-1, "Undefined"),
        Auto(1, "Auto"),
        Standby(2, "Standby"),
        Wind(3, "Wind"),
        Minus1(5, "-1"),
        Minus10(6, "-10"),
        Plus1(7, "+1"),
        PLus10(8, "+10"),
        Minus1and10(33, "-1 and -10"),
        PLus1and10(34, "+1 and +10"),
        Track(35, "Track");
        public final int id;
        public final String name;
        SeatalkKeystroke(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static SeatalkKeystroke lookup(int value) {
            for(SeatalkKeystroke e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("SeatalkKeystroke missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum SeatalkDeviceId {
        UNDEFINED(-1, "Undefined"),
        S100(3, "S100"),
        CourseComputer(5, "Course Computer");
        public final int id;
        public final String name;
        SeatalkDeviceId(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static SeatalkDeviceId lookup(int value) {
            for(SeatalkDeviceId e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("Seatlk Device ID missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum SeatalkNetworkGroup  {
        UNDEFINED(-1, "Undefined"),
        None(0, "None"),
        Helm1(1, "Helm 1"),
        Helm2(2, "Helm 2"),
        Cockpit(3, "Cockpit"),
        Flybridge(4, "Flybridge"),
        Mast(5, "Mast"),
        Group1(6, "Group 1"),
        Group2(7, "Group 2"),
        Group3(8, "Group 3"),
        Group4(9, "Group 4"),
        Group5(10, "Group 5");
        public final int id;
        public final String name;
        SeatalkNetworkGroup(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static SeatalkNetworkGroup lookup(int value) {
            for(SeatalkNetworkGroup e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("SeatalkNetworkGroup missing value {} ",  value);
            return UNDEFINED;
        }

    }
    public enum SeatalkDisplayColor  {
        UNDEFINED(-1, "Undefined"),
        Day1(0, "Day 1"),
        Day2(2, "Day 2"),
        RedBlack(3, "Red/Black"),
        Inverse(4, "Inverse");
        public final int id;
        public final String name;
        SeatalkDisplayColor(int id, String name) {
            this.id = id;
            this.name = name;
        }


        public String toString() {
            return "name:"+name+" (id:"+id+")";
        }
        public static SeatalkDisplayColor lookup(int value) {
            for(SeatalkDisplayColor e: values()) {
                if (e.id == value) {
                    return e;
                }
            }
            log.warn("SeatalkDisplayColor missing value {} ",  value);
            return UNDEFINED;
        }

    }
}
