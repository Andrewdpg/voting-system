package service;

import service.interfaces.RequestService;

public class RequestServiceImpl implements RequestService {
    @Override
    public void sendRequest(String id) {
        // TODO: each request from the batch to be sent to the workers

    }

    @Override
    public void sendBatch(String[] batch) {
        //TODO: it must send the batch to the workers (so, it means there must be a callback implemented)

    }

    @Override
    public boolean canHandle(int amount) {
        return false;
    }

}
