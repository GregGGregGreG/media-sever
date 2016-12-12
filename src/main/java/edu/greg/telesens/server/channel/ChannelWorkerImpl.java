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
        parent.useWorker(this, session);
    }

    @Override
    public void stopSession(ClientSession session) {
        nodes.remove(session.getSessionId());
        parent.freeWorker(this, session);
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
            Packet packet = n.getBuffer().get(n.getSession().getSessionId());
            while (packet != null && packet.getRealTime()  <= currentTime) {
                RtpPacket rtp = n.getSession().wrap(packet, currentTime);
                try {
                    n.getChannel().send(rtp);
                } catch (IOException e) {
//                    TODO intercept this exception
                }
                packet.recycle();
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
        private long sequence = 0L;

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

        public long getSequence() {
            return sequence;
        }

        public void setSequence(long sequence) {
            this.sequence = sequence;
        }

        public void incSequence() {
            this.sequence++;
        }
    }
}
