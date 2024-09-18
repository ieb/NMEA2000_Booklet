package uk.co.tfd.kindle.nmea2000.can;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ManualPerformanceSimulator extends CanMessageProducer implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(ManualPerformanceSimulator.class);

    private double awa = 0;
    private double aws = 0;
    private double stw = 0;
    private double hdm = 0;
    private double roll = 0;
    private double variation = 0;

    public ManualPerformanceSimulator(Polar polar) {

        this.addListener(new PerformanceCalculator(this, polar));
        this.addListener(new WindCalculator(this));
        this.addListener(new LeewayCalculator(this));
    }

    @Override
    public String toString() {
        return String.format("awa:%4.1f aws: %4.1f stw: %4.1f hdm:%4.1f roll: %4.1f, var: %4.1f",
                awa*CanMessageData.scaleToDegrees,
                aws*CanMessageData.scaleToKnots,
                stw*CanMessageData.scaleToKnots,
                hdm*CanMessageData.scaleToDegrees,
                roll*CanMessageData.scaleToDegrees,
                variation*CanMessageData.scaleToDegrees
                );
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch(e.getKeyChar()) {
            case 's':
                aws = aws - 0.1/CanMessageData.scaleToKnots;
                if ( aws < 0 ) aws = 0;
                this.emitMessage(new NavMessageHandler.PGN130306Wind(0,
                        awa,
                        aws,
                        N2KReference.WindReference.Apparent));
                break;
            case 'S':
                aws = aws + 0.1/CanMessageData.scaleToKnots;
                this.emitMessage(new NavMessageHandler.PGN130306Wind(0,
                        awa,
                        aws,
                        N2KReference.WindReference.Apparent));
                break;
            case 'a':
                awa = awa - 1/CanMessageData.scaleToDegrees;
                if ( awa < -Math.PI ) awa = -Math.PI;
                this.emitMessage(new NavMessageHandler.PGN130306Wind(0,
                        awa,
                        aws,
                        N2KReference.WindReference.Apparent));
                break;
            case 'A':
                awa = awa + 1/CanMessageData.scaleToDegrees;
                if ( awa > Math.PI ) awa = Math.PI;
                this.emitMessage(new NavMessageHandler.PGN130306Wind(0,
                        awa,
                        aws,
                        N2KReference.WindReference.Apparent));
                break;
            case 'w':
                stw = stw - 0.1/CanMessageData.scaleToKnots;
                if ( stw < 0 ) stw = 0;
                this.emitMessage(new NavMessageHandler.PGN128259Speed(0,
                        stw,
                        CanMessageData.n2kDoubleNA,
                        uk.co.tfd.kindle.nmea2000.can.N2KReference.SwrtType.PaddleWheel,
                        1));
                break;
            case 'W':
                stw = stw + 0.1/CanMessageData.scaleToKnots;
                this.emitMessage(new NavMessageHandler.PGN128259Speed(0,
                        stw,
                        CanMessageData.n2kDoubleNA,
                        uk.co.tfd.kindle.nmea2000.can.N2KReference.SwrtType.PaddleWheel,
                        1));
                break;
            case 'h':
                hdm = hdm - 1/CanMessageData.scaleToDegrees;
                if ( hdm < 0 ) hdm = hdm+2*Math.PI;
                this.emitMessage(new NavMessageHandler.PGN127250Heading(0,
                        hdm,
                        CanMessageData.n2kDoubleNA,
                        variation,
                        N2KReference.HeadingReference.Magnetic));
                break;
            case 'H':
                hdm = hdm + 1/CanMessageData.scaleToDegrees;
                if ( hdm > 2*Math.PI ) hdm = hdm-2*Math.PI;
                this.emitMessage(new NavMessageHandler.PGN127250Heading(0,
                        hdm,
                        CanMessageData.n2kDoubleNA,
                        variation,
                        N2KReference.HeadingReference.Magnetic));
                break;
            case 'r':
                roll = roll - 1/CanMessageData.scaleToDegrees;
                if ( roll < -Math.PI/4 ) roll = -Math.PI/4;
                this.emitMessage(new NavMessageHandler.PGN127257Attitude(0,
                        CanMessageData.n2kDoubleNA,
                        CanMessageData.n2kDoubleNA,
                        roll));
                break;
            case 'R':
                roll = roll + 1/CanMessageData.scaleToDegrees;
                if ( roll > Math.PI/4 ) roll = Math.PI/4;;
                this.emitMessage(new NavMessageHandler.PGN127257Attitude(0, CanMessageData.n2kDoubleNA,CanMessageData.n2kDoubleNA, roll));
                break;
            case 'v':
                variation = variation - 0.5/CanMessageData.scaleToDegrees;
                if ( variation < -15/CanMessageData.scaleToDegrees ) variation = -15/CanMessageData.scaleToDegrees;
                this.emitMessage(new NavMessageHandler.PGN127258MagneticVariation(0,
                        variation,
                        (int)(System.currentTimeMillis()/(24*3600000)),
                        N2KReference.VariationSource.Manual
                        ));
                break;
            case 'V':
                variation = variation - 0.5/CanMessageData.scaleToDegrees;
                if ( variation > 15/CanMessageData.scaleToDegrees ) variation = 15/CanMessageData.scaleToDegrees;
                this.emitMessage(new NavMessageHandler.PGN127258MagneticVariation(0,
                        variation,
                        (int)(System.currentTimeMillis()/(24*3600000)),
                        N2KReference.VariationSource.Manual
                ));
                break;
            case 'Z':
                this.emitMessage(new NavMessageHandler.PGN130306Wind(0,
                        awa,
                        aws,
                        N2KReference.WindReference.Apparent));
                this.emitMessage(new NavMessageHandler.PGN128259Speed(0,
                        stw,
                        CanMessageData.n2kDoubleNA,
                        uk.co.tfd.kindle.nmea2000.can.N2KReference.SwrtType.PaddleWheel,
                        1));
                this.emitMessage(new NavMessageHandler.PGN127250Heading(0,
                        hdm,
                        CanMessageData.n2kDoubleNA,
                        variation,
                        N2KReference.HeadingReference.Magnetic));
                this.emitMessage(new NavMessageHandler.PGN127257Attitude(0,
                        CanMessageData.n2kDoubleNA,
                        CanMessageData.n2kDoubleNA,
                        roll));
                this.emitMessage(new NavMessageHandler.PGN127258MagneticVariation(0,
                        variation,
                        (int)(System.currentTimeMillis()/(24*3600000)),
                        N2KReference.VariationSource.Manual
                ));
                break;


        }
        log.info("State {}",toString());

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
