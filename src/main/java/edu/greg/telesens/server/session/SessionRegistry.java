package edu.greg.telesens.server.session;

import edu.greg.telesens.server.format.AudioFormat;

import java.util.Set;

/**
 * Created by SKulik on 09.12.2016.
 */
public interface SessionRegistry {
    ClientSession register(String sipServer, String clientAddress, int clientPort, String melodyUrl, AudioFormat format, int repeat);
    void play(String sessionId);
    void stop(String sessionId);
    Set<String> getSessionIds();
}
