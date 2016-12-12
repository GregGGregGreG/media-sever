package edu.greg.telesens.server.resource;

import edu.greg.telesens.server.NamedThreadFactory;
import edu.greg.telesens.server.dsp.DspFactory;
import edu.greg.telesens.server.format.DspFactoryImpl;
import edu.greg.telesens.server.session.ClientSession;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Phoenix on 11.12.2016.
 */
@Component
public class ResourceManagerImpl implements ResourceManager, DisposableBean, InitializingBean {

    private List<ResourceWorker> workers = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService executorService;
    private DspFactory dspFactory;

    @Value("${resources.codecPoolSize:1}")
    private int poolSize;

    @Override
    public ResourceWorker createWorker(ClientSession session) {
        return null;
    }

    @Override
    public void start() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        workers.clear();
        executorService = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("resource"));
    }

    @Override
    public void stop() {
        executorService.shutdownNow();
        executorService = null;
        workers.clear();
    }

    @Override
    public void submit(ResourceWorker worker) {
        executorService.submit(worker);
    }

    @Override
    public DspFactory getDspFactory() {
//        TODO !!!!
        return null;
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        DspFactoryImpl dspFactory = new DspFactoryImpl();
        dspFactory.addAudioCodec("edu.greg.telesens.server.codec.l16.Codec");
        dspFactory.addAudioCodec("edu.greg.telesens.server.codec.g711.alaw.Codec");
        dspFactory.addAudioCodec("edu.greg.telesens.server.codec.g711.ulaw.Codec");
        this.dspFactory = dspFactory;
        start();
    }
}
