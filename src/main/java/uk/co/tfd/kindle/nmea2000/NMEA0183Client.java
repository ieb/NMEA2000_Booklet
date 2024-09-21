package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.cs.ext.ISO2022_CN;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SeaSmart client opens a connection to a socket, recieves messages, and sends them to the store.
 */
public class NMEA0183Client extends StatusUpdates implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NMEA0183Client.class);

    private Map<String, NMEA0183Handler> handlers = new HashMap<>();
    private boolean disabled = false;
    private Thread thread = null;
    private InetAddress address;
    private int port;
    private OutputStream outputStream;
    private String lastSentence;
    private long lastRead;
    private Socket socket = null;

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
        if ( socket != null) {
            log.info("Stop client before changing port.");
            return;
        }
        this.port = port;
    }

    public void setAddress(InetAddress address) {
        if ( socket != null) {
            log.info("Stop client before changing address.");
            return;
        }
        this.address = address;
    }

    public synchronized void start() {
        if ( socket == null && thread == null && address != null) {

            thread = new Thread(this);
            thread.start();
        } else {
            log.info("Stop client before starting. disabled:{} running:{} address:{} ", disabled, socket, address );
        }
    }

    public boolean isRunning() {
        return thread != null;
    }

    public synchronized void stop() {
        checkSocketClosed();
        thread = null;
        outputStream = null;
    }

    public void disable() {
        stop();
        disabled = true;
    }

    public boolean hasStalled() {
        return ((System.currentTimeMillis() - lastRead) > 30000);
    }

    private synchronized  void checkSocketClosed() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private synchronized  void openSocket() throws IOException {
        checkSocketClosed();
        updateStatus("Opening socket on "+address+":"+port);
        log.info("Opening socket on {} {} ", address, port);
        socket = new Socket(address, port);
        updateStatus("Connected to "+address+":"+port);
        log.info("Connected to {} {} ", address, port);
    }

    public void run()  {
        try {
            lastRead = System.currentTimeMillis();
            openSocket();
            InputStream inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            if (lastSentence != null) {
                send(lastSentence);
            }
            while (socket != null) {
                processLine(reader.readLine());
            }

            log.info("Disconnected from {}:{}", address, port);
        } catch (Exception e) {
            updateStatus("Client Error "+e.getMessage());
            log.warn("Client error {} ", e.getMessage(), e);
            log.debug("Client error cause ", e);
        } finally {
            checkSocketClosed();
            updateStatus("Disconnected from "+address+":"+port);
            log.info("Disconnected from {}:{}", address, port);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.info("Thread end interrupted");
            }
        }
    }
    public void processLine(String line) throws UnsupportedEncodingException {
        lastRead = System.currentTimeMillis();
        if ( line != null && line.startsWith("$") ) {
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
            log.info("Sending {}", sentence);
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
