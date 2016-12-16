package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.network.rtp.RtpPacket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Created by SKulik on 09.12.2016.
 */
@Slf4j
@Getter
public class ClientChannelImpl implements ClientChannel {

    private InetSocketAddress dstSockAddr;
    private InetSocketAddress srcSockAddr;
    private DatagramChannel channel;

    public ClientChannelImpl(String srcAddr, int srcPort, String dstAddr, int dstPort) {
        dstSockAddr = new InetSocketAddress(dstAddr, dstPort);
        srcSockAddr = new InetSocketAddress(srcAddr, srcPort);
    }

    public void bind() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        DatagramSocket socket = channel.socket();
        socket.bind(srcSockAddr);
        channel.configureBlocking(false);
        this.channel = channel;
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }

    public void send(RtpPacket rtpPacket) throws IOException {
//        log.debug("{} --> {} RTP packet ", srcSockAddr.getAddress().getCanonicalHostName(), dstSockAddr.getAddress().toString(), rtpPacket.getBuffer().toString());
        channel.send(rtpPacket.getBuffer(), dstSockAddr);
    }

}
