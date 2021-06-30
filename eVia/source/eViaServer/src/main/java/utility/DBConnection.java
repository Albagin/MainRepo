package utility;

import entities.*;
import enums.DriverStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.tinylog.Logger;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class DBConnection {
    public static final String PHOTO = "photo";
    final Boolean debug = false;

    public static final String SAVING = "saving";
    public static final String SAVED = "saved";
    public static final String WITH_ID = "with id =";
    public static final String UPDATING = "updating";
    public static final String UPDATED = "updated";
    public static final String DELETING = "deleting";
    public static final String DELETED = "deleted";
    public static final String FOUND = "found";
    public static final String LOOKING_FOR = "looking for";
    public static final String REQUEST = "request";
    public static final String CAR = "car";
    public static final String COURSE = "course";
    public static final String POSITION = "position";
    public static final String PATH = "path";
    public static final String RATE = "rate";
    public static final String USER = "user";
    public static final String QUESTION = "question";
    public static final String ALL = "all";
    public static final String DRIVER = "driver";
    public static final String TO = "to";
    public static final String NOT = "not";
    public static final String FOR = "for";
    public static final String EXPIRED = "expired";
    public static final String NEW = "new";
    public static final String PLURAL_SUFFIX = "s";
    public static final String ID = "id";
    public static final String OF = "of";
    public static final String NICKNAME = "nickname";
    public static final String DEFAULT = "default";
    private static SessionFactory ourSessionFactory;

    static {
        //noinspection ConstantConditions
        if (ourSessionFactory == null) {
            ourSessionFactory = new Configuration().
                    configure("hibernate.cfg.xml").
                    buildSessionFactory();
        }
    }

    public static Session getSession() {
        return ourSessionFactory.openSession();
    }

    public Integer save (Entity entity) {
        Logger.trace(new LogMessageBuilder(new Object[]{SAVING, entity.getClass()}));
        Integer id;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            id = (Integer) session.save(entity);
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{SAVED, entity.getClass(), WITH_ID, id}));
        return id;
    }

    public void update(Entity entity) {
        Logger.trace(new LogMessageBuilder(new Object[]{UPDATING, entity.getClass(), WITH_ID, entity.getId()}));
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{UPDATED, entity.getClass(), WITH_ID, entity.getId()}));
    }

    public void delete(Entity entity) {
        Logger.trace(new LogMessageBuilder(new Object[]{DELETING, entity.getClass(), WITH_ID, entity.getId()}));
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{DELETED, entity.getClass(), WITH_ID, entity.getId()}));
    }


    public void addNewScore(ScoresEntity score) {
        UserEntity user = selectUserById(score.getUserId());
        user.dodajOcene(score);
        update(user);
        save(score);
    }


    public List<QuestionsEntity> selectAllQuestions() {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, ALL, QUESTION + PLURAL_SUFFIX}));
        List<QuestionsEntity> questions;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("from QuestionsEntity");
            questions = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, questions.size(), QUESTION + PLURAL_SUFFIX}));
        return questions;
    }

    public List<ScoresEntity> selectAllScores() {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, ALL, RATE + PLURAL_SUFFIX}));
        List<ScoresEntity> rates;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("from ScoresEntity ");
            rates = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, rates.size(), RATE + PLURAL_SUFFIX}));
        return rates;
    }

    public List<RequestsEntity> selectAllRequests() {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, ALL, REQUEST + PLURAL_SUFFIX}));
        List<RequestsEntity> requests;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("from RequestsEntity");
            requests = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, requests.size(), REQUEST + PLURAL_SUFFIX}));
        return requests;
    }

    public List<DriverPositionEntity> selectAllPositions() {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, ALL, POSITION + PLURAL_SUFFIX}));
        List<DriverPositionEntity> positions;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("from DriverPositionEntity");
            positions = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, positions.size(), POSITION + PLURAL_SUFFIX}));
        return positions;
    }

    public List<PathEntity> selectAllPaths() {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, ALL, PATH + PLURAL_SUFFIX}));
        List<PathEntity> paths;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("from PathEntity");
            paths = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, paths.size(), PATH + PLURAL_SUFFIX}));
        return paths;
    }

    public List<CourseEntity> selectAllCourses() {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, ALL, COURSE + PLURAL_SUFFIX}));
        List<CourseEntity> courses;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("from CourseEntity");
            courses = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, courses.size(), COURSE + PLURAL_SUFFIX}));
        return courses;
    }


    public List<DriverPositionEntity> selectCurrentDrivers(RequestsEntity request) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, DRIVER + PLURAL_SUFFIX, TO, REQUEST, WITH_ID, request.getId()}));
        List<DriverPositionEntity> positions;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query;
            double angle = 50.0;
            double direction = request.getBearing();

            String firstPart = "SELECT * FROM driverPosition d1 WHERE DATE(Time) = DATE(NOW()) AND CURRENT_TIME() - TIME(Time)  BETWEEN 0 AND 60\n" +
                    "AND Status = 'FREE'\n" +
                    "AND TIME(Time) IN (SELECT MAX(Time) FROM driverPosition d2 WHERE d1.user_id = d2.user_ID)\n" +
                    "AND user_ID != :passengerId\n" +
                    "AND user_ID NOT IN (SELECT q.user_ID FROM questions q LEFT JOIN requests r ON q.requests_ID = r.ID WHERE r.user_ID = :passengerId AND r.Time IN \n" +
                    "            (SELECT MAX(Time) FROM requests WHERE user_ID = :passengerId)) \n" +
                    "AND user_ID NOT IN (SELECT user_ID FROM questions WHERE Answer IS NULL)\n" +
                    "AND user_ID IN (SELECT user_ID FROM car WHERE car.user_ID = user_ID)\n" +
                    "AND user_ID NOT IN (SELECT partner_ID FROM questions WHERE Answer IS NULL)\n";

            String lastPart = "AND user_ID NOT IN (SELECT partner_ID FROM questions WHERE user_ID = :passengerId\n" +
                    "AND requests_ID IN \n" +
                    "        (SELECT ID FROM requests WHERE Time IN \n" +
                    "            (SELECT MAX(Time) FROM requests WHERE user_ID = :passengerId)));";

            String squarePart = "AND (PointLat BETWEEN :lesserLat AND :greaterLat) \n" +
                    "AND (PointLng BETWEEN :lesserLng AND :greaterLng) \n";

            String simpleDirectionPart = "AND direction BETWEEN :lesserDirection AND :greaterDirection \n";

            String lesserDirectionPart = "AND (direction BETWEEN :lesserDirection AND 360\n" +
                    "OR direction < :greaterDirection) \n";

            String greaterDirectionPart =  "AND (direction BETWEEN 0 AND :greaterDirection \n" +
                    "OR direction > :lesserDirection)\n";

            if (debug.equals(true)) {
                query = session.createSQLQuery(firstPart + lastPart);
            }
            else {
                double lesserDirection;
                double greaterDirection;
                if (direction - angle/2 < 0) {
                    Logger.info("lesser query");
                    query = session.createSQLQuery(firstPart + squarePart + lesserDirectionPart + lastPart);
                    lesserDirection = direction + 360 - angle/2;
                    greaterDirection = direction + angle/2;
                }
                else if (direction + angle/2 > 360) {
                    Logger.info("greater query");
                    query = session.createSQLQuery(firstPart + squarePart + greaterDirectionPart + lastPart);
                    lesserDirection = direction - angle/2;
                    greaterDirection = direction - 360 + angle/2;
                }
                else {
                    Logger.info("simple query");
                    query = session.createSQLQuery(firstPart + squarePart + simpleDirectionPart + lastPart);
                    lesserDirection = direction - angle/2;
                    greaterDirection = direction + angle/2;
                }
                double[] tab = getSquare(request.getStartPointX(), request.getStartPointY());
                if (tab[0] < tab[1]) {
                    double tmp = tab[1];
                    tab[1] = tab[0];
                    tab[0] = tmp;
                }
                if (tab[2] < tab[3]) {
                    double tmp = tab[3];
                    tab[3] = tab[2];
                    tab[2] = tmp;
                }
                Logger.trace(Arrays.toString(tab));
                query.setParameter("lesserDirection", lesserDirection);
                query.setParameter("greaterDirection", greaterDirection);
                query.setParameter("lesserLat", tab[3]);
                query.setParameter("greaterLat", tab[2]);
                query.setParameter("lesserLng", tab[1]);
                query.setParameter("greaterLng", tab[0]);
            }

            query.setParameter("passengerId", request.getUserId());


            List<Object[]> rows = query.getResultList();
            positions = new ArrayList<>();
            for (Object[] row : rows) {
                DriverPositionEntity entity = new DriverPositionEntity();
                entity.setId((Integer) row[0]);
                entity.setPointLat((Double) row[1]);
                entity.setPointLng((Double) row[2]);
                entity.setTime((Date) row[3]);
                entity.setStatus(DriverStatus.valueOf((String) row[4]));
                entity.setUserId((Integer) row[5]);
                entity.setDirection((Double) row[6]);
                positions.add(entity);
            }

            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, positions.size(), DRIVER + PLURAL_SUFFIX, TO, REQUEST, WITH_ID, request.getId()}));
        for (DriverPositionEntity e : positions) {
            Logger.trace(new LogMessageBuilder(new Object[]{FOUND, USER, WITH_ID, e.getUserId(), TO, REQUEST, WITH_ID, request.getId()}));
        }
        return positions;
    }

    public UserEntity resolveRequest(RequestsEntity request) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, DRIVER, FOR, REQUEST, WITH_ID, request.getId()}));
        List<DriverPositionEntity> drivers = selectCurrentDrivers(request);
        UserEntity driver = null;
        if (!drivers.isEmpty()) {
            if (debug.equals(true)) {
                driver = selectUserById(drivers.get(0).getUserId());
            }
            else {
                JsonReader reader = new JsonReader();
                for (DriverPositionEntity position : drivers) {
                    Integer time = reader.getValueETA(request.getStartPointX(), request.getStartPointY(), position.getPointLat(), position.getPointLng());
                    if (time >= 5 * 60) {
                        driver = selectUserById(position.getUserId());
                    }
                }
            }
        }

        if (driver == null) {
            Logger.trace(new LogMessageBuilder(new Object[]{NOT, FOUND, DRIVER, TO, REQUEST, WITH_ID, request.getId()}));

        } else {
            Logger.trace(new LogMessageBuilder(new Object[]{FOUND, DRIVER, WITH_ID, driver.getId(), TO, REQUEST, WITH_ID, request.getId()}));
        }
        return driver;
    }


    public List<QuestionsEntity> selectTerminatedQuestions () {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, EXPIRED, QUESTION + PLURAL_SUFFIX}));
        List<QuestionsEntity> questions;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Date time = new Date();
            time.setTime(time.getTime() - 1000 * 30);
            Query query = session.createQuery("select question from QuestionsEntity question where question.time<:date and answer=null");
            query.setParameter("date", time);
            questions = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, questions.size(), EXPIRED, QUESTION + PLURAL_SUFFIX}));
        return questions;
    }

    public List<RequestsEntity> selectNewRequests() {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, NEW, REQUEST + PLURAL_SUFFIX}));
        List<RequestsEntity> list;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select request from RequestsEntity request where request.Status='NEW'");
            list = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, list.size(), NEW, REQUEST + PLURAL_SUFFIX}));
        return list;
    }


    public Integer selectUserIdByNickname(String nickname) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, ID, OF, USER, "with nickname =", nickname}));
        Integer id;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("from UserEntity where login=:nickname");
            query.setParameter(NICKNAME, nickname);
            UserEntity entity = (UserEntity) query.getSingleResult();
            id = entity.getId();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, ID, "=", id, OF, USER, "with nickname =", nickname}));
        return id;
    }

    public String selectUserNicknameById(Integer id) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, NICKNAME, OF, USER, WITH_ID, id}));
        String  result = selectUserById(id).getLogin();
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, "nickname =", result, OF, USER, WITH_ID, id}));
        return result;
    }


    public RequestsEntity selectRequestById(Integer id) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, REQUEST, WITH_ID, id}));
        RequestsEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select request from RequestsEntity request where request.id=:id");
            query.setParameter(ID, id);
            result = (RequestsEntity) query.getSingleResult();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, REQUEST, WITH_ID, id}));
        return result;
    }

    public PhotoEntity selectPhotoById(Integer id) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, PHOTO, WITH_ID, id}));
        PhotoEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select photo from PhotoEntity photo where photo.id=:id");
            query.setParameter(ID, id);
            result = (PhotoEntity) query.getSingleResult();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, PHOTO, WITH_ID, id}));
        return result;
    }

    public CarEntity selectCarById(Integer id) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, CAR, WITH_ID, id}));
        CarEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select car from CarEntity car where car.id=:id");
            query.setParameter(ID, id);
            result = (CarEntity) query.getSingleResult();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, CAR, WITH_ID, id}));
        return result;
    }

    public QuestionsEntity selectQuestionById(Integer id) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, QUESTION, WITH_ID, id}));
        QuestionsEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select question from QuestionsEntity question where question.id=:id");
            query.setParameter(ID, id);
            result = (QuestionsEntity) query.getSingleResult();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, QUESTION, WITH_ID, id}));
        return result;
    }

    public UserEntity selectUserById(Integer id) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, USER, WITH_ID, id}));
        UserEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select user from UserEntity user where user.id=:id");
            query.setParameter(ID, id);
            result = (UserEntity) query.getSingleResult();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, USER, WITH_ID, id}));
        return result;
    }

    public CarEntity selectCarByPhotoId(Integer photoId) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, CAR, "with photoId", photoId}));
        CarEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select car from CarEntity car where car.photoId=:id");
            query.setParameter(ID, photoId);
            result = (CarEntity) query.getSingleResult();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, CAR, "with photoId", photoId}));
        return result;
    }

    public UserEntity selectUserByNickname(String nickname) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, USER, "'s name", "with nickname", nickname}));
        UserEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select user from UserEntity user where user.login=:nickname");
            query.setParameter(NICKNAME, nickname);
            result = (UserEntity) query.getSingleResult();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, USER, "'s name =", result, "with nickname", nickname}));
        return result;
    }

    public CourseEntity selectCourseById(Integer id) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, COURSE, WITH_ID, id}));
        CourseEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select course from CourseEntity course where course.id=:id");
            query.setParameter(ID, id);
            result = (CourseEntity) query.getSingleResult();
            tx.commit();
        } catch (NoResultException e) {
            Logger.trace(new LogMessageBuilder(new Object[]{FOUND, COURSE, WITH_ID, id}));
            return null;
        }
        Logger.trace(new LogMessageBuilder(new Object[]{NOT,FOUND, COURSE, WITH_ID, id}));
        return result;
    }

    public CarEntity selectDefaultCarOfUser(Integer userId) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, DEFAULT, CAR, OF, USER, WITH_ID, userId}));
        CarEntity result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select car from CarEntity car where car.userId=:userId and car.isDefault=true");
            query.setParameter("userId", userId);
            result = (CarEntity) query.getSingleResult();
            tx.commit();
        } catch (NoResultException e) {
            Logger.trace(new LogMessageBuilder(new Object[]{FOUND, DEFAULT, CAR, OF, USER, WITH_ID, userId}));
            return null;
        }
        Logger.trace(new LogMessageBuilder(new Object[]{NOT,FOUND, DEFAULT, CAR, OF, USER, WITH_ID, userId}));
        return result;
    }

    public List<CarEntity> selectAllCarsByUserId(Integer userId) {
        Logger.trace(new LogMessageBuilder(new Object[]{LOOKING_FOR, CAR + PLURAL_SUFFIX, "with userId", userId}));
        List<CarEntity> cars;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select car from CarEntity car where car.userId=:userId");
            query.setParameter("userId", userId);
            cars = query.getResultList();
            tx.commit();
        }
        Logger.trace(new LogMessageBuilder(new Object[]{FOUND, cars.size(), CAR + PLURAL_SUFFIX, "with userId", userId}));
        return cars;
    }

    private Long getCarCountOfUser(Integer userId) {
        Long result;
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("select count(car) from CarEntity car where car.userId = :id");
            query.setParameter("id", userId);
            result = (Long) query.getSingleResult();
            tx.commit();
        }
        return result;
    }

    public Boolean getUserHaveCars(Integer userId) {
        return getCarCountOfUser(userId) > 0;
    }

    public double [] getSquare(double coordX, double coordY) {
        double accuracy = 1; //in kilometers
        double[] tab = new double[4];
        double xToKM = coordX * 111.32; //X in kilometers
        double yToKM = coordY * 40075 * Math.cos(coordX) / 360;   //Y in kilometers
        tab[0] = (xToKM + accuracy)/111.32;    //X+1
        tab[1] = (xToKM - accuracy)/111.32;    //X-1
        tab[2] = (yToKM + accuracy) / (40075 * Math.cos(coordX) / 360); //Y+1
        tab[3] = (yToKM - accuracy) / (40075 * Math.cos(coordX) / 360); //Y-1
        return tab;
    }
}

