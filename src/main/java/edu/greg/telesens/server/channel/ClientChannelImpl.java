package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.network.rtp.RtpPacket;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Created by SKulik on 09.12.2016.
 */
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
        channel.send(rtpPacket.getBuffer(), dstSockAddr);
    }

    @Override
    public DatagramChannel getChannel() {
        return channel;
    }
}
