package edu.greg.telesens.server.services;

import edu.greg.telesens.server.audio.Track;
import edu.greg.telesens.server.audio.wav.WavTrackImpl;
import edu.greg.telesens.server.dsp.AudioProcessor;
import edu.greg.telesens.server.format.AudioFormat;
import edu.greg.telesens.server.format.DspFactoryImpl;
import edu.greg.telesens.server.format.FormatFactory;
import edu.greg.telesens.server.memory.ByteFrame;
import edu.greg.telesens.server.memory.ShortFrame;
import edu.greg.telesens.server.network.rtp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.channels.DatagramChannel;
import java.util.ArrayDeque;
import java.util.Queue;


/**
 * Created by
 * GreG on 11/8/2016.
 */
@Slf4j
@Component
public class DefaultPlayerService implements PlayerService, ActionListener {

    private static final int FRAME_PERIOD = 20;
    public static final int PORT = 1313;
    public static final String HOSTNAME = "localhost";
    public static final byte[] UDP_MESSAGE;


    protected volatile long timestamp = 0;
    private int sn = 0;
    private final long ssrc = System.currentTimeMillis();
    private RTPFormat fmt;
    //RTP clock
    private RtpClock rtpClock = new RtpClock(new DefaultClock());
    //Media stream format
    private RTPFormats rtpFormats = new RTPFormats();

    //digital signaling processor
    private static AudioProcessor dsp;

    private Track track;

    public InetSocketAddress DST_ADDRRES;


    final Timer timer;
    private DatagramChannel rtpSocket;
    private Queue<ByteFrame> frames = new ArrayDeque<>();
    private RtpPacket rtpPacket = new RtpPacket(8192, true);

    private AudioFormat g711a = FormatFactory.createAudioFormat("pcma", 8000, 8, 1);

    private final static RTPFormat pcmu = new RTPFormat(0, FormatFactory.createAudioFormat("pcmu", 8000, 8, 1), 8000);
    private final static RTPFormat pcma = new RTPFormat(8, FormatFactory.createAudioFormat("pcma", 8000, 8, 1), 8000);


    static {
        UDP_MESSAGE = new byte[214];
        for (int i = 0; i < 214; i++) {
            UDP_MESSAGE[i] = 'S';
        }
        final DspFactoryImpl dspFactory = new DspFactoryImpl();
        dspFactory.addAudioCodec("edu.greg.telesens.server.codec.l16.Codec");
        dspFactory.addAudioCodec("edu.greg.telesens.server.codec.g711.alaw.Codec");
        dspFactory.addAudioCodec("edu.greg.telesens.server.codec.g711.ulaw.Codec");
        try {
            dsp = dspFactory.newAudioProcessor();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public DefaultPlayerService() throws IOException {
        timer = generateTimer();
        rtpSocket = generateChanel();
        rtpFormats.add(pcmu);
        rtpFormats.add(pcma);
    }

    private Timer generateTimer() {
        Timer timer;
        timer = new Timer(FRAME_PERIOD, this);
        timer.setInitialDelay(0);
        timer.setCoalesce(true);
        return timer;
    }

    private static DatagramChannel generateChanel() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        InetSocketAddress addr = new InetSocketAddress(HOSTNAME, PORT);
        DatagramSocket socket = channel.socket();
        socket.bind(addr);
        channel.configureBlocking(false);
        return channel;
    }


    @Override
    public void play(String host, int port, String melodyPath, String codec, int rep) {
        log.info("Start Play --> {}:{}:{}:{}", host, port, melodyPath, codec);
        DST_ADDRRES = new InetSocketAddress(host, port);
        try {
            track = new WavTrackImpl(new URL(melodyPath));
            dsp.setSourceFormat(track.getFormat());
            dsp.setDestinationFormat(g711a);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }


        timer.start();
    }

    @Override
    public void stop() {
        log.info("Stop Play");
        timer.stop();
        if (track != null) {
            track.close();
            track = null;
        }
        timestamp = 0;
        sn = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ByteFrame frame;

            frame = track.process(timestamp);
            frame.setTimestamp(timestamp);
            ShortFrame outputFrame = dsp.decode(frame);
            ByteFrame audioFrame;
            audioFrame = dsp.encode(outputFrame);


            if (fmt == null || !fmt.getFormat().matches(audioFrame.getFormat())) {
                fmt = rtpFormats.getRTPFormat(audioFrame.getFormat());

                //update clock rate
                rtpClock.setClockRate(fmt.getClockRate());
            }
//            log.debug("Send --> {}", Arrays.toString(audioFrame.getData()));

            rtpPacket.wrap(false, fmt.getID(), sn, timestamp,
                    ssrc, audioFrame.getData(), audioFrame.getOffset(), audioFrame.getLength());
            rtpSocket.send(rtpPacket.getBuffer(), DST_ADDRRES);
            sn++;
            timestamp = timestamp + 160;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
