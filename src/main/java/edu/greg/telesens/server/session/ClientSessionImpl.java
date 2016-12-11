package edu.greg.telesens.server.session;

import edu.greg.telesens.server.channel.ClientChannelImpl;

/**
 * Created by Phoenix on 11.12.2016.
 */
public class ClientSessionImpl {
    //    global params
    private String id;
    private String sipServer;

    //    addresses
    private String clientAddress;
    private int clientPort;
    private String serverAddress;
    private int serverPort;

    //    local params
    private ClientSessionState state;
    private ClientChannelImpl channel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSipServer() {
        return sipServer;
    }

    public void setSipServer(String sipServer) {
        this.sipServer = sipServer;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public ClientSessionState getState() {
        return state;
    }

    public void setState(ClientSessionState state) {
        this.state = state;
    }
}
