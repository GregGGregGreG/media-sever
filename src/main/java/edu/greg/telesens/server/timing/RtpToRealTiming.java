package edu.greg.telesens.server.timing;

import java.util.concurrent.TimeUnit;

/**
 * Created by SKulik on 12.12.2016.
 */
public interface RtpToRealTiming {
    void initTime(long time, TimeUnit timeUnit);
    long getRealTimeByRtpTime(long time, TimeUnit timeUnit);
}
