package eviateam.evia.utility;

import android.os.Bundle;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Class for communication between client-server and client-client
 */
public class Communication {

    public static final int PORT = 5222;
    private static XMPPTCPConnection connection;
    private String HOST = "153.92.24.62"; //10.10.201.53 192.168.137.1 192.168.1.24
    private static String username;
    private JSONObject messageReceived;
    private boolean passenger_found = false;
    private boolean driver_found = false;
    private boolean driver_agreed = false;
    private boolean passenger_agreed = false;
    private boolean jakDojade = false;
    private boolean stopThread = false;
    private boolean isPassengerWithMe = false;
    private boolean startCourse = false;
    private boolean endCourse = false;
    private boolean endCourseAgreed = false;
    private boolean isLoggedIn = false;
    private boolean isLoggedInAsGuest = false;
    private int courseId;
    private boolean isDriverWithMe = false;
    private boolean cancelClicked = false;
    private boolean endCourseByDistance = false;
    private boolean foundCars = false;
    private int userHaveCars = 0;
    private String status = "FREE";
    private JSONArray cars = new JSONArray();
    private boolean carPhotoReceived = false;
    private boolean carPhotoAdded = false;
    private boolean carPhotoRemoved = false;
    private JSONObject carPhotoJSON = new JSONObject();
    private boolean outOfTime = false;

    public Communication() {}

    public Communication(String username) {
        Communication.username = username;
    }

    private XMPPTCPConnectionConfiguration buildConfiguration() throws XmppStringprepException, UnknownHostException {
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();

        builder.setHostAddress(InetAddress.getByName(HOST));
        //builder.setHost(HOST);
        builder.setPort(PORT);
        builder.setCompressionEnabled(false);
        //builder.setDebuggerFactory();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        builder.setSendPresence(true);
        DomainBareJid serviceName = JidCreate.domainBareFrom("myserver"); //myserver
        builder.setXmppDomain(serviceName);

        return builder.build();
    }

    public void disconnect() {
        if(connection != null) connection.disconnect();
        connection = null;
    }

