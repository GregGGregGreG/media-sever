package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.buffer.Buffer;
import edu.greg.telesens.server.memory.Packet;
import edu.greg.telesens.server.network.rtp.RtpPacket;
import edu.greg.telesens.server.session.ClientSession;
import edu.greg.telesens.server.timing.RtpToRealTiming;
import edu.greg.telesens.server.timing.RtpToRealTimingImpl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by SKulik on 12.12.2016.
 */
public class ChannelWorkerImpl implements ChannelWorker {
    private ChannelManager parent;
    private Map<String, Node> nodes = new ConcurrentHashMap<>();

    public ChannelWorkerImpl(ChannelManager parent) {
        this.parent = parent;
    }

    @Override
    public void playSession(ClientSession session, Buffer buffer, ClientChannel channel) {
        Node node = new Node(session, buffer, channel);
        Node oldNode = nodes.get(session.getSessionId());
        if (oldNode != null) {
//            TODO we need to process this situation
        }
        nodes.put(session.getSessionId(), node);
    }

    @Override
    public void stopSession(ClientSession session) {
        nodes.remove(session.getSessionId());
    }

    @Override
    public int getSessionCount() {
        return nodes.size();
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Node> node : nodes.entrySet()) {
            Node n = node.getValue();
            if (n.getSequence() == 0L) {
                n.getTiming().initTime(currentTime, TimeUnit.MILLISECONDS);
            }
            Packet packet = n.getBuffer().get(n.getSession().getSessionId());
            while (packet != null && n.getTiming().getRealTimeByRtpTime(packet.getTimestamp(), TimeUnit.MILLISECONDS) < currentTime) {
                RtpPacket rtp = n.getSession().wrap(packet, currentTime, n.getTimestamp());
                try {
                    n.getChannel().send(rtp);
                } catch (IOException e) {
//                    TODO intercept this exception
                }
                n.incTimestamp(160L);
                n.incSequence();
                packet = n.getBuffer().get(n.getSession().getSessionId());
            }
            if (packet != null) {
                n.getBuffer().rollback(packet);
            }
        }
    }

    static class Node {
        private ClientSession session;
        private Buffer buffer;
        private ClientChannel channel;
        private RtpToRealTiming timing = new RtpToRealTimingImpl();
        private long sequence = 0L;
        private long timestamp = 0L;

        public Node(ClientSession session, Buffer buffer, ClientChannel channel) {
            this.session = session;
            this.buffer = buffer;
            this.channel = channel;
        }

        public ClientSession getSession() {
            return session;
        }

        public Buffer getBuffer() {
            return buffer;
        }

        public ClientChannel getChannel() {
            return channel;
        }

        public RtpToRealTiming getTiming() {
            return timing;
        }

        public long getSequence() {
            return sequence;
        }

        public void setSequence(long sequence) {
            this.sequence = sequence;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public void incTimestamp(long value) {
            this.timestamp += value;
        }

        public void incSequence() {
            this.sequence++;
        }
    }
}
