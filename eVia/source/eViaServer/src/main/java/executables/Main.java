package executables;

import org.tinylog.Logger;
import utility.Communication;
import utility.DBConnection;
import utility.NewRequestsCollector;
import utility.UnansweredQuestionsCollector;

public class Main {
    static Communication communication = null;
    static DBConnection connection = null;

    private static void connectXMPP() {
        if (communication == null) {
            connectDB();
            communication = new Communication();
        }
    }

    private static void runXMPP() {
        connectXMPP();
        communication.start();
    }

    private static void runCollectors() {
        connectDB();
        NewRequestsCollector requestCollector = new NewRequestsCollector(communication);
        UnansweredQuestionsCollector questionsCollector = new UnansweredQuestionsCollector(communication);
        requestCollector.start();
        questionsCollector.start();
    }

    private static void connectDB() {
        if (connection == null) {
            connection = new DBConnection();
        }
    }

    public static void main(final String[] args) {
        Logger.info("Server is running");
        connectDB();
        Logger.info("Connected to DB");
        runXMPP();
        runCollectors();
    }
}
