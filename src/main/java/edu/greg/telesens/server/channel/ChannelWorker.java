package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.buffer.Buffer;
import edu.greg.telesens.server.session.ClientSession;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface ChannelWorker {
//    start working with session
    void playSession(ClientSession session, Buffer buffer, ClientChannel channel);
//    stop working with session
    void stopSession(ClientSession session);
}