    /**
     * Connects client to the xmpp server
     */
    public void serverConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    XMPPTCPConnectionConfiguration config = buildConfiguration();
                    SmackConfiguration.DEBUG = true;
                    connection = new XMPPTCPConnection(config);
                    connection.connect();
                } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Creates account and logs in to xmpp server
     * @param pass value for password
     * @param bundle contains information about user
     */
    private void createAccount(final String pass, final Bundle bundle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                    AccountManager accountManager = AccountManager.getInstance(connection);
                    accountManager.sensitiveOperationOverInsecureConnection(true);
                    accountManager.createAccount(Localpart.from(username), pass);
                    connection.disconnect();
                    connection.connect();
                    connection.login(Localpart.from(username), pass);
                    isLoggedIn = true;
                    sendNewUserToServer(bundle);
                } catch (InterruptedException | SmackException | IOException | XMPPException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends new user to server
     * @param bundle contains information about user
     */
    private void sendNewUserToServer(Bundle bundle) throws XmppStringprepException, JSONException, SmackException.NotConnectedException, InterruptedException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
        Chat chat = chatManager.chatWith(jid);

        JSONObject js = new JSONObject();
        Date date = new Date();
        long time = date.getTime();
        js.put("type", "newUser");
        js.put("xmpplogin", username);
        js.put("time", time);
        js.put("fbId", bundle.getString("id"));
        js.put("firstName", bundle.getString("fname"));
        js.put("surname", bundle.getString("lname"));
        js.put("email", bundle.getString("email"));
        chat.send(String.valueOf(js));
    }

    /**
     * Logs in to xmpp server
     * @param pass value for password
     * @param bundle contains information about user
     */
    public void loginToServer(final String pass, final Bundle bundle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                    connection.login(Localpart.from(username), pass);
                    isLoggedIn = true;
                } catch (SASLErrorException e) {
                    createAccount("pass", bundle);
                } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void loginToServerAsGuest(final String pass, final String fbId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                    connection.login("guest", pass, Resourcepart.fromOrNull(fbId));
                    isLoggedInAsGuest = true;
                } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends client's current location to the other user
     * @param user user xmpp nickname
     * @param latitude latitude
     * @param longitude longitude
     */
    public void sendLocationToClient(String user, double latitude, double longitude) throws XmppStringprepException, JSONException, SmackException.NotConnectedException, InterruptedException {

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom(user + "@myserver");
        Chat chat = chatManager.chatWith(jid);


        Date date = new Date();
        long time = date.getTime();
        JSONObject js = new JSONObject();
        js.put("time", time);
        js.put("type", "locationFromClient");
        js.put("longitude", longitude);
        js.put("latitude", latitude);
        chat.send(String.valueOf(js));
    }

    /**
     * Sends information about ending the course to the other user
     * @param user user's xmpp nickname
     * @param latitude latitude
     * @param longitude longitude
     * @throws XmppStringprepException
     * @throws JSONException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    public void sendEndCourse(String user, double latitude, double longitude) throws XmppStringprepException, JSONException, SmackException.NotConnectedException, InterruptedException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom(user + "@myserver");
        Chat chat = chatManager.chatWith(jid);

        Date date = new Date();
        long time = date.getTime();

        JSONObject js = new JSONObject();
        js.put("type", "endCourse");
        js.put("longitude", longitude);
        js.put("latitude", latitude);
        js.put("courseId", courseId);
        js.put("time", time);
        chat.send(String.valueOf(js));
    }

    /**
     * Send information about cancelling the course to the other user
     * @param user user's xmpp nickname
     * @param latitude latitude
     * @param longitude longitude
     * @throws XmppStringprepException
     * @throws JSONException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    public void sendCancelCourse(String user, double latitude, double longitude) throws XmppStringprepException, JSONException, SmackException.NotConnectedException, InterruptedException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom(user + "@myserver");
        Chat chat = chatManager.chatWith(jid);

        Date date = new Date();
        long time = date.getTime();

        JSONObject js = new JSONObject();
        js.put("type", "cancelCourse");
        js.put("longitude", longitude);
        js.put("latitude", latitude);
        js.put("courseId", courseId);
        js.put("time", time);
        chat.send(String.valueOf(js));
    }


    /**
     * Sends information about ending the course by too big distance to the other user
     * @param user user's xmpp nickname
     * @param latitude latitude
     * @param longitude longitude
     * @throws XmppStringprepException
     * @throws JSONException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    public void sendEndCourseByDistance(String user, double latitude, double longitude) throws XmppStringprepException, JSONException, SmackException.NotConnectedException, InterruptedException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom(user + "@myserver");
        Chat chat = chatManager.chatWith(jid);

        Date date = new Date();
        long time = date.getTime();

        JSONObject js = new JSONObject();
        js.put("type", "endCourseByDistance");
        js.put("longitude", longitude);
        js.put("latitude", latitude);
        js.put("courseId", courseId);
        js.put("time", time);
        chat.send(String.valueOf(js));
    }

    /**
     * Sends info about starting new or ending course to the other client
     * @param user user's xmpp nickname
     * @param mode driver or passenger
     * @param latitude latitude
     * @param longitude longitude
     * @param start_end starting or ending course
     * @param to_who string contains to who send message
     * @throws XmppStringprepException
     * @throws JSONException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    public void sendInfo(String user, String mode, double latitude, double longitude, String start_end, String to_who) throws XmppStringprepException, JSONException, SmackException.NotConnectedException, InterruptedException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom(to_who + "@myserver");
        Chat chat = chatManager.chatWith(jid);

        Date date = new Date();
        long time = date.getTime();

        JSONObject js = new JSONObject();
        js.put("type", start_end);
        js.put("longitude", longitude);
        js.put("latitude", latitude);
        js.put("time", time);
        if(mode.equals("passenger")) {
            js.put("passengerName", username);
            js.put("driverName", user);
        } else {
            js.put("passengerName", user);
            js.put("driverName", username);
        }
        js.put("courseId", courseId);
        chat.send(String.valueOf(js));
        if(start_end.equals("endCourse")) {
            sendEndCourseAgreed(user, latitude, longitude);
        }

    }

    /**
     * Sends information about agreeing to the end of the course
     * @param user user's xmpp nickname
     * @param latitude latitude
     * @param longitude longitude
     * @throws JSONException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     * @throws XmppStringprepException
     */
    private void sendEndCourseAgreed(String user, double latitude, double longitude) throws JSONException, SmackException.NotConnectedException, InterruptedException, XmppStringprepException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom(user + "@myserver");
        Chat chat = chatManager.chatWith(jid);

        Date date = new Date();
        long time = date.getTime();

        JSONObject js = new JSONObject();
        js.put("type", "endCourseAgreed");
        js.put("longitude", longitude);
        js.put("latitude", latitude);
        js.put("courseId", courseId);
        js.put("time", time);
        chat.send(String.valueOf(js));
    }

    /**
     * Sends information to the other user that you have ran out of time
     * @param user user's xmpp nickname
     */
    public void sendOutOfTime(final String user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    EntityBareJid jid = JidCreate.entityBareFrom(user + "@myserver");
                    Chat chat = chatManager.chatWith(jid);

                    Date date = new Date();
                    long time = date.getTime();

                    JSONObject js = new JSONObject();
                    js.put("type", "outOfTime");
                    js.put("time", time);
                    chat.send(String.valueOf(js));
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends changes made to the client's car to the server
     * @param type type of the message
     * @param car contains information about car
     * @throws XmppStringprepException
     * @throws JSONException
     */
    public void sendCarChanges(final String type, final JSONObject car) throws XmppStringprepException, JSONException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
                    Chat chat = chatManager.chatWith(jid);

                    Date date = new Date();
                    long time = date.getTime();

                    car.put("type", type);
                    car.put("time", time);
                    car.put("name", username);
                    chat.send(String.valueOf(car));
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends changes made to the client's car to the server but with new photo
     * @param type type of the message
     * @param car contains information about car
     * @param photo String contains photo of the car
     * @throws XmppStringprepException
     * @throws JSONException
     */
    public void sendCarChangesWithPhoto(final String type, final JSONObject car, final String photo) throws XmppStringprepException, JSONException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
                    Chat chat = chatManager.chatWith(jid);

                    Date date = new Date();
                    long time = date.getTime();

                    car.put("type", type);
                    car.put("time", time);
                    car.put("name", username);
                    car.put("photo", photo);
                    chat.send(String.valueOf(car));
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends request for object of a car to the server
     */
    public void sendCarsRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
                    Chat chat = chatManager.chatWith(jid);

                    Date date = new Date();
                    long time = date.getTime();

                    JSONObject js = new JSONObject();
                    js.put("type", "CarsRequest");
                    js.put("time", time);
                    js.put("name", username);
                    chat.send(String.valueOf(js));
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends question to the server if user has cars attached to the account
     */
    public void sendUserHaveCarsRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
                    Chat chat = chatManager.chatWith(jid);

                    Date date = new Date();
                    long time = date.getTime();

                    JSONObject js = new JSONObject();
                    js.put("type", "userHaveCars");
                    js.put("time", time);
                    js.put("name", username);
                    chat.send(String.valueOf(js));
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends request for photo of a car
     * @param photoId photo id
     * @param carId car id
     */
    public void carPhotoRequest(final int photoId, final int carId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
                    Chat chat = chatManager.chatWith(jid);

                    Date date = new Date();
                    long time = date.getTime();

                    JSONObject js = new JSONObject();
                    js.put("type", "carPhotoRequest");
                    js.put("carId", carId);
                    js.put("photoId", photoId);
                    js.put("time", time);
                    js.put("name", username);
                    chat.send(String.valueOf(js));
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Method listens for list of cars from server
     */
    public void carsListener() {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        IncomingChatMessageListener listener;
        chatManager.addIncomingListener(listener = new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                try {
                    messageReceived = new JSONObject(message.getBody());
                    Date msgDate = new Date(messageReceived.getLong("time"));
                    Date date = new Date();
                    date.setTime(date.getTime() - 30 * 1000);

                    if (msgDate.before(date)) {
                        System.out.println("Wiadomosc przeterminowana!");
                    } else {
                        try {
                            if (messageReceived.getString("type").equals("CarsList")) {
                                cars = messageReceived.getJSONArray("CarsList");
                                foundCars = true;
                            }
                            else if (messageReceived.getString("type").equals("carPhoto")) {
                                carPhotoReceived = true;
                                carPhotoJSON = messageReceived;
                            }
                            else if (messageReceived.getString("type").equals("carPhotoAdded")) {
                                carPhotoAdded = true;
                            }
                            else if (messageReceived.getString("type").equals("carPhotoRemoved")) {
                                carPhotoRemoved = true;
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                        System.out.println("New message from " + from + ": " + message.getBody());
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }

            }
        });

        while(connection.isConnected()) {
            if(stopThread) {
                chatManager.removeIncomingListener(listener);
                break;
            }
        }


    }

    /**
     * Method listens for new passenger requests
     */
    public void passengerListener() {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        IncomingChatMessageListener listener;
        chatManager.addIncomingListener(listener = new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                try {
                    messageReceived = new JSONObject(message.getBody());
                    Date msgDate = new Date(messageReceived.getLong("time"));
                    Date date = new Date();
                    date.setTime(date.getTime() - 30 * 1000);

                    if (msgDate.before(date)) {
                        System.out.println("Wiadomosc przeterminowana!");
                        System.out.println("New message from " + from + ": " + message.getBody());
                    }
                    else {
                        try {
                            if (messageReceived.getString("type").equals("question") && status.equals("FREE")) {
                                if (messageReceived.getString("passenger_found").equals("true")) {
                                    passenger_found = true;
                                    status = "FOUND_PARTNER";
                                }
                            }
                            else if (messageReceived.getString("type").equals("locationFromClient") && (status.equals("FOUND_PARTNER") || (status.equals("COURSE")))) {
                                passenger_agreed = true;
                            }
                            else if (messageReceived.getString("type").equals("courseId") && status.equals("FOUND_PARTNER")) {
                                startCourse = true;
                                courseId = messageReceived.getInt("courseId");
                                status = "COURSE";
                            }
                            else if (messageReceived.getString("type").equals("endCourse") && status.equals("COURSE")) {
                                endCourse = true;
                            }
                            else if (messageReceived.getString("type").equals("endCourseAgreed") && status.equals("COURSE")) {
                                endCourseAgreed = true;
                            }
                            else if (messageReceived.getString("type").equals("cancelCourse") && status.equals("FOUND_PARTNER")) {
                                cancelClicked = true;
                            }
                            else if (messageReceived.getString("type").equals("userHaveCars")) {
                                if(messageReceived.getBoolean("userHaveCars")) {
                                    userHaveCars = 1;
                                }
                                else userHaveCars = 2;
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    System.out.println("New message from " + from + ": " + message.getBody());
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }

            }
        });

        while(connection.isConnected()) {
            if(stopThread) {
                chatManager.removeIncomingListener(listener);
                break;
            }
        }


    }

    /**
     * Method listens for new driver requests
     */
    public void driverListener() {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        IncomingChatMessageListener listener;
        chatManager.addIncomingListener(listener = new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                try {
                    messageReceived = new JSONObject(message.getBody());
                    Date msgDate = new Date(messageReceived.getLong("time"));
                    Date date = new Date();
                    date.setTime(date.getTime() - 30 * 1000);

                    if (msgDate.before(date)) {
                        System.out.println("Wiadomosc przeterminowana!");
                    } else {
                        try {
                            if(messageReceived.getString("type").equals("question") && (status.equals("FREE") || status.equals("FOUND_PARTNER")) ) {
                                //TODO No value for driver_found, jesli driver i passenger to ta sama osoba
                                if (messageReceived.getString("driver_found").equals("true")) {
                                    driver_found = true;
                                    status = "FOUND_PARTNER";
                                }
                            }
                            else if (messageReceived.getString("type").equals("locationFromClient") && (status.equals("FOUND_PARTNER") || (status.equals("COURSE")))) {
                                driver_agreed = true;
                            }
                                //TODO no value for driver_found, łączyłem się z Mateuszem
                            else if(messageReceived.getString("type").equals("jakDojadeQuestion")) {
                                jakDojade = true;
                            }
                            else if(messageReceived.getString("type").equals("courseId") && status.equals("FOUND_PARTNER")) {
                                startCourse = true;
                                courseId = messageReceived.getInt("courseId");
                                status = "COURSE";
                            }
                            else if (messageReceived.getString("type").equals("endCourse") && status.equals("COURSE")) {
                                endCourse = true;
                            }
                            else if (messageReceived.getString("type").equals("endCourseAgreed") && status.equals("COURSE")) {
                                isDriverWithMe = true;
                                endCourseAgreed = true;
                            }
                            else if (messageReceived.getString("type").equals("endCourseByDistance") && status.equals("COURSE")) {
                                endCourseByDistance = true;
                            }
                            else if (messageReceived.getString("type").equals("cancelCourse") && status.equals("FOUND_PARTNER")) {
                                cancelClicked = true;
                            }
                            else if(messageReceived.getString("type").equals("outOfTime")) {
                                outOfTime = true;
                            }

                            System.out.println("New message from " + from + ": " + message.getBody());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        while(connection.isConnected()) {
            if(stopThread) {
                chatManager.removeIncomingListener(listener);
                break;
            }
        }


    }

    /**
     * Sends location to the server
     * @param js contains type locationfromdriver or requestfrompassenger
     * @param mode contains mode driver or passenger_mode
     * @throws XmppStringprepException
     * @throws JSONException
     * @throws InterruptedException
     * @throws SmackException.NotConnectedException
     */
    public void sendLocationToServer(JSONObject js, String mode) throws XmppStringprepException, JSONException, InterruptedException, SmackException.NotConnectedException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
        Chat chat = chatManager.chatWith(jid);

        Date date = new Date();
        long time = date.getTime();
        if(mode.equals("driver")) js.put("type", "locationfromdriver");
        else if(mode.equals("passenger_mode")) js.put("type", "requestfrompassenger");
        js.put("username", username);
        js.put("time", time);
        chat.send(String.valueOf(js));
    }

    /**
     * Sends rate of the course
     * @param rate integer from 1 to 5
     * @param ratedUserNick xmpp nickname of rated user
     * @param ratedUserMode mode of rated user
     * @throws XmppStringprepException
     * @throws JSONException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    public void sendRate(final int rate, final String ratedUserNick, final String ratedUserMode) throws XmppStringprepException, JSONException, SmackException.NotConnectedException, InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
                    Chat chat = chatManager.chatWith(jid);

                    Date date = new Date();
                    long time = date.getTime();

                    JSONObject js = new JSONObject();
                    js.put("time", time);
                    js.put("type", "rate");
                    js.put("courseId", courseId);
                    js.put("rate", rate);
                    js.put("ratedUserNick", ratedUserNick);
                    js.put("ratedUserMode", ratedUserMode);
                    chat.send(String.valueOf(js));
                } catch (SmackException.NotConnectedException | XmppStringprepException | JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * Sends information about agreeing to the new course
     * @param yesOrNo contains answer
     * @param questionId question id
     * @throws XmppStringprepException
     * @throws JSONException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    public void sendYesOrNo(boolean yesOrNo, int questionId) throws XmppStringprepException, JSONException, SmackException.NotConnectedException, InterruptedException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        EntityBareJid jid = JidCreate.entityBareFrom("server@myserver");
        Chat chat = chatManager.chatWith(jid);

        Date date = new Date();
        long time = date.getTime();

        JSONObject js = new JSONObject();
        js.put("time", time);
        js.put("type", "answer");
        js.put("yesOrNo", yesOrNo);
        js.put("questionId", questionId);
        chat.send(String.valueOf(js));
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    public static void setUsername(String username) {
        Communication.username = username;
    }

    public boolean isStartCourse() {
        return startCourse;
    }

    public boolean isDriver_found() {
        return driver_found;
    }

    public void setDriver_found(boolean driver_found) {
        this.driver_found = driver_found;
    }

    public boolean passenger_found() {
        return passenger_found;
    }

    public void setPassenger_found(boolean passenger_found) {
        this.passenger_found = passenger_found;
    }

    public JSONObject getJs2() {
        return messageReceived;
    }

    public boolean isJakDojade() {
        return jakDojade;
    }

    public void setJakDojade(boolean jakDojade) {
        this.jakDojade = jakDojade;
    }

    public boolean isDriver_agreed() {
        return driver_agreed;
    }

    public void setDriver_agreed(boolean driver_agreed) {
        this.driver_agreed = driver_agreed;
    }

    private final Set<IncomingChatMessageListener> incomingListeners = new CopyOnWriteArraySet<>();

    public boolean isPassenger_agreed() {
        return passenger_agreed;
    }

    public void setPassenger_agreed(boolean passenger_agreed) {
        this.passenger_agreed = passenger_agreed;
    }

    public boolean isEndCourse() {
        return endCourse;
    }

    public void setEndCourse(boolean endCourse) {
        this.endCourse = endCourse;
    }

    public boolean isPassengerWithMe() {
        return isPassengerWithMe;
    }

    public void setPassengerWithMe(boolean passengerWithMe) {
        isPassengerWithMe = passengerWithMe;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isLoggedInAsGuest() {
        return isLoggedInAsGuest;
    }

    public static XMPPTCPConnection isConnected() {
        return connection;
    }

    public boolean isDriverWithMe() {
        return isDriverWithMe;
    }

    public void setDriverWithMe(boolean driverWithMe) {
        isDriverWithMe = driverWithMe;
    }

    public boolean isEndCourseAgreed() {
        return endCourseAgreed;
    }

    public void setEndCourseAgreed(boolean endCourseAgreed) {
        this.endCourseAgreed = endCourseAgreed;
    }

    public boolean isCancelClicked() {
        return cancelClicked;
    }

    public void setCancelClicked(boolean cancelClicked) {
        this.cancelClicked = cancelClicked;
    }

    public boolean isEndCourseByDistance() {
        return endCourseByDistance;
    }

    public void setEndCourseByDistance(boolean endCourseByDistance) {
        this.endCourseByDistance = endCourseByDistance;
    }

    public JSONArray getCars() {
        return cars;
    }

    public boolean isFoundCars() {
        return foundCars;
    }

    public void setFoundCars(boolean foundCars) {
        this.foundCars = foundCars;
    }

    public int getUserHaveCars() {
        return userHaveCars;
    }

    public boolean isCarPhotoReceived()
    {
        return carPhotoReceived;
    }

    public void setCarPhotoReceived(boolean carPhotoReceived) {
        this.carPhotoReceived = carPhotoReceived;
    }

    public boolean isOutOfTime() {
        return outOfTime;
    }

    public void setOutOfTime(boolean outOfTime) {
        this.outOfTime = outOfTime;
    }

    public JSONObject getCarPhotoJSON()
    {
        return carPhotoJSON;
    }

    public static XMPPTCPConnection getConnection() {
        return connection;
    }
}
