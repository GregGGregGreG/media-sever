package edu.greg.telesens.server.services;

/**
 * Created by
 * GreG on 11/8/2016.
 */
public interface PlayerService {

    void play(String DST_ADDRRES, int PORT_DEST, String melodyPath, String codec, int rep);

    void stop();

}
