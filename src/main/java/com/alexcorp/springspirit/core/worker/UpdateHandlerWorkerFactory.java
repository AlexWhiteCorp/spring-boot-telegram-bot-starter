package com.alexcorp.springspirit.core.worker;

import com.alexcorp.springspirit.props.SpringSpiritProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateHandlerWorkerFactory {

    private final SpringSpiritProperties properties;
    private final ApplicationContext appCtx;

    private final List<UpdateHandlerWorker> workers = new ArrayList<>();

    @PostConstruct
    public void init () {
        for(int i = 1; i <= properties.getWorkers().getCount(); i++) {
            startMessageHandlerWorker();
        }
    }

    private void startMessageHandlerWorker() {
        UpdateHandlerWorker worker = appCtx.getBean(UpdateHandlerWorker.class);

        Thread workerThread = new Thread(worker);
        workerThread.start();

        workers.add(worker);
    }
}
