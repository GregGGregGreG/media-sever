package edu.greg.telesens.server.channel;

/**
 * Created by SKulik on 13.12.2016.
 */
public interface DtmfEventListener {
    public abstract void keyDetected(byte key);
}
