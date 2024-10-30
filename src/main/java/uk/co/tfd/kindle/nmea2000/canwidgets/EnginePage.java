package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;

import java.awt.*;

import static uk.co.tfd.kindle.nmea2000.Util.*;

public class EnginePage extends JPanel implements CanMessageListener, CanWidget {
    private static final Logger log = LoggerFactory.getLogger(EnginePage.class);
    private final Tachometer tachometer;
    private final TemperatureGauge coolantGauge;
    private final FluidLevelGauge fuelGauge;
    private final TemperatureGauge alternatorTemperature;
    private final TemperatureGauge exhaustTemperature;
    private final VoltageGauge alternatorVoltage;
    private long lastTachometerUpdate = System.currentTimeMillis();
    private long lastCoolantUpdate = System.currentTimeMillis();
    private long lastFuelUpdate = System.currentTimeMillis();
    private long lastAlternatorVoltageUpdate = System.currentTimeMillis();
    private long lastAlternatorTemperatureUpdate = System.currentTimeMillis();
    private long lastExhaustTemperatureUpdate = System.currentTimeMillis();
    private long lastEngineHoursUpdate = System.currentTimeMillis();
    private long lastEngineStatusUpdate = System.currentTimeMillis();

    public EnginePage(boolean rotate) {
        setLayout(null);
        Font dialLabelFont = Util.createFont(14.0f);
        tachometer = new Tachometer(true);
        this.add(tachometer);
        tachometer.setBounds(scaleKindle(10), scaleKindle(690), scaleKindle(700), scaleKindle( 700));

        coolantGauge = new TemperatureGauge(true);
        this.add(coolantGauge);
        coolantGauge.setTitle("Coolant °C");
        coolantGauge.setBounds(scaleKindle(10), scaleKindle(390), scaleKindle(300), scaleKindle( 300));

        LabelRotated coolantLabel = new LabelRotated("Coolant", true);
        this.add(coolantLabel);
        coolantLabel.setFont(dialLabelFont);
        Dimension s = coolantLabel.getPreferredSize();
        coolantLabel.setBounds(scaleKindle(318), scaleKindle(541), scaleKindle(150), scaleKindle(40) );





        exhaustTemperature = new TemperatureGauge(true);
        this.add(exhaustTemperature);
        exhaustTemperature.setTitle("Exhaust °C");
        exhaustTemperature.setBounds(scaleKindle(360), scaleKindle(390), scaleKindle(300), scaleKindle( 300));

        LabelRotated exhaustLabel = new LabelRotated("Exhaust Temp", true);
        this.add(exhaustLabel);
        exhaustLabel.setFont(dialLabelFont);
        s = exhaustLabel.getPreferredSize();
        exhaustLabel.setBounds(scaleKindle(669), scaleKindle(541), scaleKindle(150), scaleKindle(40) );

        alternatorTemperature = new TemperatureGauge(true);
        this.add(alternatorTemperature);
        alternatorTemperature.setTitle("Alternator °C");
        alternatorTemperature.setBounds(scaleKindle(730), scaleKindle(390), scaleKindle(300), scaleKindle( 300));

        LabelRotated alternatorLabel = new LabelRotated("Alternator Temp", true);
        this.add(alternatorLabel);
        alternatorLabel.setFont(dialLabelFont);
        s = alternatorLabel.getPreferredSize();
        alternatorLabel.setBounds(scaleKindle(1036), scaleKindle(541), scaleKindle(150), scaleKindle(40) );

        alternatorVoltage = new VoltageGauge(true);
        this.add(alternatorVoltage);
        alternatorVoltage.setTitle("Alternator V");
        alternatorVoltage.setBounds(scaleKindle(730), scaleKindle(724), scaleKindle(300), scaleKindle( 300));

        LabelRotated alternatorVoltageLabel = new LabelRotated("Alternator V", true);
        this.add(alternatorVoltageLabel);
        alternatorVoltageLabel.setFont(dialLabelFont);
        s = alternatorVoltageLabel.getPreferredSize();
        alternatorVoltageLabel.setBounds(scaleKindle(1036), scaleKindle(875), scaleKindle(150), scaleKindle(40) );

        fuelGauge = new FluidLevelGauge(true);
        this.add(fuelGauge);
        fuelGauge.setTitle("Fuel Tank %");
        fuelGauge.setBounds(scaleKindle(730), scaleKindle(1070), scaleKindle(300), scaleKindle( 300));

        LabelRotated fuelLabel = new LabelRotated("Fuel", true);
        this.add(fuelLabel);
        fuelLabel.setFont(dialLabelFont);
        s = fuelLabel.getPreferredSize();
        fuelLabel.setBounds(scaleKindle(1036), scaleKindle(1226), scaleKindle(150), scaleKindle(40));

       // Util.addMouseTracker(this);
    }

    @Override
    public int[] getPgns() {
        return new int[] {
                EngineMessageHandler.PGN127488RapidEngineData.PGN,
                EngineMessageHandler.PGN127489EngineDynamicParam.PGN,
                EngineMessageHandler.PGN127505FluidLevel.PGN
        };
    }

    @Override
    public void onDrop(int pgn) {

    }

    @Override
    public void onUnhandled(int pgn) {

    }

