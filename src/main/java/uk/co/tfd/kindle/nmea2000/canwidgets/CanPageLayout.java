package uk.co.tfd.kindle.nmea2000.canwidgets;

import com.amazon.agui.swing.ComplexStateModel;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.*;
import uk.co.tfd.kindle.nmea2000.can.CanMessageListener;
import uk.co.tfd.kindle.nmea2000.can.CanMessageProducer;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import static uk.co.tfd.kindle.nmea2000.Util.scaleKindle;

public class CanPageLayout extends JPanel  {




    /**
     * Created by ieb on 20/06/2020.
     */
    private static final Logger log = LoggerFactory.getLogger(CanPageLayout.class);
    private int pageNo;
    private final CanInstruments instruments;
    private int pagesCount;
    private int dragStartY;

    private ControlPage control;
    private Map<String, JPanel> cards = new HashMap<String, JPanel>();
    private JPanel visiblePage;

    public CanPageLayout(CanMessageProducer canMessageProducer, ControlPage controlPage) throws NoSuchMethodException {
        this.instruments = new CanInstruments(canMessageProducer);
        this.setLayout(null);
        this.pageNo = 0;
        // the kindle SDK implementation of MouseAdapter is incomplete
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int distanceY = e.getY() - dragStartY;
                if (distanceY > 200) {
                    showPage("control");
                }
                if ( visiblePage instanceof MouseMotionListener ) {
                    ((MouseMotionListener) visiblePage).mouseMoved(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                dragStartY = e.getY();
                if ( visiblePage instanceof MouseMotionListener ) {
                    ((MouseMotionListener) visiblePage).mouseMoved(e);
                }
            }
        });
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.info("Clicked {}", visiblePage);
                if ( visiblePage instanceof MouseListener ) {
                    ((MouseListener) visiblePage).mouseClicked(e);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }
        });


        this.control = controlPage;
        this.add(control);
        cards.put("control", control);
    }

    public void showPage(String name) {
        if ( cards.containsKey(name)) {
            for(Map.Entry<String, JPanel> e : cards.entrySet()) {
                e.getValue().setVisible(false);
            }
            visiblePage = cards.get(name);
            visiblePage.setVisible(true);
            log.info("Page should be showing now {}", name);
        } else {
            log.error("Page not found {}", name);
        }
    }
    public void loadConfiguration(Configuration config) {
        instruments.addCustomConfiguration(config);
        Map<String, Object> configuration = config.getConfiguration();
        java.util.List<Map<String, Object>> pages = (java.util.List<Map<String, Object>>) configuration.get("pages");
        pagesCount = 0;
        for (Map<String, Object> page : pages) {
            // create the card and register it
            String pageName = "page" + pagesCount;
            control.addMenuItem(pagesCount, (String)page.get("id"), new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showPage(pageName);
                }
            });

            pagesCount++;
            // build card contents
            int i = 0;
            boolean rotatePage = Util.option(page, "rotate", false);
            JPanel card = null;
            if ( page.containsKey("instruments") ) {
                java.util.List<java.util.List<String>> grid = (java.util.List<java.util.List<String>>) page.get("instruments");
                int nrows = grid.size();
                int ncols = grid.get(0).size();
                long hgap = Util.option(page, "hspace", 10L);
                long vgap = Util.option(page, "vspace", 10L);
                card = new Card();
                if (rotatePage) {
                    card.setLayout(new GridLayout((int) ncols, (int) nrows, (int) hgap, (int) vgap));
                } else {
                    card.setLayout(new GridLayout((int) nrows, (int) ncols, (int) hgap, (int) vgap));
                }

                if (rotatePage) {
                    for (int c = ncols - 1; c >= 0; c--) {
                        for (int r = 0; r < nrows; r++) {
                            String name = grid.get(r).get(c);
                            log.debug("Adding Rotated {} {} {}  ", r, c, name);
                            card.add(instruments.create(name, rotatePage).getJComponent(), i++);
                        }

                    }

                } else {
                    for (int r = 0; r < nrows; r++) {
                        for (int c = 0; c < ncols; c++) {
                            String name = grid.get(r).get(c);
                            log.debug("Adding {} {} {}  ", r, c, name);
                            card.add(instruments.create(name, rotatePage).getJComponent(), i++);
                        }
                    }
                }
                log.info("Loaded Page {} c{} r{} ", page.get("id"), ncols, nrows);
            } else if (page.containsKey("page")) {
                card  = (JPanel) instruments.create((String) page.get("page"), rotatePage);
            }
            if ( card != null ) {
                this.add(card);
                card.setBounds(0, 0, Util.scaleKindle(Util.KINDLE_FRAME_WIDTH), Util.scaleKindle(Util.KINDLE_FRAME_HEIGHT));
                card.setVisible(false);
                cards.put(pageName, card);
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
            log.info("Setting BG for {} ", c);
            c.setBackground(bg);
        }
    }


    private class Card extends JPanel {
        @Override
        public void setForeground(Color fg) {
            super.setForeground(fg);
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
    }

}
