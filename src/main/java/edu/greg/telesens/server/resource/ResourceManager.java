package edu.greg.telesens.server.resource;

import edu.greg.telesens.server.session.ClientSession;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface ResourceManager {
    ResourceWorker createWorker(ClientSession session);
}
