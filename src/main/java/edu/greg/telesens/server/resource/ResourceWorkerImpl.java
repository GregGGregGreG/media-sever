package edu.greg.telesens.server.resource;

import edu.greg.telesens.server.audio.Track;
import edu.greg.telesens.server.audio.vox.VoxTrackImpl;
import edu.greg.telesens.server.buffer.Buffer;
import edu.greg.telesens.server.dsp.AudioProcessor;
import edu.greg.telesens.server.dsp.DspFactory;
import edu.greg.telesens.server.memory.ByteFrame;
import edu.greg.telesens.server.memory.Packet;
import edu.greg.telesens.server.memory.PacketMemory;
import edu.greg.telesens.server.memory.ShortFrame;
import edu.greg.telesens.server.session.ClientSession;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Phoenix on 11.12.2016.
 */
public class ResourceWorkerImpl implements ResourceWorker {

    private ResourceManager parent;
    private ClientSession session;
    private Buffer buffer;
    private AtomicBoolean isStarted = new AtomicBoolean(true);

    private Track track;
    private AudioProcessor dsp;
    private long packetRealTime = 0L;
    private long packetStartRealTime = 0L;

    private long timestamp = 0;
    private int packetCount = 0;

    public ResourceWorkerImpl(ResourceManager parent) {
        this.parent = parent;
    }

    @Override
    public void handle(String sessionId, int packetCount) {
        if (isStarted.get()) {
            this.packetCount = packetCount;
            parent.submit(this);
        }
    }

    @Override
    public void bufferStopped() {
        isStarted.set(false);
    }

    @Override
    public void init(ClientSession session) throws IOException, UnsupportedAudioFileException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        this.session = session;
        buffer = session.getBuffer();
        buffer.setEventHandler(this);
        initTrack();
        initDsp();
    }

    private void initTrack() throws IOException, UnsupportedAudioFileException {
        track = new VoxTrackImpl(new URL(session.getMelodyPath()));
    }

    private void initDsp() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        DspFactory factory = parent.getDspFactory();
        dsp = factory.newAudioProcessor();
        dsp.setSourceFormat(track.getFormat());
        dsp.setDestinationFormat(session.getDestinationFormat());
    }

    @Override
    public void run() {
        if (packetRealTime == 0L) {
            packetRealTime = System.currentTimeMillis();
            packetStartRealTime = packetRealTime;
        }
        for (int i = 0; i < packetCount; i++) {
            try {
                ByteFrame frame;

                frame = track.process(timestamp);
                frame.setTimestamp(timestamp);
                ShortFrame outputFrame = dsp.decode(frame);
                ByteFrame audioFrame;
                audioFrame = dsp.encode(outputFrame);

                Packet packet = PacketMemory.allocate();
                packet.setSessionId(session.getSessionId());
                packet.setAudioFrame(audioFrame);
                packet.setRealTime(packetRealTime);
                packet.setStartRealTime(packetStartRealTime);
                packetRealTime += 20L;
                timestamp += 160L;
                buffer.put(packet);
            } catch (IOException e) {
//                TODO intercept exception (maybe need to log it)
            }
        }
        buffer.resourceComplete(session.getSessionId());
    }
}
