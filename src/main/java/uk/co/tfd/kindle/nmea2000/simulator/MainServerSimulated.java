package uk.co.tfd.kindle.nmea2000.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.NMEA0183Client;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Runs a demo server reading from a file
 */
public class MainServerSimulated {
    private static final Logger log = LoggerFactory.getLogger(MainServer.class);
    private final FirmwareSimulator firmwareSimulator;
    private boolean running = false;

    public MainServerSimulated() {
        firmwareSimulator = new FirmwareSimulator();

    }

    public void run() throws IOException {
        ServerSocket server = null;
        Socket socket = null;
        try {
            server = new ServerSocket(10112);
            log.info("Listening  {}", server);
            while (true) {
                socket = server.accept();
                log.info("Connecton from {}", socket);
                try {
                    OutputStream out = socket.getOutputStream();
                    PGNFilter pgnFilter = new PGNFilter();


                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    running = true;
                    Thread reader = new Thread(new Runnable() {

                        @Override
                        public void run(){
                            log.info("Reader starting");
                            try {
                                while (running) {
                                    if (!in.ready()) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        String line = in.readLine();
                                        log.info("Got command {}", line);
                                        if (line != null) {
                                            if (NMEA0183Client.checkSumOk(line)) {
                                                if (line.startsWith("$PCDCM,1,")) {
                                                    // pgn list
                                                    Set<Long> newPgnFilterList = new HashSet<>();
                                                    String[] parts = line.substring(0, line.lastIndexOf('*')).split(",");
                                                    for (int i = 3; i < parts.length; i++) {
                                                        newPgnFilterList.add(Long.parseLong(parts[i]));
                                                    }
                                                    pgnFilter.setPgnFilterList(newPgnFilterList);
                                                    log.info("Filter now {}", pgnFilter);
                                                } else {
                                                    log.info("Command not regognised");
                                                }
                                            } else {
                                                log.info("Checksum failed");
                                            }
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException("Reader Failed", e);
                            }
                            log.info("Reader stopped");

                        }
                    });
                    reader.start();
                    while (true) {
                        long t = firmwareSimulator.sendNext(out, pgnFilter);
                        if ( t > 0 ) {
                            Thread.sleep(t);
                        } else if ( t < 0) {
                            log.warn("Delay < 0 {}", t);
                        }
                    }
                } catch (IOException ex) {
                    log.info("Failed with client",ex);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    running = false;
                    if (socket != null) {
                        socket.close();
                        socket = null;
                    }
                }
            }
        } finally {
            if (socket != null) {
                socket.close();
            }
            if (server != null) {
                server.close();
            }
        }
    }


    public static void main(String argv[]) throws IOException {
        MainServerSimulated mainServer = new MainServerSimulated();
        mainServer.run();
    }

}
