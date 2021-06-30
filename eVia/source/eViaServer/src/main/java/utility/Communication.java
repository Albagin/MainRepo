package utility;

import entities.*;
import enums.DriverStatus;
import enums.RequestStatus;
import enums.UserType;
import org.jivesoftware.smack.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class Communication extends Thread {

    private static final String COURSE_ID = "courseId";
    private static final String SURNAME = "surname";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String QUESTION_ID = "questionId";
    private static final String MYSERVER = "@myserver";
    private static final String SEND_MESSAGE_SUCCESS = "Message successfully sent:\n\t";
    private static final String SENDING_MESSAGE_TO = "Sending message to: ";
    private static final String NEWLINE_TAB_TO = "\n\tto: ";
    private static final String XMPP_LOGIN = "xmppLogin";
    private static final String TYPE = "type";
    private static final String TIME = "time";
    private static final String FB_ID = "fbId";
    private static final String NAME = "name";
    private static final String RATE = "rate";
    private static final String USERNAME = "username";
    private static final String DIRECTION = "direction";
    private static final String USER_HAVE_CARS = "userHaveCars";
    private static final String CAR_PHOTO = "carPhoto";
    private static final String PHOTO_ID = "photoId";
    private static final String PHOTO = "photo";
    private JSONObject jsonObject;
    private XMPPConnection con;
    private final DBConnection dbConnection = new DBConnection();

    private void logException(Exception e) {
        Logger.error(e);
    }

    @Override
    public void run() {
        try {
            String host = "localhost";
            int port = 5222;
            ConnectionConfiguration config = new ConnectionConfiguration(host, port);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setDebuggerEnabled(false);
            config.setSendPresence(true);
            con = new XMPPConnection(config);
            con.connect();
            con.login("server", "pass");
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
                            case "locationfromdriver":
                                resolveLocationFromDriver(jsonObject);
                                break;
                            case "requestfrompassenger":
                                resolveRequestFromPassenger(jsonObject);
                                break;
                            case "answer":
                                resolveAnswer(jsonObject);
                                break;
                            case "meeting":
                                resolveMeeting(jsonObject);
                                break;
                            case "endCourse":
                                resolveEndCourse(jsonObject);
                                break;
                            case "newUser":
                                resolveNewUser(jsonObject);
                                break;
                            case RATE:
                                resolveRate(jsonObject);
                                break;
                            case "CarsRequest":
                                sendListOfCars(jsonObject.getString("name"));
                                break;
                            case "removeCar":
                                resolveRemoveCar(jsonObject);
                                break;
                            case "editCar":
                                resolveEditCar(jsonObject);
                                break;
                            case "addCar":
                                resolveAddCar(jsonObject);
                                break;
                            case "photoEditCar":
                                resolveEditCarWithPhoto(jsonObject);
                                break;
                            case "photoAddCar":
                                resolveAddCarWithPhoto(jsonObject);
                                break;
                            case USER_HAVE_CARS:
                                sendUserHaveCars(jsonObject);
                                break;
                            case "carPhotoRequest":
                                sendCarPhotoToClient(jsonObject.getString("name"), jsonObject.getInt(PHOTO_ID));
                                break;
                            default:
                                Logger.info("Cannot recognize type of message\n\t" + jsonObject);
                        }
                    }catch (Exception e) {
                        logException(e);
                    }
                }
            };

            con.getChatManager().addChatListener((chat, b) -> chat.addMessageListener(messageListener));

            //noinspection StatementWithEmptyBody
            while(con.isConnected()) {
                //needed
            }

        } catch (XMPPException e) {
            logException(e);
        }
    }

    private void resolveAddCar(JSONObject message) {
        CarEntity car = getCarFromJson(message);
        car.setIsDefault(true);
        car.setUserId(dbConnection.selectUserIdByNickname(message.getString("name")));
        setAllCarsAsNotDefault(car.getUserId());
        dbConnection.save(car);
        sendListOfCars(message.getString("name"));
    }

    private void resolveAddCarWithPhoto(JSONObject message) {
        CarEntity car = getCarFromJson(message);
        car.setIsDefault(true);
        car.setUserId(dbConnection.selectUserIdByNickname(message.getString("name")));
        PhotoEntity photo = new PhotoEntity();
        photo.setPhoto(Base64.getDecoder().decode(jsonObject.getString(PHOTO)));
        int photoId = dbConnection.save(photo);
        car.setPhotoId(photoId);
        setAllCarsAsNotDefault(car.getUserId());
        dbConnection.save(car);
        sendListOfCars(message.getString("name"));
    }

    private void resolveEditCar(JSONObject message) {
        CarEntity car = getCarFromJson(message);
        CarEntity oldCar = dbConnection.selectCarById(car.getId());
        if (!car.equals(oldCar)) {
            setOtherCarsAsNotDefault(car);
            dbConnection.update(car);
            if (oldCar.getPhotoId() != null && car.getPhotoId() == null) {
                PhotoEntity oldPhoto = dbConnection.selectPhotoById(oldCar.getPhotoId());
                dbConnection.delete(oldPhoto);
            }
        }
        sendListOfCars(message.getString("name"));
    }

    private void resolveEditCarWithPhoto(JSONObject message) {
        CarEntity car = getCarFromJson(message);
        CarEntity oldCar = dbConnection.selectCarById(car.getId());
        PhotoEntity photo = new PhotoEntity();
        photo.setPhoto(Base64.getDecoder().decode(jsonObject.getString(PHOTO)));
        int photoId = dbConnection.save(photo);
        car.setPhotoId(photoId);
        dbConnection.update(car);
        if (oldCar.getPhotoId() != null) {
            dbConnection.update(car);
            PhotoEntity oldPhoto = dbConnection.selectPhotoById(oldCar.getPhotoId());
            dbConnection.delete(oldPhoto);
        }
        sendListOfCars(message.getString("name"));
    }

    private void resolveRemoveCar(JSONObject message) {
        CarEntity car = getCarFromJson(message);
        if (car.getIsDefault().equals(true)) {
            List<CarEntity> cars = dbConnection.selectAllCarsByUserId(car.getUserId());
            if (!cars.isEmpty()) {
                for (CarEntity c : cars) {
                    if (!c.getId().equals(car.getId())) {
                        c.setIsDefault(true);
                        dbConnection.update(c);
                        break;
                    }
                }
            }
        }
        dbConnection.delete(car);
        if (car.getPhotoId() != null) {
            dbConnection.delete(dbConnection.selectPhotoById(car.getPhotoId()));
        }
        sendListOfCars(message.getString("name"));
    }

    private void sendCarPhotoToClient(String to, int photoId) {
        JSONObject message = new JSONObject();
        message.put(TIME, new Date().getTime());
        message.put(TYPE, CAR_PHOTO);
        PhotoEntity carPhoto = dbConnection.selectPhotoById(photoId);
        message.put(CAR_PHOTO, Base64.getEncoder().encodeToString(carPhoto.getPhoto()));
        sendMessageTo(to, message);
    }

    private void sendUserHaveCars(JSONObject jsonObject) {
        int userId = dbConnection.selectUserIdByNickname(jsonObject.getString("name"));
        JSONObject message = new JSONObject();
        message.put(TYPE, USER_HAVE_CARS);
        message.put(TIME, new Date().getTime());
        message.put(USER_HAVE_CARS, dbConnection.getUserHaveCars(userId));
        sendMessageTo(jsonObject.getString("name"), message);
    }

    private CarEntity getCarFromJson(JSONObject jsonObject) {
        CarEntity car = new CarEntity();
        car.setBrand(jsonObject.getString("brand"));
        car.setColor(jsonObject.getString("color"));
        car.setIsDefault(jsonObject.getBoolean("isDefault"));
        car.setRegistration(jsonObject.getString("registration"));
        try {
            car.setId(jsonObject.getInt("id")); //that properties can not exist in json
        } catch (Exception e) {
            //
        }
        try {
            car.setUserId(jsonObject.getInt("userId"));
        } catch (Exception e) {
            //
        }
        try {
            if (jsonObject.getInt(PHOTO_ID) != 0) {
                car.setPhotoId(jsonObject.getInt(PHOTO_ID));
            }
        } catch (Exception e) {
            //
        }
        return car;
    }

    private void setOtherCarsAsNotDefault(CarEntity car) {
        if (car.getIsDefault().equals(true)) { //overwrite of default car
            List<CarEntity> cars = dbConnection.selectAllCarsByUserId(car.getUserId());
            for (CarEntity c : cars) {
                if (c.getIsDefault().equals(true) && !car.getId().equals(c.getId())) {
                    c.setIsDefault(false);
                    dbConnection.update(c);
                }
            }
        }
    }

    private void setAllCarsAsNotDefault(Integer userId) {
        List<CarEntity> cars = dbConnection.selectAllCarsByUserId(userId);
        for (CarEntity c : cars) {
            if (c.getIsDefault().equals(true)) {
                c.setIsDefault(false);
                dbConnection.update(c);
            }
        }
    }

    private void resolveRate(JSONObject message) {
        ScoresEntity score = new ScoresEntity();
        score.setScore(message.getInt(RATE));
        score.setRole(UserType.valueOf(message.getString("ratedUserMode").toUpperCase()));
        score.setUserId(dbConnection.selectUserIdByNickname(message.getString( "ratedUserNick")));
        score.setCourseId(message.getInt(COURSE_ID));
        dbConnection.addNewScore(score);
    }

    private void resolveNewUser(JSONObject message) {
        UserEntity user = new UserEntity();
        user.setLogin(message.getString("xmpplogin"));
        user.setEmail(message.getString("email"));
        user.setFbId(message.getString(FB_ID));
        user.setName(message.getString("firstName"));
        user.setSurname(message.getString(SURNAME));
        dbConnection.save(user);
    }

    private void resolveEndCourse(JSONObject message){
        CourseEntity course = dbConnection.selectCourseById(message.getInt(COURSE_ID));
        if (course != null) {
            course.setLastPointX(message.getDouble(LATITUDE));
            course.setLastPointY(message.getDouble(LONGITUDE));
            course.setLastTime(new Date(message.getLong(TIME)));
            dbConnection.update(course);
        }
    }

    private void resolveLocationFromDriver(JSONObject message) {
        DriverPositionEntity entity = new DriverPositionEntity();
        entity.setUserId(dbConnection.selectUserIdByNickname(message.getString(USERNAME)));
        entity.setPointLat(message.getDouble(LATITUDE));
        entity.setPointLng(message.getDouble(LONGITUDE));
        entity.setTime(new Date(message.getLong(TIME)));
        entity.setStatus(DriverStatus.valueOf(message.getString("status").toUpperCase()));
        entity.setDirection(message.getDouble(DIRECTION));
        dbConnection.save(entity);
    }

    private void resolveRequestFromPassenger(JSONObject message) {
        RequestsEntity request = new RequestsEntity();
        request.setStatus(RequestStatus.NEW);
        request.setTime(new Date(message.getLong(TIME)));
        request.setUserId(dbConnection.selectUserIdByNickname(message.getString(USERNAME)));
        request.setStartPointX(message.getDouble(LATITUDE));
        request.setStartPointY(message.getDouble(LONGITUDE));
        request.setDestPointX(message.getDouble("dstLatitude"));
        request.setDestPointY(message.getDouble("dstLongitude"));
        request.setBearing(message.getDouble(DIRECTION));

        dbConnection.save(request);
    }

    private void resolveAnswer(JSONObject message) {
        QuestionsEntity question = dbConnection.selectQuestionById(message.getInt(QUESTION_ID));
        question.setAnswer(message.getBoolean("yesOrNo"));
        dbConnection.update(question);
        if (question.getRole().equals(UserType.P)) {
            if (question.getAnswer().equals(true)) {
                UserEntity passenger = dbConnection.selectUserById(question.getUserId());
                UserEntity driver = dbConnection.selectUserById(question.getPartnerId());
                RequestsEntity request = dbConnection.selectRequestById(question.getRequestsId());
                QuestionsEntity newQuestion = new QuestionsEntity();
                newQuestion.setRole(UserType.D);
                newQuestion.setUserId(driver.getId());
                newQuestion.setRequestsId(question.getRequestsId());
                newQuestion.setPartnerId(passenger.getId());
                Integer questionId = dbConnection.save(newQuestion);
                sendQuestionToDriver(driver.getLogin(), passenger, questionId, request);
            } else if (question.getAnswer().equals(false)) {
                searchForDriverAndAskThem(question.getRequestsId());
            }
        } else {
            if (question.getAnswer().equals(false)) {
                searchForDriverAndAskThem(question.getRequestsId());
            }
        }
    }

    private void resolveMeeting(JSONObject message) {
        Logger.debug("Creating course");
        CourseEntity course = new CourseEntity();
        course.setStartPointX(message.getDouble(LATITUDE));
        course.setStartPointY(message.getDouble(LONGITUDE));
        course.setStartTime(new Date(message.getLong(TIME)));
        Integer courseId = dbConnection.save(course);
        PathEntity path = new PathEntity();
        path.setUserIdDriver(dbConnection.selectUserIdByNickname(message.getString("driverName")));
        path.setUserIdPassenger(dbConnection.selectUserIdByNickname(message.getString("passengerName")));
        path.setCourseId(courseId);
        dbConnection.save(path);
        Logger.debug("meeting");
        UserEntity passenger = dbConnection.selectUserByNickname(message.getString("passengerName"));
        UserEntity driver = dbConnection.selectUserByNickname(message.getString("driverName"));
        sendCourseInfo(courseId, passenger, driver);
    }

    private void sendQuestionToPassenger(String to, UserEntity driver, Integer questionId) {
        /*
         * this sends a message to someone
         * @param to the xmpp-account who receives the message, the destination
         * @param message: yeah, the text I'm sending...
         */

        JSONObject message = new JSONObject();
        message.put(TYPE, "question");
        message.put(TIME, new Date().getTime());
        message.put(QUESTION_ID, questionId);
        message.put(FB_ID, driver.getFbId());
        message.put(NAME, driver.getName());
        message.put(XMPP_LOGIN, driver.getLogin());
        message.put("driver_found", "true");
        if (driver.getOcenaKierowca() == null) {
            message.put(RATE, 0.);
        }
        else {
            message.put(RATE, driver.getOcenaKierowca());
        }
        CarEntity car = dbConnection.selectDefaultCarOfUser(driver.getId());
        message.put("color", car.getColor());
        message.put("brand", car.getBrand());
        String registration = car.getRegistration();
        int length = registration.length();
        char[] reg = registration.toCharArray();
        reg[length - 2] = '*';
        reg[length - 3] = '*';
        registration = new String(reg);
        message.put("registration", registration);
        if (car.getPhotoId() != null) {
            PhotoEntity photo = dbConnection.selectPhotoById(car.getPhotoId());
            message.put(CAR_PHOTO, Base64.getEncoder().encodeToString(photo.getPhoto()));
        }
        sendMessageTo(to, message);
    }

    private void sendQuestionToDriver(String to, UserEntity passenger, Integer questionId, RequestsEntity request){
        /*
         * this sends a message to someone
         * @param to the xmpp-account who receives the message, the destination
         * @param message: yeah, the text I'm sending...
         */

        JSONObject message = new JSONObject();
        message.put(TYPE, "question");
        message.put(LATITUDE, request.getDestPointX());
        message.put(LONGITUDE, request.getDestPointY());
        message.put(TIME, new Date().getTime());
        message.put(QUESTION_ID, questionId);
        message.put(FB_ID, passenger.getFbId());
        message.put(NAME, passenger.getName());
        message.put(XMPP_LOGIN, passenger.getLogin());
        message.put("passenger_found", "true");
        if (passenger.getOcenaPasazer() == null) {
            message.put(RATE, 0.);
        }
        else {
            message.put(RATE, passenger.getOcenaPasazer());
        }
        sendMessageTo(to, message);
    }

    private void sendJakDojadeQuestion(String to) {
        Logger.debug("Sending info about lack of driver: " + to);
        JSONObject message = new JSONObject();
        message.put(TYPE, "jakDojadeQuestion");
        message.put(TIME, new Date().getTime());
        sendMessageTo(to, message);
    }

    private void sendCourseId(UserEntity user, int courseId) {
        JSONObject message = new JSONObject();
        message.put(TYPE, COURSE_ID);
        message.put(TIME, new Date().getTime());
        message.put(COURSE_ID, courseId);
        message.put(NAME, user.getName());
        sendMessageTo(user.getLogin(), message);
    }

    private void sendCourseInfo(int courseId, UserEntity passenger, UserEntity driver) {
        sendCourseId(passenger, courseId);
        sendCourseId(driver, courseId);
    }

    private void sendListOfCars(String to) {
        List<CarEntity> cars = dbConnection.selectAllCarsByUserId(dbConnection.selectUserIdByNickname(to));
        JSONObject message = new JSONObject();
        JSONArray array = new JSONArray(cars);
        message.put(TYPE, "CarsList");
        message.put(TIME, new Date().getTime());
        message.put("CarsList", array);
        sendMessageTo(to, message);
    }

    private void sendMessageTo(String to, JSONObject message) {
        if (!to.contains(MYSERVER)) {
            to += MYSERVER;
        }
        Logger.debug(SENDING_MESSAGE_TO + to);
        ChatManager chatmanager = con.getChatManager();
        Chat newChat = chatmanager.createChat(to, (chat, m) -> {
            //
        });
        try {
            Logger.debug(SENDING_MESSAGE_TO + to + "\n\t" + message);
            newChat.sendMessage(message.toString());
            Logger.info(SEND_MESSAGE_SUCCESS + message + NEWLINE_TAB_TO + to);
        } catch (XMPPException e) {
            logException(e);
        }
    }

    public void searchForDriverAndAskThem(Integer requestId) {
        RequestsEntity request = dbConnection.selectRequestById(requestId);
        String passengerNickname = dbConnection.selectUserNicknameById(request.getUserId());
        Date time = new Date();
        time.setTime(time.getTime() - 2 * 60 * 1000);
        if (request.getTime().after(time)) {
            UserEntity driver = dbConnection.resolveRequest(request);
            if (driver == null) {
                request.setStatus(RequestStatus.CLOSED);
                Logger.debug("driver not found, request closed\n\t" + request);
                dbConnection.update(request);
                sendJakDojadeQuestion(passengerNickname);
            } else {
                Logger.debug("driver for request found:\n\t" + request + "\n\t" + driver);
                QuestionsEntity newQuestion = new QuestionsEntity();
                newQuestion.setRole(UserType.P);
                newQuestion.setUserId(request.getUserId());
                newQuestion.setRequestsId(request.getId());
                newQuestion.setPartnerId(driver.getId());
                Integer questionId = dbConnection.save(newQuestion);
                sendQuestionToPassenger(passengerNickname, driver, questionId);
            }
        }
        else {
            request.setStatus(RequestStatus.CLOSED);
            Logger.debug("request terminated\n\t" + request);
            dbConnection.update(request);
            sendJakDojadeQuestion(passengerNickname);
        }
    }
}
