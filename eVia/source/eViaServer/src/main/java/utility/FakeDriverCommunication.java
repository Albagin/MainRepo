package utility;

import enums.DriverStatus;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.json.JSONException;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.util.Date;
import java.util.Random;

@SuppressWarnings("ALL")
public class FakeDriverCommunication extends Thread{
    public static final String PASS = "pass";
    private static final String SURNAME = "surname";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String MYSERVER = "@myserver";
    private static final String SEND_MESSAGE_SUCCES = "Message successfully sent:\n\t";
    private static final String SENDING_MESSAGE_TO = "Sending message to: ";
    private static final String NEWLINE_TAB_TO = "\n\tto: ";
    private static final String TYPE = "type";
    private static final String TIME = "time";
    private static final String FB_ID = "fbId";
    private static final String NAME = "name";
    private static final String USERNAME = "username";
    public static final String DIRECTION = "direction";

    private Integer id;
    private static Integer count = 0;
    private DriverStatus status = DriverStatus.FREE;
    private JSONObject jsonObject;
    private XMPPConnection connection;
    private Double fakeLatitude;
    private Double fakeLongitude;
    private Double fakeDirection;
    private String fbid = "";

    public FakeDriverCommunication() {
        id = count;
        count++;
        Random r = new Random();
        fakeLatitude = 53.008321 + (53.013962 - 53.008321) * r.nextDouble(); //random lat near UMK WMiI
        fakeLongitude = 18.591818 + (18.598061 - 18.591818) * r.nextDouble(); //random lng near UMK WMiI
        fakeDirection = -180. + (180 - -180.) * r.nextDouble(); //random lng near UMK WMiI
    }

    private String getUsername() {
        return "fakeDriver" + id;
    }

    private void logException(Exception e) {
        Logger.error(e);
    }

    private void createAccount() {
        new Thread(() -> {
            try {
                AccountManager accountManager = connection.getAccountManager();
                accountManager.createAccount(getUsername(), PASS);
                connection.disconnect();
                connection.connect();
                connection.login(getUsername(), PASS);
            } catch (XMPPException | JSONException e) {
                Logger.error(e);
            }
        }).start();
    }

    public void sendLocationToClient(String user) {
        Date date = new Date();
        long time = date.getTime();
        JSONObject js = new JSONObject();
        js.put("time", time);
        js.put("type", "locationFromClient");
        js.put(LONGITUDE, fakeLongitude);
        js.put(LATITUDE, fakeLatitude);
        sendMessageTo(user, js);
    }

    public void sendLocationToServer() {
        JSONObject js = new JSONObject();
        Date date = new Date();
        long time = date.getTime();
        js.put(TYPE, "locationfromdriver");
        js.put(USERNAME, getUsername());
        js.put(TIME, time);
        js.put(FB_ID, fbid);
        js.put(NAME, "test");
        js.put(SURNAME, "driver");
        js.put(LATITUDE, fakeLatitude);
        js.put(LONGITUDE, fakeLongitude);
        js.put(DIRECTION, fakeDirection);
        js.put("status", status.toString());
    }

    //czy blisko siebie

    //info, że są w kursie do serwera

    //sprawdzanie rozdzielenia w kursie

    //info o końcu kursu po rozdzileniu

    //jeżeli od pasażera dostaje koniec kursu to potwierdza i odsyła o tym info do serwera i pasażera a potem kończy działanie

    @Override
    public void run() {
        try {
            String host = "zebrowski.ddns.net";
            int port = 7778;
            ConnectionConfiguration config = new ConnectionConfiguration(host, port);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setDebuggerEnabled(false);
            config.setSendPresence(true);
            connection = new XMPPConnection(config);
            connection.connect();
            connection.login(getUsername(), PASS);
        } catch (SASLErrorException e) {
            Logger.warn("New user");
            createAccount();
        } catch (XMPPException e) {
            Logger.warn(e);
        }
        Logger.info("Connected to XMPP server");

        final MessageListener messageListener = (chat, message) -> {
            jsonObject = new JSONObject(message.getBody());
            Date msgDate = new Date(jsonObject.getLong(TIME));
            Date date = new Date();
            date.setTime(date.getTime() - 30 * 1000);

            if (msgDate.before(date)) {
                Logger.debug("Message expired:\n\t" + jsonObject);
            }
            else {
                Logger.info(chat.getParticipant() + " said:\n\t" + " type: " + jsonObject.getString(TYPE) + ", " + jsonObject);
                try {
                    switch (jsonObject.getString(TYPE)) {
                        //kejsy
                        default:
                            Logger.info("Cannot recognize type of message\n\t" + jsonObject);
                            break;
                    }
                }catch (Exception e) {
                    logException(e);
                }
            }
        };

        connection.getChatManager().addChatListener((chat, b) -> chat.addMessageListener(messageListener));

        //noinspection StatementWithEmptyBody
        while(connection.isConnected()) {
            //needed
        }
    }

    private void sendMessageTo(String to, JSONObject message) {
        if (!to.contains(MYSERVER)) {
            to += MYSERVER;
        }
        Logger.debug(SENDING_MESSAGE_TO + to);
        ChatManager chatmanager = connection.getChatManager();
        Chat newChat = chatmanager.createChat(to, (chat, m) -> {
            //
        });
        try {
            Logger.debug(SENDING_MESSAGE_TO + to + "\n\t" + message);
            newChat.sendMessage(message.toString());
            Logger.info(SEND_MESSAGE_SUCCES + message + NEWLINE_TAB_TO + to);
        } catch (XMPPException e) {
            logException(e);
        }

    }
}
