package edu.greg.telesens.server.session;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import edu.greg.telesens.server.network.rtp.RtpPacket;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Created by SKulik on 09.12.2016.
 */
public class ClientChannel {
    private InetSocketAddress dstSockAddr;
    private InetSocketAddress srcSockAddr;
    private DatagramChannel channel;

    public ClientChannel(String srcAddr, int srcPort, String dstAddr, int dstPort) {
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

    public void send(RtpPacket rtpPacket) throws IOException {
        channel.send(rtpPacket.getBuffer(), dstSockAddr);
    }

    private static DatagramChannel generateChanel(String srcAddr, int port) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        InetSocketAddress addr = new InetSocketAddress(srcAddr, port);
        DatagramSocket socket = channel.socket();
        socket.bind(addr);
        channel.configureBlocking(false);
        return channel;
    }
}
