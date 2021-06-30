package utility;

import entities.QuestionsEntity;
import enums.UserType;
import org.tinylog.Logger;

import java.util.List;

public class UnansweredQuestionsCollector extends Thread {

    private final DBConnection connection = new DBConnection();
    private final Communication communication;

    public UnansweredQuestionsCollector(Communication com) {
        this.communication = com;
    }

    @Override
    public void run() {
        Logger.info("Questions collector is running");
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                List<QuestionsEntity> questions = connection.selectTerminatedQuestions();
                if (questions == null || questions.isEmpty()) {
                    int sleepTime = 1;
                    //Logger.trace("no questions found, sleeping for: " + sleepTime + " second");
                    //noinspection BusyWait
                    sleep((sleepTime * 1000));
                } else {
                    for (QuestionsEntity question : questions) {
                        question.setAnswer(false);
                        connection.update(question);
                        if (question.getRole() == UserType.D)
                            communication.searchForDriverAndAskThem(question.getRequestsId());
                    }
                    Logger.debug("closed " + questions.size() + " questions");
                }
            } catch (Exception e) {
                Logger.warn(e);
            }
        }
    }
}
