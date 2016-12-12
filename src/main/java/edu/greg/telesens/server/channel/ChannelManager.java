package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.session.ClientSession;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface ChannelManager {
    void start();
    void stop();
    ChannelWorker getFreeWorker();

    void useWorker(ChannelWorker worker, ClientSession session);
    void freeWorker(ChannelWorker worker, ClientSession session);
}
