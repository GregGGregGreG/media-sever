package edu.greg.telesens.server.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by SKulik on 12.12.2016.
 */
public class PortHolder {

    private int highestPort;
    private int lowestPort;

    private List<Integer> usedPorts = new ArrayList<>();
    private Integer lastPort;

    int maxCount;

    private Lock lock = new ReentrantLock();

    public PortHolder(int lowestPort, int highestPort) {
        this.lowestPort = lowestPort;
        this.highestPort = highestPort;
        lastPort = lowestPort;
        maxCount = highestPort - lowestPort;
    }

    public void freePort(int port) {
        lock.lock();
        try {
            usedPorts.remove(Integer.valueOf(port));
        } finally {
            lock.unlock();
        }
    }

    public int getPort() throws Exception {
        lock.lock();
        try {
            return getNextPort();
        } finally {
            lock.unlock();
        }
    }

    private int getNextPort() throws Exception {
        if (usedPorts.size() >= maxCount) {
            throw new Exception("Not enough free ports");
        }
        while (usedPorts.contains(lastPort)) {
            lastPort++;
            if (lastPort.compareTo(highestPort) > 0) {
                lastPort = lowestPort;
            }
        }
        usedPorts.add(lastPort);
        return lastPort;
    }


}
