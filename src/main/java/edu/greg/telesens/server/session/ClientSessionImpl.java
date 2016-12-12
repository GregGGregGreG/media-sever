package edu.greg.telesens.server.session;

import edu.greg.telesens.server.buffer.Buffer;
import edu.greg.telesens.server.format.Format;
import edu.greg.telesens.server.memory.Packet;
import edu.greg.telesens.server.network.rtp.RtpPacket;

/**
 * Created by SKulik on 12.12.2016.
 */
public class ClientSessionImpl implements ClientSession {
    @Override
    public Buffer getBuffer() {
        return null;
    }

    @Override
    public String getMelodyPath() {
        return null;
    }

    @Override
    public Format getDestinationFormat() {
        return null;
    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public RtpPacket wrap(Packet packet, long currentTime) {
        return null;
    }
}
