package edu.greg.telesens.server.resource;

import edu.greg.telesens.server.dsp.DspFactory;
import edu.greg.telesens.server.session.ClientSession;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface ResourceManager {

    ResourceWorker createWorker(ClientSession session) throws ClassNotFoundException, UnsupportedAudioFileException, InstantiationException, IllegalAccessException, IOException;

    void destroyWorker(ResourceWorker worker);

    Future<?> submit(ResourceWorker worker);

    void start();

    void stop();

    DspFactory getDspFactory();
}
