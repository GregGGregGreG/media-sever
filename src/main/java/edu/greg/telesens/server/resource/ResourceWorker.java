package edu.greg.telesens.server.resource;

import edu.greg.telesens.server.buffer.BufferEventHandler;
import edu.greg.telesens.server.session.ClientSession;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface ResourceWorker extends Runnable, BufferEventHandler{
    void init(ClientSession session);
}
