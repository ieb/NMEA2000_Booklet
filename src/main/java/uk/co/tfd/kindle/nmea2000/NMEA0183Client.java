package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * SeaSmart client opens a connection to a socket, recieves messages, and sends them to the store.
 */
public class NMEA0183Client extends StatusUpdates implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NMEA2000Discovery.class);

    private Map<String, NMEA0183Handler> handlers = new HashMap<>();
    private boolean running = false;
    private Thread thread = null;
    private InetAddress address;
    private int port;
    private OutputStream outputStream;
    private String lastSentence;

    public NMEA0183Client(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        try {
            lastSentence = addCheckSum("$PCDCM,1,0");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public NMEA0183Client() {
        try {
            lastSentence = addCheckSum("$PCDCM,1,0");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public void start() {
        if ( !running && address != null) {
            Thread thread = new Thread(this);
            running = true;
            thread.start();
        }
    }

    public void stop() {

        running = false;
        outputStream = null;
    }


    public void run()  {
        Socket socket = null;
        while(running) {
            try {
                socket = new Socket(address, port);
                InputStream inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                log.info("Connected to {}:{}", address, port);
                if ( lastSentence != null ) {
                    send(lastSentence);
                }
                while (running) {
                    processLine(reader.readLine());
                }
                log.info("Disconnected from {}:{}", address, port);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                log.warn("Client error ",e);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("Disconnected from {}:{}", address, port);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void processLine(String line) throws UnsupportedEncodingException {
        if ( line.startsWith("$") ) {
            // perhaps NMEA, check the checksum first.
            if ( checkSumOk(line)) {
                String talkerId = line.substring(1,3);
                String messageId = line.substring(3,6);
                NMEA0183Handler handler = handlers.get(messageId);
                if ( handler != null ) {
                    handler.parseMessage(line);
                } else {
                    log.info("No handler found for {}, line was {} ", messageId, line);
                }
            }
        }
    }

    public void send(String sentence) throws IOException {
        lastSentence = sentence;
        if ( outputStream != null) {
            outputStream.write(sentence.getBytes("ASCII"));
            outputStream.write(new byte[]{ '\r', '\n'});
        }
    }


    public void addHandler(String id, NMEA0183Handler handler) {
        handlers.put(id, handler);
    }

    public static String addCheckSum(String sentence) throws UnsupportedEncodingException {
        return String.format("%s*%02X", sentence, calculateChecksum(sentence));
    }

    public static boolean checkSumOk(String line) throws UnsupportedEncodingException {
        int star = line.lastIndexOf('*');
        if  (star > 0) {
            byte csv = Byte.parseByte(line.substring(line.lastIndexOf('*') + 1), 16);
            byte cs = calculateChecksum(line);
            if (csv == cs) {
                return true;
            }
            log.info("CheckSum failed. sentence:{}, calculated:{} read:{}", line, (int) cs, (int) csv);
        } else {
            log.info("Sentence has no checksum. sentence:{}", line);
        }
        return false;
    }

    public static byte calculateChecksum(String sentence) throws UnsupportedEncodingException {
        byte cs = 0;
        byte[] bytes = sentence.getBytes("ASCII");
        for (int i = 1; i < bytes.length && bytes[i] != '*'; i++) {
            cs = (byte)(cs ^ bytes[i]);
        }
        return cs;
    }

}
