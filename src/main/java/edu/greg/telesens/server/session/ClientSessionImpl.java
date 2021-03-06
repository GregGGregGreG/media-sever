package edu.greg.telesens.server.session;

import edu.greg.telesens.server.buffer.Buffer;
import edu.greg.telesens.server.channel.*;
import edu.greg.telesens.server.format.AudioFormat;
import edu.greg.telesens.server.format.Format;
import edu.greg.telesens.server.memory.Packet;
import edu.greg.telesens.server.network.rtp.RTPFormat;
import edu.greg.telesens.server.network.rtp.RtpPacket;
import edu.greg.telesens.server.resource.ResourceWorker;
import lombok.Getter;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Created by SKulik on 12.12.2016.
 */
public class ClientSessionImpl implements ClientSession, DtmfEventListener {

    private ResourceWorker resource;
    private Buffer buffer;
    private String melodyPath;
    private AudioFormat destinationFormat;
    private String sessionId;
    private int repeat;
    private SessionRegistry registry;

    private String sipServer;
    private RTPFormat fmt;

    @Getter
    private ClientChannel channel;

    private ChannelWorker channelWorker;

    private RxChannelManager rxChannelManager;

    private RtpPacket rtpPacket = new RtpPacket(8192, true);

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
        channel.bind();
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
    public RtpPacket wrap(Packet packet, long currentTime, int sequence, long startTime) {
        if (fmt == null || !fmt.getFormat().matches(packet.getAudioFrame().getFormat())) {
            fmt = registry.getRtpFormats().getRTPFormat(packet.getAudioFrame().getFormat());
        }
//            log.debug("Send --> {}", Arrays.toString(audioFrame.getData()));

        rtpPacket.wrap(false, fmt.getID(), sequence, packet.getAudioFrame().getTimestamp(),
                startTime, packet.getAudioFrame().getData(), packet.getAudioFrame().getOffset(), packet.getAudioFrame().getLength());


        return rtpPacket;
    }

    @Override
    public void play() {
        try {
            rxChannelManager = registry.getRxChannelManager();
            rxChannelManager.register(this, channel);

            channelWorker = registry.getChannelManager().getFreeWorker();
            channelWorker.playSession(this, buffer, channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            registry.getRxChannelManager().unregister(sessionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (channelWorker != null) {
            channelWorker.stopSession(this);
        }
        buffer.cleanup();
        registry.getResourceManager().destroyWorker(resource);
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public DtmfEventListener getDtmfEventListener() {
        return this;
    }

    @Override
    public void keyDetected(byte key) {
        registry.getSipServerConnector().sendDtmfDetected(sipServer, sessionId, key);
    }

}
