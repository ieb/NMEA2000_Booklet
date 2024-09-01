package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.SeaSmartHandler;
import uk.co.tfd.kindle.nmea2000.can.CanMessageListener;
import uk.co.tfd.kindle.nmea2000.can.CanMessageProducer;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class WidgetComponentLstener implements AncestorListener {
    private final CanMessageProducer canMessageProducer;
    private static final Logger log = LoggerFactory.getLogger(WidgetComponentLstener.class);

    public WidgetComponentLstener(CanMessageProducer canMessageProducer) {
        this.canMessageProducer = canMessageProducer;
    }

    @Override
    public void ancestorAdded(AncestorEvent event) {
        Component c = event.getComponent();
        log.info("Component visible {}",c);
        if ( c instanceof CanMessageListener) {
            canMessageProducer.addListener((CanMessageListener)c);
            canMessageProducer.addPgnsToStream(((CanMessageListener)c).getPgns());
        }
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {
        Component c = event.getComponent();
        log.info("Component hidden {}",c);
        if ( c instanceof CanMessageListener ) {
            canMessageProducer.removeListener((CanMessageListener)c);
            canMessageProducer.removePgnsFromStream(((CanMessageListener)c).getPgns());
        }
    }

    @Override
    public void ancestorMoved(AncestorEvent event) {
        log.info("Ancestor moved {}",event);
    }
}
