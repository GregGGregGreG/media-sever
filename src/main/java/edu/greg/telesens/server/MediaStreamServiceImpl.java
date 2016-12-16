package edu.greg.telesens.server;

import edu.greg.telesens.server.format.AudioFormat;
import edu.greg.telesens.server.format.FormatFactory;
import edu.greg.telesens.server.session.ClientSession;
import edu.greg.telesens.server.session.SessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.telesens.o320.nrt.ms.api.*;

/**
 * Created by
 * GreG on 12/14/2016.
 */
@Slf4j
@Service
public class MediaStreamServiceImpl implements MediaStreamService {

    private final SessionRegistry sessionRegistry;

    private AudioFormat g711a = FormatFactory.createAudioFormat("pcma", 8000, 8, 1);


    public MediaStreamServiceImpl(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public CreateResponse create(CreateRequest createRequest) {
        log.info("Create request for {}:{}:{}:{} ", createRequest.getCallerHost(), createRequest.getCallerPort(), createRequest.getMelodyPath(), createRequest.getRep());

        ClientSession session = null;
        try {
            session = sessionRegistry.
                    register("streamer",
                            createRequest.getCallerHost(),
                            createRequest.getCallerPort(),
                            createRequest.getMelodyPath(),
                            g711a,
                            createRequest.getRep()
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }

        CreateResponse createResponse = new CreateResponse();
        createResponse.setSessionId(session.getSessionId());
        createResponse.setMSHost(session.getChannel().getSrcSockAddr().getAddress().getHostAddress());
        createResponse.setMSPort(session.getChannel().getSrcSockAddr().getPort());

        log.info("Created session with id  {}", session.getSessionId());
        return createResponse;
    }

    @Override
    public void play(PlayRequest playRequest) {
        log.info("Play melody for sessionId {}", playRequest.getSessionId());
        sessionRegistry.play(playRequest.getSessionId());
    }

    @Override
    public void stop(StopRequest stopRequest) {
        log.info("Stop melody for sessionId {}", stopRequest.getSessionId());
        sessionRegistry.stop(stopRequest.getSessionId());
    }
}
