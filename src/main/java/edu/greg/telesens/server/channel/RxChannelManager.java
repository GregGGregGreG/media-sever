package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.network.rtp.RTPFormats;
import edu.greg.telesens.server.session.ClientSession;

import java.io.IOException;

/**
 * Created by SKulik on 13.12.2016.
 */
public interface RxChannelManager {

    void start() throws IOException;

    void stop() throws IOException;

    void register(ClientSession session, ClientChannel channel) throws IOException;

    void unregister(String sessionId) throws IOException;

    RTPFormats getRtpFormats();
}
