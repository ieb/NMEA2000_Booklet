package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;
import java.awt.*;

public class BatteryPage extends JPanel implements CanMessageListener, CanWidget {
    private static final Logger log = LoggerFactory.getLogger(BatteryPage.class);
    private final InstrumentBoxes.Battery3CellBox engineBattery;
    private final InstrumentBoxes.Battery3CellBox serviceBattery;
    private final BMSInstrument bms;
    private long lastEngineBatteryUpdate = System.currentTimeMillis();
    private long lastServiceBatteryUpdate = System.currentTimeMillis();
    private long lastReg03Update = System.currentTimeMillis();
    private long lastReg04Update = System.currentTimeMillis();
    private long lastReg05Update = System.currentTimeMillis();

    public BatteryPage(boolean rotate) {
        setLayout(null);

        Font dialLabelFont = Util.createFont(14.0f);
        int w, h, pad, x, y;
        pad = 10;


        serviceBattery = new InstrumentBoxes.Battery3CellBox("Service V");
        this.add(serviceBattery);
        //1390
        //center = 1072
        w = 425;
        h = 150;
        x = (10);
        y = 10;
        serviceBattery.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));

        engineBattery = new InstrumentBoxes.Battery3CellBox("Engine V");
        this.add(engineBattery);
        //1390
        //center = 1072
        w = 425;
        h = 150;
        x = 425+pad;
        y = 10;
        engineBattery.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));

        bms = new BMSInstrument();
        this.add(bms);
        //1390
        //center = 1072
        w = 900;
        h = 900;
        x = 10;
        y = 150+pad;
        bms.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));

    }

    @Override
    public int[] getPgns() {
        return new int[] {
                ElectricalMessageHandler.PGN127508DCBatteryStatus.PGN,
                ElectricalMessageHandler.PGN130829BMSRegO3.PGN
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
        if ( message instanceof ElectricalMessageHandler.PGN127508DCBatteryStatus) {
            ElectricalMessageHandler.PGN127508DCBatteryStatus batt = (ElectricalMessageHandler.PGN127508DCBatteryStatus) message;
            if (batt.instance == 0) {
                if (batt.batteryVoltage != CanMessageData.n2kDoubleNA) {
                    lastEngineBatteryUpdate = System.currentTimeMillis();
                    engineBattery.update(batt.batteryVoltage, batt.batteryCurrent, batt.batteryTemperature);
                    this.repaint();
                }
            } else if (batt.instance == 1) {
                // only the BMS Controller has current. The Engine controller emits
                // PGN 127508 but does not have current (or temperature).
                // both emit the voltage measured for the service battery.
                // Longer term. might be better to disable the engine controller PGN 127508
                // as there is bound to be a discrepancy between the engin controller voltage
                // reading and the BMS.
                if (batt.batteryVoltage != CanMessageData.n2kDoubleNA
                        && batt.batteryCurrent != CanMessageData.n2kDoubleNA) {
                    lastServiceBatteryUpdate = System.currentTimeMillis();
                    serviceBattery.update(batt.batteryVoltage, batt.batteryCurrent, batt.batteryTemperature);
                    this.repaint();
                }
            }
        } else if ( message instanceof  ElectricalMessageHandler.PGN130829BMSRegO3 ) {
            ElectricalMessageHandler.PGN130829BMSRegO3 reg03 = (ElectricalMessageHandler.PGN130829BMSRegO3) message;
            bms.update(reg03);
            lastReg03Update = System.currentTimeMillis();
        } else if ( message instanceof  ElectricalMessageHandler.PGN130829BMSRegO4 ) {
            ElectricalMessageHandler.PGN130829BMSRegO4 reg04 = (ElectricalMessageHandler.PGN130829BMSRegO4) message;
            bms.update(reg04);
            lastReg04Update = System.currentTimeMillis();
        } else if ( message instanceof  ElectricalMessageHandler.PGN130829BMSRegO5 ) {
            ElectricalMessageHandler.PGN130829BMSRegO5 reg05 = (ElectricalMessageHandler.PGN130829BMSRegO5) message;
            bms.update(reg05);
            lastReg05Update = System.currentTimeMillis();
        } else if (message instanceof IsoMessageHandler.CanBusStatus) {
            if (System.currentTimeMillis() - lastEngineBatteryUpdate > 30000) {
                engineBattery.update(CanMessageData.n2kDoubleNA,CanMessageData.n2kDoubleNA,CanMessageData.n2kDoubleNA);
            }
            if (System.currentTimeMillis() - lastServiceBatteryUpdate > 30000) {
                serviceBattery.update(CanMessageData.n2kDoubleNA,CanMessageData.n2kDoubleNA,CanMessageData.n2kDoubleNA);
            }
            if (System.currentTimeMillis() - lastReg03Update > 30000) {
                bms.update((ElectricalMessageHandler.PGN130829BMSRegO3) null);
                bms.update((ElectricalMessageHandler.PGN130829BMSRegO4) null);
                bms.update((ElectricalMessageHandler.PGN130829BMSRegO5) null);
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

}
