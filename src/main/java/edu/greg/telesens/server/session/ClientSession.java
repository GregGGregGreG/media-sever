package edu.greg.telesens.server.session;

import edu.greg.telesens.server.buffer.Buffer;
import edu.greg.telesens.server.channel.ClientChannelImpl;
import edu.greg.telesens.server.format.Format;

/**
 * Created by SKulik on 09.12.2016.
 */
public interface ClientSession {

    Buffer getBuffer();

    String getMelodyPath();

    Format getDestinationFormat();

    String getSessionId();
}
