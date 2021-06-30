package utility;

import entities.RequestsEntity;
import enums.RequestStatus;
import org.tinylog.Logger;

import java.util.List;

public class NewRequestsCollector extends Thread {

    private final DBConnection connection = new DBConnection();
    private final Communication communication;

    public NewRequestsCollector(Communication com) {
        communication = com;
    }

    @Override
    public void run() {
        Logger.info("Request collector is running");
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                List<RequestsEntity> list = connection.selectNewRequests();
                if (list == null || list.isEmpty()) {
                    int sleepTime = 1;
                    //Logger.trace("no new requests, sleeping for: " + sleepTime + " second");
                    //noinspection BusyWait
                    sleep((sleepTime * 1000));
                } else {
                    Logger.info("found " + list.size() + " new requests");
                    for (RequestsEntity request : list) {
                        request.setStatus(RequestStatus.WIP);
                        connection.update(request);
                        communication.searchForDriverAndAskThem(request.getId());
                    }
                }
            } catch (Exception e) {
                Logger.warn(e);
            }
        }

    }
}
