package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;
import java.awt.*;

public class BatteryPage extends JPanel implements CanMessageListener, CanWidget {
    private static final Logger log = LoggerFactory.getLogger(BatteryPage.class);
    //private final VoltageText engineVoltage;
    //private final VoltageText serviceVoltage;
    //private final CurrentText serviceCurrent;
    private long lastEngineBatteryUpdate = System.currentTimeMillis();
    private long lastServiceBatteryUpdate = System.currentTimeMillis();

    public BatteryPage(boolean rotate) {
        setLayout(null);

        Font dialLabelFont = Util.createFont(14.0f);
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
 //                   engineVoltage.setVoltage(batt.batteryVoltage, true);
                    lastEngineBatteryUpdate = System.currentTimeMillis();
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
//                    serviceVoltage.setVoltage(batt.batteryVoltage, true);
//                    serviceCurrent.setCurrent(batt.batteryCurrent, true);
                    lastServiceBatteryUpdate = System.currentTimeMillis();
                    this.repaint();
                }
            }
        } else if ( message instanceof  ElectricalMessageHandler.PGN130829BMSRegO3 ) {
            log.info("Got Reg03 {} ", message);
        } else if ( message instanceof  ElectricalMessageHandler.PGN130829BMSRegO4 ) {
            log.info("Got Reg04 {} ", message);
        } else if ( message instanceof  ElectricalMessageHandler.PGN130829BMSRegO5 ) {
            log.info("Got Reg05 {} ", message);
        } else if (message instanceof IsoMessageHandler.CanBusStatus) {
            if (System.currentTimeMillis() - lastEngineBatteryUpdate > 30000) {
                //engineVoltage.setVoltage(0, false);
            }
            if (System.currentTimeMillis() - lastServiceBatteryUpdate > 30000) {
                //serviceVoltage.setVoltage(0, false);
                //serviceCurrent.setCurrent(0, false);
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
