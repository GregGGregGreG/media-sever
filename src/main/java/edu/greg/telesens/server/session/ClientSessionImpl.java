package edu.greg.telesens.server.session;

import edu.greg.telesens.server.buffer.Buffer;
import edu.greg.telesens.server.channel.ChannelWorker;
import edu.greg.telesens.server.channel.ClientChannel;
import edu.greg.telesens.server.channel.ClientChannelImpl;
import edu.greg.telesens.server.format.AudioFormat;
import edu.greg.telesens.server.format.Format;
import edu.greg.telesens.server.memory.Packet;
import edu.greg.telesens.server.network.rtp.RtpPacket;
import edu.greg.telesens.server.resource.ResourceWorker;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by SKulik on 12.12.2016.
 */
public class ClientSessionImpl implements ClientSession {

    private ResourceWorker resource;
    private Buffer buffer;
    private String melodyPath;
    private AudioFormat destinationFormat;
    private String sessionId;
    private int repeat;
    private SessionRegistry registry;

    private String sipServer;

    private ClientChannel channel;

    private ChannelWorker channelWorker;



    public ClientSessionImpl(SessionRegistryImpl sessionRegistry, String sipServer, String clientAddress, int clientPort, String serverAddress, int serverPort, String melodyUrl, AudioFormat format, int repeat) throws ClassNotFoundException, UnsupportedAudioFileException, InstantiationException, IOException, IllegalAccessException {
        this.registry = sessionRegistry;
        this.repeat = repeat;
        this.destinationFormat = format;
        this.melodyPath = melodyUrl;
        this.sipServer = sipServer;
        sessionId = SessionIdGenerator.getSessionId(sipServer, clientAddress, clientPort, sessionRegistry.getName());
        channel = new ClientChannelImpl(serverAddress, serverPort, clientAddress, clientPort);
        buffer = sessionRegistry.getBufferManager().createBuffer();
        resource = sessionRegistry.getResourceManager().createWorker(this);
    }

    @Override
    public Buffer getBuffer() {
        return buffer;
    }



    @Override
    public String getMelodyPath() {
        return melodyPath;
    }

    @Override
    public Format getDestinationFormat() {
        return destinationFormat;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public RtpPacket wrap(Packet packet, long currentTime) {
        return registry.wrap(this, packet, currentTime);
    }

    @Override
    public void play() {
        channelWorker = registry.getChannelManager().getFreeWorker();
        channelWorker.playSession(this, buffer, channel);
    }

    @Override
    public void stop() {
        if (channelWorker != null) {
            channelWorker.stopSession(this);
        }
        buffer.cleanup();
        registry.getResourceManager().freeWorker(resource);
    }
}
