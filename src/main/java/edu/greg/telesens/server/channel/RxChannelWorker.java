package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.format.AudioFormat;
import edu.greg.telesens.server.format.FormatFactory;
import edu.greg.telesens.server.network.rtp.RTPFormat;
import edu.greg.telesens.server.network.rtp.RTPFormats;
import edu.greg.telesens.server.network.rtp.RtpPacket;
import edu.greg.telesens.server.session.ClientSession;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SKulik on 13.12.2016.
 */
public class RxChannelWorker implements Runnable {

    private final static AudioFormat dtmf = FormatFactory.createAudioFormat("telephone-event", 8000);

    private AtomicInteger loading = new AtomicInteger(0);
    private List<String> managedSessions = new CopyOnWriteArrayList<>();
    private List<Node> nodes = new CopyOnWriteArrayList<>();

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private static final long WAIT_TIME = 10L;
    private static final long SLEEP_TIME = 10000L;
    private final Object sync = new Object();

    private RxChannelManager parent;
    private RTPFormats formats;

    public RxChannelWorker(RxChannelManager parent) {
        this.parent = parent;
        formats = parent.getRtpFormats();
    }

    @Override
    public void run() {

        while (isRunning.get()) {
            if (nodes.size() == 0) {
                synchronized (sync) {
                    try {
                        sync.wait(SLEEP_TIME);
                    } catch (InterruptedException e) {
//                        TODO
                        e.printStackTrace();
                    }
                }
            }
            for (Node node: nodes) {
                try {
                    if (node.getSelector().select() > 0) {
                        Set<SelectionKey> keys = node.getSelector().selectedKeys();
                        Iterator<SelectionKey> it = keys.iterator();

                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            if (key.isReadable()) {
                                node.getPacket().getBuffer().clear();
                                ((DatagramChannel)key.channel()).read(node.getPacket().getBuffer());
                                RTPFormat format = formats.find(node.getPacket().getPayloadType());
                                if (format != null && format.getFormat().matches(dtmf)) {
                                    node.getDtmpEventProcessor().process(node.getPacket());
                                } else {
//                                    TODO add detect voice
                                }
                            }
                            it.remove();
                        }
                    }
                } catch (IOException e) {
//                    TODO intercept exception
                }
            }
            synchronized (sync) {
                try {
                    sync.wait(WAIT_TIME);
                } catch (InterruptedException e) {
//                    TODO
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() throws IOException {
        isRunning.set(false);
        for (Node n: nodes) {
            n.getSelector().close();
        }
        nodes.clear();
    }



    public void unregister(String sessionId) throws IOException {
        if (managedSessions.contains(sessionId)) {
            Node node = null;
            for (Node n: nodes) {
                if (n.getSession().getSessionId().equals(sessionId)) {
                    node = n;
                    break;
                }
            }
            nodes.remove(node);
            node.getSelector().close();
        }
    }


    public void register(ClientSession session, ClientChannel channel) throws IOException {
        loading.incrementAndGet();
        managedSessions.add(session.getSessionId());
        Node node = new Node();
        node.setChannel(channel);
        node.setSession(session);
        Selector selector = Selector.open();
        DatagramChannel ch = channel.getChannel();
//        int ops = ch.validOps();
//        ch.register(selector, ops, null);
        ch.register(selector, SelectionKey.OP_READ);
        node.setSelector(selector);
        node.setPacket(new RtpPacket(8192, true));
        node.setDtmpEventProcessor(new DtmfEventProcessorImpl(session.getDtmfEventListener()));
        nodes.add(node);
        synchronized (sync) {
            sync.notify();
        }
    }

    public AtomicInteger getLoading() {
        return loading;
    }

    private static class Node {
        ClientSession session;
        ClientChannel channel;
        Selector selector;
        RtpPacket packet;
        private DtmfEventProcessor dtmpEventProcessor;

        public ClientSession getSession() {
            return session;
        }

        public void setSession(ClientSession session) {
            this.session = session;
        }

        public ClientChannel getChannel() {
            return channel;
        }

        public void setChannel(ClientChannel channel) {
            this.channel = channel;
        }

        public Selector getSelector() {
            return selector;
        }

        public void setSelector(Selector selector) {
            this.selector = selector;
        }

        public RtpPacket getPacket() {
            return packet;
        }

        public void setPacket(RtpPacket packet) {
            this.packet = packet;
        }

        public void setDtmpEventProcessor(DtmfEventProcessor dtmpEventListener) {
            this.dtmpEventProcessor = dtmpEventListener;
        }

        public DtmfEventProcessor getDtmpEventProcessor() {
            return dtmpEventProcessor;
        }
    }
}
