package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.network.rtp.RtpPacket;

import java.io.IOException;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface ClientChannel {
    void bind() throws IOException;
    void close() throws IOException;
    void send(RtpPacket packet) throws IOException;
}
