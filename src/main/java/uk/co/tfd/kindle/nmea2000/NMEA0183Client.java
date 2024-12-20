package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * SeaSmart client opens a connection to a socket, recieves messages, and sends them to the store.
 */
public class NMEA0183Client extends StatusUpdates {

    private static final Logger log = LoggerFactory.getLogger(NMEA0183Client.class);

    private Map<String, NMEA0183Handler> handlers = new HashMap<>();
    private boolean disabled = false;
    private OutputStream outputStream;
    private String lastSentence;
    private long lastRead = System.currentTimeMillis();
    private Socket socket = null;
    private int recieved = 0;
    private int sent = 0;
    private SocketHandler socketHandler;
    private SocketAddress socketAddress;

    public NMEA0183Client(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
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

    public void setSocketAddress(SocketAddress socketAddress) {
        if ( socket != null) {
            log.info("Stop client before changing socketAddress.");
            return;
        }
        this.socketAddress = socketAddress;
    }


    public synchronized boolean start() {
        if ( disabled ) {
            return true;
        }
        if ( socketHandler != null ) {
            log.info("Stop client before starting.  socket:{}  ", socketAddress );
            return true;
        }
        if ( socketAddress == null ) {
            log.info("No address set, cant start " );
            return false;
        }
        try {
            socketHandler = new SocketHandler(socketAddress);
        } catch (IOException ex) {
            log.info("Connect Failed ", ex);
            return false;
        }
        Thread thread = new Thread(socketHandler);
        thread.start();
        log.info("Created new thread for reading ", thread);
        return true;

    }
    public synchronized void stop() {
        if ( socketHandler != null ) {
            socketHandler.stopRunning();
        }
        socketHandler = null;
    }

    public boolean isRunning() {
        return (socketHandler != null && socketHandler.isRunning());
    }


    public void disable() {
        stop();
        disabled = true;
    }

    public boolean hasStalled() {
        if ( socketAddress == null ) {
            return false;
        }
        return ((System.currentTimeMillis() - lastRead) > 30000);
    }


    public String getStatusMessage() {
        String state = isRunning()?"disconnected":"connected";
        int age = (int)((System.currentTimeMillis() - lastRead)/1000);
        return String.format("Client %s tx:%d rx:%d age:%d", state, sent, recieved, age );
    }




    public  void processLine(String line) throws UnsupportedEncodingException {
        lastRead = System.currentTimeMillis();
        if ( line != null && line.startsWith("$") ) {
            recieved++;
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

    public void send(String sentence) {
        lastSentence = sentence;
        if ( socketHandler != null && socketHandler.isRunning()) {
            try {
                socketHandler.send(sentence);
            } catch (IOException e) {
                log.info("Failed to send cmd will retry");
            }
        }
    }

    public class SocketHandler implements Runnable {
        private final Socket socket;
        private final SocketAddress socketAddress;
        private boolean running;
        public SocketHandler(SocketAddress socketAddress) throws IOException {
            this.socketAddress = socketAddress;
            updateStatus("Opening socket on "+socketAddress);
            log.info("Opening socket on {} ", socketAddress);
            try {
                socket = new Socket();
                socket.connect(socketAddress, 5000);
                updateStatus("Connected to " + socketAddress);
                log.info("Connected to {} ", socketAddress);
            } catch (IOException e) {
                updateStatus("Failed top open socket on "+socketAddress);
                throw e;
            }
        }



        public synchronized void send(String sentence) throws IOException {
            if ( socket != null && outputStream != null) {
                log.info("Sending {}", sentence);
                sent++;
                outputStream.write(sentence.getBytes("ASCII"));
                outputStream.write(new byte[]{ '\r', '\n'});
            }
        }


        public void run()  {
            try {
                running = true;
                lastRead = System.currentTimeMillis();
                InputStream inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                if (lastSentence != null) {
                    // every time the socket is opened, always resend the last sentence.
                    send(lastSentence);
                }
                while (running) {
                    processLine(reader.readLine());
                }
                log.info("Disconnected from {}", socketAddress);
            } catch (Exception e) {
                updateStatus("Client Error "+e.getMessage());
                log.warn("Client error {} ", e.getMessage(), e);
                log.debug("Client error cause ", e);
            } finally {
                running = false;
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                updateStatus("Disconnected from "+socketAddress);
                log.info("Disconnected from {}", socketAddress);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.info("Thread end interrupted");
                }
            }
        }

        public void stopRunning() {
            running = false;
        }

        public boolean isRunning() {
            return running;
        }
    }



}
