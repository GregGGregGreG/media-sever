package edu.greg.telesens.server.channel;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface ChannelManager {
    void start();
    void stop();
    ChannelWorker getFreeWorker();
}
