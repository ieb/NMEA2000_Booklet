package uk.co.tfd.kindle.nmea2000.canwidgets;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import java.awt.*;

public interface CanWidget {
    void addAncestorListener(AncestorListener visibilityListener);

    JComponent getJComponent();
}
