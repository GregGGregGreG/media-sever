package edu.greg.telesens.server.session;

import edu.greg.telesens.server.buffer.BufferManager;
import edu.greg.telesens.server.channel.ChannelManager;
import edu.greg.telesens.server.format.AudioFormat;
import edu.greg.telesens.server.memory.Packet;
import edu.greg.telesens.server.network.rtp.RTPFormats;
import edu.greg.telesens.server.network.rtp.RtpPacket;
import edu.greg.telesens.server.resource.ResourceManager;

import java.util.Set;

/**
 * Created by SKulik on 09.12.2016.
 */
public interface SessionRegistry {
    ClientSession register(String sipServer, String clientAddress, int clientPort, String melodyUrl, AudioFormat format, int repeat) throws Exception;
    void play(String sessionId);
    void stop(String sessionId);
    Set<String> getSessionIds();

    ResourceManager getResourceManager();

    BufferManager getBufferManager();

    ChannelManager getChannelManager();

    RTPFormats getRtpFormats();

    SipServerConnector getSipServerConnector();
}
