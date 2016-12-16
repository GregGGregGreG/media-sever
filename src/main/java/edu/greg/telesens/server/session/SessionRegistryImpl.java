package edu.greg.telesens.server.session;

import edu.greg.telesens.server.buffer.BufferManager;
import edu.greg.telesens.server.channel.ChannelManager;
import edu.greg.telesens.server.channel.RxChannelManager;
import edu.greg.telesens.server.format.AudioFormat;
import edu.greg.telesens.server.format.FormatFactory;
import edu.greg.telesens.server.network.rtp.RTPFormat;
import edu.greg.telesens.server.network.rtp.RTPFormats;
import edu.greg.telesens.server.resource.ResourceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by SKulik on 12.12.2016.
 */
@Slf4j
@Component
public class SessionRegistryImpl implements SessionRegistry, InitializingBean {

    private List<ClientSession> sessions = new CopyOnWriteArrayList<>();

    @Value("${server.address:localhost}")
    private String serverAddress;

    @Value("${session.lowestPort:10000}")
    private int lowestPort;
    @Value("${session.highestPort:19000}")
    private int highestPort;

    private PortHolder portHolder;

    @Value("${session.name:streamer}")
    private String name;

    @Autowired
    private ResourceManager resourceManager;

    @Autowired
    private BufferManager bufferManager;

    @Autowired
    private ChannelManager channelManager;

    //Media stream format
    private RTPFormats rtpFormats = new RTPFormats();

    @Autowired
    private RxChannelManager rxChannelManager;

    @Override
    public ClientSession register(String sipServer, String clientAddress, int clientPort, String melodyUrl, AudioFormat format, int repeat) throws Exception {
        ClientSessionImpl session = new ClientSessionImpl(this, sipServer, clientAddress, clientPort, serverAddress, portHolder.getPort(), melodyUrl, format, repeat);
        sessions.add(session);
        return session;
    }

    @Override
    public void play(String sessionId) {
        ClientSession session = null;
        for (ClientSession s : sessions) {
            if (s.getSessionId().equals(sessionId)) {
                session = s;
            }
        }
        if (session != null) {
            session.play();
        }
    }

    @Override
    public void stop(String sessionId) {
        ClientSession session = null;
        for (ClientSession s : sessions) {
            if (s.getSessionId().equals(sessionId)) {
                session = s;
                break;
            }
        }
        if (Objects.nonNull(session)) {
            sessions.remove(session);
            session.stop();
            portHolder.freePort(session.getChannel().getSrcSockAddr().getPort());
        }
    }

    @Override
    public Set<String> getSessionIds() {
        Set<String> sessionIds = new HashSet<>();
        for (ClientSession session : sessions) {
            sessionIds.add(session.getSessionId());
        }
        return sessionIds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.portHolder = new PortHolder(lowestPort, highestPort);
        RTPFormat pcmu = new RTPFormat(0, FormatFactory.createAudioFormat("pcmu", 8000, 8, 1), 8000);
        RTPFormat pcma = new RTPFormat(8, FormatFactory.createAudioFormat("pcma", 8000, 8, 1), 8000);
        rtpFormats.add(pcmu);
        rtpFormats.add(pcma);
    }

    public String getName() {
        return name;
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public BufferManager getBufferManager() {
        return bufferManager;
    }

    @Override
    public ChannelManager getChannelManager() {
        return channelManager;
    }

    @Override
    public RTPFormats getRtpFormats() {
        return rtpFormats;
    }

    @Override
    public SipServerConnector getSipServerConnector() {
//        TODO
        return new SipServerConnector() {
            @Override
            public void sendDtmfDetected(String sipServer, String sessionId, byte dtmf) {
                log.info("dtmf detected for server {}, session {} and key {}", sipServer, sessionId, dtmf);
            }
        };
    }

    @Override
    public RxChannelManager getRxChannelManager() {
        return rxChannelManager;
    }
}
