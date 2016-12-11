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
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Phoenix on 11.12.2016.
 */
public class ResourceWorkerImpl implements ResourceWorker {

    private ResourceManager parent;
    private ClientSession session;
    private Buffer buffer;

    private Track track;
    private AudioProcessor dsp;

    private long timestamp = 0;

    public ResourceWorkerImpl(ResourceManager parent) {
        this.parent = parent;
    }

    @Override
    public void handle(String sessionId) {
        parent.submit(this);
    }

    @Override
    public void init(ClientSession session) throws IOException, UnsupportedAudioFileException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        this.session = session;
        buffer = session.getBuffer();
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
        ByteFrame frame;

        frame = track.process(timestamp);
        frame.setTimestamp(timestamp);
        ShortFrame outputFrame = dsp.decode(frame);
        frame.recycle();
        ByteFrame audioFrame;
        audioFrame = dsp.encode(outputFrame);
        outputFrame.recycle();

        Packet packet = PacketMemory.allocate();
        packet.setSessionId(session.getSessionId());
        packet.setAudionFrame(audioFrame);
    }
}