    @Override
    public void onMessage(CanMessage message) {
        if (message instanceof EngineMessageHandler.PGN127488RapidEngineData) {
            EngineMessageHandler.PGN127488RapidEngineData engine = (EngineMessageHandler.PGN127488RapidEngineData) message;
            if (engine.engineSpeed != CanMessageData.n2kDoubleNA) {
                tachometer.setRpm(engine.engineSpeed, true);
                lastTachometerUpdate = System.currentTimeMillis();
            }
        } else if ( message instanceof EngineMessageHandler.PGN127489EngineDynamicParam) {
            EngineMessageHandler.PGN127489EngineDynamicParam engineDynamicParam = (EngineMessageHandler.PGN127489EngineDynamicParam) message;
            if (engineDynamicParam.engineCoolantTemperature != CanMessageData.n2kDoubleNA) {
                coolantGauge.setTemperature(engineDynamicParam.engineCoolantTemperature, true);
                lastCoolantUpdate = System.currentTimeMillis();
            }
            if (engineDynamicParam.alternatorVoltage != CanMessageData.n2kDoubleNA) {
                alternatorVoltage.setVoltage(engineDynamicParam.alternatorVoltage, true);
                lastAlternatorVoltageUpdate = System.currentTimeMillis();
            }
            if (engineDynamicParam.engineCoolantTemperature != CanMessageData.n2kDoubleNA) {
                alternatorTemperature.setTemperature(engineDynamicParam.engineCoolantTemperature, true);
                lastAlternatorTemperatureUpdate = System.currentTimeMillis();
            }
            if (engineDynamicParam.engineHours != CanMessageData.n2kDoubleNA) {
                tachometer.setEngineHours(engineDynamicParam.engineHours, true);
                lastEngineHoursUpdate = System.currentTimeMillis();
            }
            if (engineDynamicParam.status1 != CanMessageData.n2kInt8NA) {
                tachometer.setStatus1(engineDynamicParam.status1, true);
                lastEngineStatusUpdate = System.currentTimeMillis();
            }
            if (engineDynamicParam.status2 != CanMessageData.n2kInt8NA) {
                tachometer.setStatus2(engineDynamicParam.status2, true);
                lastEngineStatusUpdate = System.currentTimeMillis();
            }
        } else if ( message instanceof EngineMessageHandler.PGN130312Temperature) {
            EngineMessageHandler.PGN130312Temperature temperature = (EngineMessageHandler.PGN130312Temperature) message;
            if ( temperature.source == N2KReference.TemperatureSource.ExhaustGasTemperature
                    && temperature.actualTemperature != CanMessageData.n2kDoubleNA) {
                exhaustTemperature.setTemperature(temperature.actualTemperature, true);
                lastExhaustTemperatureUpdate = System.currentTimeMillis();
            }
        } else if ( message instanceof EngineMessageHandler.PGN127505FluidLevel) {
            EngineMessageHandler.PGN127505FluidLevel tank = (EngineMessageHandler.PGN127505FluidLevel) message;
            if ( tank.fluidType == N2KReference.TankType.Fuel && tank.fluidLevel != CanMessageData.n2kDoubleNA) {
                fuelGauge.setFuelLevel(tank.fluidLevel, true);
                tachometer.setFuelLevel(tank.fluidLevel, true);
                lastFuelUpdate = System.currentTimeMillis();
                this.repaint();
            }
        } else if (message instanceof IsoMessageHandler.CanBusStatus) {
            if (System.currentTimeMillis() - lastTachometerUpdate > 30000) {
                tachometer.setRpm(0, false);
            }
            if (System.currentTimeMillis() - lastCoolantUpdate > 30000) {
                coolantGauge.setTemperature(0, false);
            }
            if (System.currentTimeMillis() - lastFuelUpdate > 30000) {
                fuelGauge.setFuelLevel(0, false);
            }
            if (System.currentTimeMillis() - lastAlternatorVoltageUpdate > 30000) {
                alternatorVoltage.setVoltage(0, false);
            }
            if (System.currentTimeMillis() - lastAlternatorTemperatureUpdate > 30000) {
                alternatorTemperature.setTemperature(0, false);
            }
            if (System.currentTimeMillis() - lastExhaustTemperatureUpdate > 30000) {
                exhaustTemperature.setTemperature(0, false);
            }
            if (System.currentTimeMillis() - lastEngineHoursUpdate > 30000) {
                tachometer.setEngineHours(0, false);
            }
            if (System.currentTimeMillis() - lastEngineStatusUpdate > 30000) {
                tachometer.setStatus1(0, false);
                tachometer.setStatus2(0, false);
            }
        }
    }

    @Override
    public void setForeground(Color fg) {
        for(Component c: this.getComponents()) {
            c.setForeground(fg);
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        for(Component c: this.getComponents()) {
            c.setBackground(bg);
        }
    }


    @Override
    public JComponent getJComponent() {
        return this;
    }


    public static class LabelRotated extends JComponent {
        private final boolean centered;
        private final String text;

        public LabelRotated(String text, boolean centered) {
            this.setLayout(null);
            this.text = text;
            this.centered = centered;
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
        }

        @Override
        public void paint(Graphics g) {
            Util.drawStringRotated(text, 0, 0, getFont(), (Graphics2D)g);
        }
    }
}
