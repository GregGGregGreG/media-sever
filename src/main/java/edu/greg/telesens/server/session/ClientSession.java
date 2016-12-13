package edu.greg.telesens.server.session;

import edu.greg.telesens.server.buffer.Buffer;
import edu.greg.telesens.server.channel.ClientChannelImpl;
import edu.greg.telesens.server.format.Format;
import edu.greg.telesens.server.memory.Packet;
import edu.greg.telesens.server.network.rtp.RtpPacket;

/**
 * Created by SKulik on 09.12.2016.
 */
public interface ClientSession {



    Buffer getBuffer();

    String getMelodyPath();

    Format getDestinationFormat();

    String getSessionId();

    RtpPacket wrap(Packet packet, long currentTime, int sequence, long startTime);

    void play();

    void stop();
}
