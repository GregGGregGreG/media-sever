package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.network.rtp.RtpPacket;

/**
 * Created by SKulik on 13.12.2016.
 */
public interface DtmfEventProcessor {
    void process(RtpPacket packet);
}
