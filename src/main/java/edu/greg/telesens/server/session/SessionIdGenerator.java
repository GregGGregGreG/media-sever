package edu.greg.telesens.server.session;

/**
 * Created by SKulik on 09.12.2016.
 */
public class SessionIdGenerator {
    private static volatile long sessionCounter = 0L;
    public static synchronized String getSessionId(String sipServer, String clientAddress, String clientPort, String streamerName) {
//        return sipServer + "_" + streamerName + "_" + (sessionCounter++) + "_" + clientAddress + "_" + clientPort;
        return sipServer + "_" + streamerName + "_" + (sessionCounter++);
    }
}
