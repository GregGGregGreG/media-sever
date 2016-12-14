package edu.greg.telesens.server.session;

/**
 * Created by SKulik on 13.12.2016.
 */
public interface SipServerConnector {
    void sendDtmfDetected(String sipServer, String sessionId, byte dtmf);
}
