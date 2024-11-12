package uk.co.tfd.kindle.nmea2000.canwidgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Util;
import uk.co.tfd.kindle.nmea2000.can.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ieb on 20/06/2020.
 */
public class PolarPage extends JPanel implements CanMessageListener, CanWidget, MouseListener  {
    private static Logger log = LoggerFactory.getLogger(PolarPage.class);

    private final PolarChart polarChart;
    private final InstrumentBoxes.Performance3CellBox performanceBox;
    private final InstrumentBoxes.Target3CellBox targetBox;
    private final InstrumentBoxes.ApparentWindBox aparentWindBox;
    private final InstrumentBoxes.TrueWind trueWindBox;
    private final boolean rotate;
    private final GraphBoxes.PerformanceGraph performanceGraph;
    private final GraphBoxes.RelativeAngleGraph trueWindAngleGraph;
    private final GraphBoxes.RelativeAngleGraph aparentWindAngleGraph;
    private final GraphBoxes.DepthGraph depthGraph;
    private int subPageNo;
    private long lastUpdate = 0;


    public PolarPage(boolean rotate) {
        this.rotate = rotate;
        this.setLayout(null);

        polarChart = new PolarChart();
        this.add(polarChart);
        polarChart.setBounds(0,0, Util.scaleKindle(1072), Util.scaleKindle(1072));

        int w, h, pad, x, y;
        pad = 5;

        performanceBox = new InstrumentBoxes.Performance3CellBox();
        this.add(performanceBox);
        //1390
        //center = 1072
        w = 425;
        h = 150;
        x = (1072/2)-pad-w;
        y = 1072+pad;
        performanceBox.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));

        targetBox = new InstrumentBoxes.Target3CellBox();
        this.add(targetBox);
        x = (1072/2)+pad;
        targetBox.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));

        aparentWindBox = new InstrumentBoxes.ApparentWindBox();
        this.add(aparentWindBox);
        x = (1072/2)-pad-w;
        y = 1072+2*pad+h;
        aparentWindBox.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));

        trueWindBox = new InstrumentBoxes.TrueWind();
        this.add(trueWindBox);
        x = (1072/2)+pad;
        trueWindBox.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));


        w = 1040;
        h = 330;
        x = (1072/2)-pad-500;

        performanceGraph = new GraphBoxes.PerformanceGraph();
        this.add(performanceGraph);
        performanceGraph.setVisible(false);
        y = 40+pad;
        performanceGraph.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));

        trueWindAngleGraph = new GraphBoxes.RelativeAngleGraph("twa");
        this.add(trueWindAngleGraph);
        trueWindAngleGraph.setVisible(false);
        y = y+335;
        trueWindAngleGraph.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));


        aparentWindAngleGraph = new GraphBoxes.RelativeAngleGraph("awa");
        this.add(aparentWindAngleGraph);
        aparentWindAngleGraph.setVisible(false);
        y = y+335;
        aparentWindAngleGraph.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));



        depthGraph = new GraphBoxes.DepthGraph();
        this.add(depthGraph);
        depthGraph.setVisible(false);
        y = y+335;
        depthGraph.setBounds(Util.scaleKindle(x), Util.scaleKindle(y), Util.scaleKindle(w), Util.scaleKindle(h));

        subPageNo = 0;
        log.info("Created polar page");
    }







    @Override
    public int[] getPgns() {
        log.info("Loading PGNS {} ", Arrays.toString(PerformanceCalculator.PerformanceCanMessage.getSourcePGNS()));
        int i = 0;
        int[] pgns = new int[PerformanceCalculator.PerformanceCanMessage.getSourcePGNS().length+1];
        for(Integer pgn : PerformanceCalculator.PerformanceCanMessage.getSourcePGNS()) {
            pgns[i++] = pgn;
        }
        pgns[i] = NavMessageHandler.PGN128267WaterDepth.PGN;
        return pgns;
    }

    @Override
    public void onDrop(int pgn) {

    }

    @Override
    public void onUnhandled(int pgn) {

    }

    @Override
    public void onMessage(CanMessage message) {
        if ( message instanceof PerformanceCalculator.PerformanceCanMessage) {
            PerformanceCalculator.PerformanceCanMessage performance = (PerformanceCalculator.PerformanceCanMessage) message;
            if (!CanMessageData.isNa(performance.twa, performance.tws, performance.stw)) {
                polarChart.updatePerformance(performance, true);
                performanceBox.update(performance.polarSpeedRatio, performance.polarSpeed, performance.stw);
                performanceGraph.update(performance.stw, performance.polarSpeed);
                performanceGraph.setTitle(String.format("stw=%2.1f", performance.stw * CanMessageData.scaleToKnots));
                trueWindAngleGraph.update(performance.twa, performance.upwindTarget.twa, performance.downwindTarget.twa);
                trueWindAngleGraph.setTitle(String.format("twa=%3.0f tws=%2.1f", performance.twa * CanMessageData.scaleToDegrees, performance.tws * CanMessageData.scaleToKnots));
                aparentWindAngleGraph.update(performance.awa, calcAWATarget(performance.upwindTarget.twa, performance.tws, performance.stw), calcAWATarget(performance.downwindTarget.twa, performance.tws, performance.stw));
                aparentWindAngleGraph.setTitle(String.format("awa=%3.0f aws=%2.1f ", performance.awa * CanMessageData.scaleToDegrees, performance.aws * CanMessageData.scaleToKnots));
                targetBox.update(performance.upwindTarget, performance.downwindTarget, performance.twa);
                trueWindBox.update(performance.twa, performance.tws, performance.vmg);
                aparentWindBox.update(performance.awa, performance.aws, performance.vmg);
                lastUpdate = System.currentTimeMillis();
            }
        } else if (message instanceof NavMessageHandler.PGN128267WaterDepth) {
            NavMessageHandler.PGN128267WaterDepth depth = (NavMessageHandler.PGN128267WaterDepth) message;
            if ( depth.depthBelowTransducer != CanMessageData.n2kDoubleNA) {
                depthGraph.update(-depth.depthBelowTransducer, -3.0);
                depthGraph.setTitle(String.format("dbt=%3.1f ", depth.depthBelowTransducer));
                lastUpdate = System.currentTimeMillis();
            }
        } else {
            if ( System.currentTimeMillis() - lastUpdate  > 30000 ) {
                double na = CanMessageData.n2kDoubleNA;
                polarChart.updatePerformance(null, false);
                performanceBox.update(na, na, na);
                performanceGraph.update(na, na);
                performanceGraph.setTitle("stw=-.-");
                trueWindAngleGraph.update(na, na, na);
                trueWindAngleGraph.setTitle("twa=-.- tws=-.- ");
                aparentWindAngleGraph.update(na, na, na);
                aparentWindAngleGraph.setTitle("awa=-.- aws=-.- ");
                depthGraph.update(na, na);
                depthGraph.setTitle("dbt=-.- ");
                targetBox.update(na, na, na);
                trueWindBox.update(na, na, na);
                aparentWindBox.update(na, na, na);
                lastUpdate = System.currentTimeMillis();
                log.info("Performance message timeout trigger: {}", message );
                repaint();
            }
        }
    }

    public double calcAWATarget(double twa, double tws, double stw) {
        if ( stw < 0.2/CanMessageData.scaleToKnots) {
            return twa;
        } else {
            double trueX = Math.cos(twa) * tws;
            double trueY = Math.sin(twa) * tws;
            return Math.atan2(trueY, stw + trueX);  // twa in radian
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


    @Override
    public void mouseClicked(MouseEvent e) {
        log.info("Mouse clicked, change to next set of boxes");
        if (subPageNo == 0) {
//            polarChart.setBounds(0,0, Util.scaleKindle(350), Util.scaleKindle(350));
            polarChart.setVisible(false);
            performanceBox.setVisible(false);
            targetBox.setVisible(false);
            trueWindBox.setVisible(false);
            aparentWindBox.setVisible(false);
            performanceGraph.setVisible(true);
            trueWindAngleGraph.setVisible(true);
            aparentWindAngleGraph.setVisible(true);
            depthGraph.setVisible(true);
            subPageNo = 1;
        } else if ( subPageNo == 1) {
            //polarChart.setBounds(0,0, Util.scaleKindle(1072), Util.scaleKindle(1072));
            performanceGraph.setVisible(false);
            trueWindAngleGraph.setVisible(false);
            aparentWindAngleGraph.setVisible(false);
            depthGraph.setVisible(false);
            polarChart.setVisible(true);
            performanceBox.setVisible(true);
            targetBox.setVisible(true);
            trueWindBox.setVisible(true);
            aparentWindBox.setVisible(true);
            subPageNo = 0;
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
