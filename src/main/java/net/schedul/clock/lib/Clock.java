package net.schedul.clock.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * Created by xor on 27/03/15.
 */
public class Clock {
    private long realUtcMilliseconds;
    private long floatingUtcMilliseconds;
    private long prevUtcMilliseconds;
    private double timePower;
    private List<ClockUpdateReceivable> tickEventDelegates;

    public Clock() {
        this.timePower = 1.0;
        this.realUtcMilliseconds = this.floatingUtcMilliseconds = this.prevUtcMilliseconds = System.currentTimeMillis();
        this.tickEventDelegates = new ArrayList<>();
    }

    public void addTickEventDelegate(ClockUpdateReceivable tickEventDelegate) {
        this.tickEventDelegates.add(tickEventDelegate);
    }

    public void start() {
        this.realUtcMilliseconds = System.currentTimeMillis();
        this.floatingUtcMilliseconds = this.prevUtcMilliseconds;
        this.handleTimeUpdatedEvent();
    }

    public void setUtcMilliseconds(long utcMilliseconds) {
        this.floatingUtcMilliseconds = utcMilliseconds;
        this.realUtcMilliseconds = System.currentTimeMillis();
    }

    public long getUtcMilliseconds() {
        long realDelta = System.currentTimeMillis() - this.realUtcMilliseconds;
        long amplifiedRealDelta = (long) (realDelta * this.timePower);
        this.prevUtcMilliseconds = this.floatingUtcMilliseconds + amplifiedRealDelta;
        return this.prevUtcMilliseconds;
    }

    public long getPrevUtcMilliseconds() {
        return prevUtcMilliseconds;
    }

    public void setPrevUtcMilliseconds(long prevUtcMilliseconds) {
        this.prevUtcMilliseconds = prevUtcMilliseconds;
    }

    public long getRealUtcMilliseconds() {
        return realUtcMilliseconds;
    }

    public void setRealUtcMilliseconds(long realUtcMilliseconds) {
        this.realUtcMilliseconds = realUtcMilliseconds;
    }

    public long getFloatingUtcMilliseconds() {
        return floatingUtcMilliseconds;
    }

    public void setFloatingUtcMilliseconds(long floatingUtcMilliseconds) {
        this.floatingUtcMilliseconds = floatingUtcMilliseconds;
    }

    public double getTimePower() {
        return timePower;
    }

    public void setTimePower(double timePower) {
        this.timePower = timePower;
    }

    public void handleTimeUpdatedEvent() {
        int length = this.tickEventDelegates.size();
        for (int i = 0; i < length; i++) {
            ClockUpdateReceivable l = this.tickEventDelegates.get(i);
            l.onClockUpdate(this.getUtcMilliseconds());
        }
    }

    public interface ClockUpdateReceivable {
        void onClockUpdate(long utcMilliseconds);
    }
}
