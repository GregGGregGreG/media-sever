package edu.greg.telesens.server.timing;

import java.util.concurrent.TimeUnit;

/**
 * Created by SKulik on 12.12.2016.
 */
public class RtpToRealTimingImpl implements RtpToRealTiming {
    public static final long TIMING_RTP_TIME_PIECE = 160L;
    public static final long TIMING_REAL_TIME_PIECE = 20L;
    public static final TimeUnit TIMING_REAL_TIME_UNIT = TimeUnit.MILLISECONDS;

    private long startTime;

    @Override
    public void initTime(long time, TimeUnit timeUnit) {
        startTime = TIMING_REAL_TIME_UNIT.convert(time, timeUnit);
    }

    @Override
    public long getRealTimeByRtpTime(long time, TimeUnit timeUnit) {
        return timeUnit.convert(startTime + (time/TIMING_RTP_TIME_PIECE)*TIMING_REAL_TIME_PIECE, TIMING_REAL_TIME_UNIT);
    }
}
