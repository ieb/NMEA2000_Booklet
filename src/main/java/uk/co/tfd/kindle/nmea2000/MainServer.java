package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.WindowFocusListener;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Runs a demo server reading from a file
 */
public class MainServer {
    private static final Logger log = LoggerFactory.getLogger(MainServer.class);
    private Set<Integer> pgnFilterList = new HashSet<>();
    private boolean running = false;

    public MainServer() {

    }

    public void run(String sourceFile) throws IOException {
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
                                                    Set<Integer> newPgnFilterList = new HashSet<>();
                                                    String[] parts = line.substring(0, line.lastIndexOf('*')).split(",");
                                                    for (int i = 3; i < parts.length; i++) {
                                                        newPgnFilterList.add(Integer.parseInt(parts[i]));
                                                    }
                                                    pgnFilterList = newPgnFilterList;
                                                    log.info("New filter {}", pgnFilterList);
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
                        sendFile(sourceFile, out);
                        Thread.sleep(5000);
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

    public void sendFile(String fileName, OutputStream out) throws IOException {
        log.info("Opening {} ", new File(fileName).getAbsoluteFile());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            long lastTimeStamp = 0;
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                // sleep till next message should be sent.

                // poacher and gamekeeper collude.
                String pcdin = convertToPCDIN(line);
                if (pcdin != null) {
                    pcdin = NMEA0183Client.addCheckSum(pcdin);
                    if ( line.trim().startsWith("{") ) {
                        // no timestamp present, use a 100ms delay
                        Thread.sleep(100);
                    } else {
                        try {
                            long timeStamp = Long.parseLong(line.split(":")[0].trim());
                            if (lastTimeStamp > 0 && lastTimeStamp < timeStamp) {
                                Thread.sleep(timeStamp - lastTimeStamp);
                            }
                            lastTimeStamp = timeStamp;
                        } catch (NumberFormatException e) {

                            log.info("No timestamp {}", line);
                        }
                    }
                    out.write(pcdin.getBytes("ASCII"));
                    out.write(new byte[]{'\r', '\n'});
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if ( br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }


    private String convertToPCDIN(String sampleLine) {

        int pgn = -1;
        int source = -1;
        String data = null;
        int timestamp = (int)(System.currentTimeMillis() & 0x7fffffff);
        // 228849 : Pri:2 PGN:127257 Source:204 Dest:255 Len:8 Data:FF,9C,8,C7,0,21,0,FF
        // format 2 {n: 22, pgn: 127245, src: 205, msg: '00ffff7febfaffff'}
        sampleLine = sampleLine.trim();
        if ( sampleLine.startsWith("{")) {
            // format 2
            for (String p: sampleLine.split(",")) {
                p = p.trim();
                if ( p.startsWith("pgn: ")) {
                    pgn = Integer.parseInt(p.substring(5));
                } else if ( p.startsWith("src: ")) {
                    source = Integer.parseInt(p.substring(5));
                } else if ( p.startsWith("msg: '")) {
                    /*
                    boolean instring = false;
                    StringBuilder sb = new StringBuilder();
                    int charCount = 0;
                    for ( char c : p.toUpperCase().toCharArray()) {
                        if (c == '\'') {
                            if ( instring ) {
                                break;
                            }
                            instring = !instring;
                            charCount = 0;
                        } else if (instring ) {
                            if ( charCount == 2) {
                                sb.append(",");
                                charCount = 0;
                            }
                            if ( charCount > 0 || c != '0') {
                                sb.append(c);
                            }
                            charCount++;
                        }
                    }
                    */

                    data = p.split("'")[1].toUpperCase();
                }
            }
        } else {

            for (String p : sampleLine.split(" ")) {
                if (p.startsWith("PGN:")) {
                    pgn = Integer.parseInt(p.substring(4));
                } else if (p.startsWith("Source:")) {
                    source = Integer.parseInt(p.substring(7));
                } else if (p.startsWith("Data:")) {
                    String[] parts = p.substring(5).split(",");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < parts.length; i++) {
                        if ( parts[i].length() == 1 ) {
                            sb.append("0");
                        }
                        sb.append(parts[i]);
                    }
                    data =  sb.toString();
                }
            }
        }
        if ( pgn != -1 && source != -1 && data != null) {
            if (pgnFilterList.size() > 0 && !pgnFilterList.contains(pgn)) {
                return null;
            }
            return "$PCDIN,"
                    +Integer.toHexString(pgn).toUpperCase()+","
                    +Integer.toHexString(timestamp).toUpperCase()+","
                    +Integer.toHexString(source).toUpperCase()+","
                    +data;
        }
        return null;
    }


    public static void main(String argv[]) throws IOException {
        MainServer mainServer = new MainServer();
        mainServer.run("src/test/resources/samplecandata2.txt");
    }

}
