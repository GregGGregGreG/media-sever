package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.network.rtp.RtpPacket;

/**
 * Created by SKulik on 13.12.2016.
 */
public class DtmfEventProcessorImpl implements DtmfEventProcessor {
    private final DtmfEventListener listener;
    private byte lastEventId = -1;
    private byte lastIsEnd = -1;

    public DtmfEventProcessorImpl(DtmfEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void process(RtpPacket packet) {
        byte eventId = packet.getBuffer().get(12);
        byte isEnd = (byte) (packet.getBuffer().get(13) >> 7);
        if (eventId != lastEventId || (isEnd == (byte)0 && lastIsEnd == (byte)1)) {
//            lastEventId = isEnd == (byte)1 ? -1 :eventId;
            lastEventId = eventId;
            lastIsEnd = isEnd;
            listener.keyDetected(lastEventId);
        }
    }

}
