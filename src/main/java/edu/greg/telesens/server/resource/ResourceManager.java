package edu.greg.telesens.server.resource;

import edu.greg.telesens.server.dsp.DspFactory;
import edu.greg.telesens.server.session.ClientSession;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface ResourceManager {
    ResourceWorker createWorker(ClientSession session) throws ClassNotFoundException, UnsupportedAudioFileException, InstantiationException, IllegalAccessException, IOException;

    void freeWorker(ResourceWorker worker);

    void start();
    void stop();

    void submit(ResourceWorker worker);

    DspFactory getDspFactory();
}
