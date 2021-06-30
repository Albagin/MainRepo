package executables;

import entities.*;
import org.tinylog.Logger;
import utility.DBConnection;

import java.util.List;

public class Cleaner {
    private static final String QUESTIONS = " questions";
    private static final String FOUND = "Found ";
    private static final String DELETED = "Deleted ";
    private static final String REQUESTS = " requests";
    private static final String POSITIONS = " positions";
    private static final String PATHS = " paths";
    private static final String COURSES = " courses";
    private static final String RATES = " rates";
    static final DBConnection connection = new DBConnection();


    private static void deleteQuestions() {
        List<QuestionsEntity> questions = connection.selectAllQuestions();
        if (questions != null) {
            Logger.debug(FOUND + questions.size() + QUESTIONS);
            for (QuestionsEntity question : questions) {
                connection.delete(question);
            }
            Logger.info(DELETED + questions.size() + QUESTIONS);
        }
    }

    private static void deleteRequests() {
        List<RequestsEntity> requests = connection.selectAllRequests();
        if (requests != null) {
            Logger.debug(FOUND + requests.size() + REQUESTS);
            for (RequestsEntity request : requests) {
                connection.delete(request);
            }
            Logger.info(DELETED + requests.size() + REQUESTS);
        }
    }

    private static void deletePositions() {
        List<DriverPositionEntity> positions = connection.selectAllPositions();
        if (positions != null) {
            Logger.debug(FOUND + positions.size() + POSITIONS);
            for (DriverPositionEntity position : positions) {
                connection.delete(position);
            }
            Logger.info(DELETED + positions.size() + POSITIONS);
        }
    }

    private static void deletePaths() {
        List<PathEntity> paths = connection.selectAllPaths();
        if (paths != null) {
            Logger.debug(FOUND + paths.size() + PATHS);
            for (PathEntity path : paths) {
                connection.delete(path);
            }
            Logger.info(DELETED + paths.size() + PATHS);
        }
    }

    private static void deleteCourses() {
        List<CourseEntity> courses = connection.selectAllCourses();
        if (courses != null) {
            Logger.debug(FOUND + courses.size() + COURSES);
            for (CourseEntity course : courses) {
                connection.delete(course);
            }
            Logger.info(DELETED + courses.size() + COURSES);
        }
    }

    private static void deleteScores() {
        List<ScoresEntity> scores = connection.selectAllScores();
        if (scores != null) {
            Logger.debug(FOUND + scores.size() + RATES);
            for (ScoresEntity score : scores) {
                connection.delete(score);
            }
            Logger.info(DELETED + scores.size() + RATES);
        }
    }

    public static void main(String[] args) {
        Logger.info("Cleaner is running");
        deleteQuestions();
        deleteRequests();
        deletePaths();
        deleteScores();
        deleteCourses();
        deletePositions();
        Logger.info("Successful DB cleaning");
    }
}
