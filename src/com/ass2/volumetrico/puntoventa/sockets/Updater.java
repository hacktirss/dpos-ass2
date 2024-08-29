package com.ass2.volumetrico.puntoventa.sockets;

import com.ass2.volumetrico.puntoventa.common.OmicromSLQHelper;
import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.database.mysql.MySQLHelper;
import com.softcoatl.database.DBException;
import com.softcoatl.utils.DateUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.GenericSleeper;
import com.softcoatl.utils.PropertyLoader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Properties;

public class Updater implements Runnable {

    public static final String PPT_LISTENING_PORT = "updater.port";
    public static final String CONFIGURATION_FILE = "/com/detisa/omicrom/sockets/updater.properties";

    private DinamicVO<String, String> pos;
    private DinamicVO<String, String> version;
    private final Properties properties;
    private Thread goThread=null;
    String versionID;
    public Updater(String versionID) {
        properties = PropertyLoader.load(CONFIGURATION_FILE);
        this.versionID = versionID;
    }//Constructor

    public void updateVersion() {
        StringBuilder sqlBuffer = new StringBuilder();
        
        sqlBuffer.append("UPDATE pos_version");
        sqlBuffer.append("\nSET status = 'E'");
        sqlBuffer.append("\nWHERE id = ").append(version.NVL("UPDATE_ID"));
        
        try {
            MySQLHelper.getInstance().execute(sqlBuffer.toString());
        } catch (DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
    }

    public void getVersion() {
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("SELECT" )
                .append("\npos_id AS POS_ID,")
                .append("\npos_version.id AS UPDATE_ID,")
                .append("\nversioning.version_id AS VERSION_ID,")
                .append("\nschedulled_date AS SCHEDULLED_DATE,")
                .append("\nstatus AS STATUS,")
                .append("\nmajor_version AS MAJOR_VERSION,")
                .append("\nminor_version AS MINOR_VERSION,")
                .append("\nbuild AS BUILD,")
                .append("\nrevision AS REVISION,")
                .append("\nrelease_date AS release_date,")
                .append("\nlocation AS LOCATION ")
                .append("\nFROM pos_version, versioning ")
                .append("\nWHERE pos_version.version_id = versioning.version_id ")
                .append("\nAND versioning.version_id = ").append(versionID)
                .append("\nAND pos_id=").append(pos.NVL("POS_ID"));

        try {
            LogManager.debug(sqlBuffer);
            version = OmicromSLQHelper.getFirst(sqlBuffer);
        } catch (DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
    }//getTerminal
    public void getPOS(String ip) {
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("SELECT");
        sqlBuffer.append("\npos_id AS POS_ID,");
        sqlBuffer.append("\nserial AS SERIAL,");
        sqlBuffer.append("\nmodel AS MODEL,");
        sqlBuffer.append("\nip AS IP,");
        sqlBuffer.append("\nstatus AS STATUS");
        sqlBuffer.append("\nFROM  pos_catalog");
        sqlBuffer.append("\nWHERE ip='").append(ip).append("'");

        try {
            LogManager.debug(sqlBuffer);
            pos = OmicromSLQHelper.getFirst(sqlBuffer);
        } catch (DBException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        }
    }//getTerminal

    private void listen () {
        ServerSocket voSS = null;
        Socket voRequest = null;
        Calendar deadLine = DateUtils.fncoAdd(Calendar.MINUTE, 1); //Waits connection 10 minutes 
        String deviceIP;
        byte[] buffer = new byte [1024];
        int readedBytes;
        int writedBytes = 0;
        int availableBytes;
        int listeningSocket;

        LogManager.info("Starting updater");

        try {
            listeningSocket = Integer.parseInt(properties.getProperty(PPT_LISTENING_PORT));
            LogManager.info("Listening port "+listeningSocket);
            voSS = new ServerSocket(listeningSocket);
            voSS.setSoTimeout(30000);
            while (deadLine.getTimeInMillis()>Calendar.getInstance().getTimeInMillis()) {
                try {
                    LogManager.debug("Waiting for connection...");
                    voRequest = voSS.accept();
                    deviceIP = voRequest.getInetAddress().getHostAddress();
                    getPOS(deviceIP);
                    getVersion();
                    LogManager.debug("Accepted request from "+deviceIP);
                    GenericSleeper.get().sleep();
                    try (BufferedOutputStream tBos = new BufferedOutputStream(voRequest.getOutputStream());
                        BufferedInputStream fbis = new BufferedInputStream(new FileInputStream(new File(version.NVL("LOCATION"))))) {
                        availableBytes = fbis.available();
                        while ((readedBytes=fbis.read(buffer))>0) {
                            tBos.write(buffer, 0, readedBytes);
                            writedBytes+=readedBytes;
                        }//while
                        if (writedBytes>0 && writedBytes==availableBytes) {
                            updateVersion();
                            break;
                        }
                    }
                } catch (IOException ex) {
                    LogManager.error(ex);
                    LogManager.debug("Trace", ex);
                } finally {
                    if (voRequest != null) {
                        try {
                            voRequest.close();
                            voRequest = null;
                        } catch (IOException ignored) {}
                    }
                }//finalization
            }//listening
        } catch (NumberFormatException | IOException ex) {
            LogManager.error(ex);
            LogManager.debug("Trace", ex);
        } finally {
            if (voSS != null) {
                try {
                    voSS.close();
                } catch (IOException ignored) {}
            }
        }
        
        LogManager.info("Stopping updater");
    }

    public void initService() {
        goThread=new Thread(this);
        goThread.start();
    }//start

    @Override
    public void run() {
        listen();
    }//run
}//Updater
