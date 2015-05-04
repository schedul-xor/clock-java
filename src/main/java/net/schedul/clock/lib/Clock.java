package net.schedul.clock.lib;

import java.util.Collection;
import java.util.HashSet;

import org.joda.time.DateTime;

/**
 * Created by xor on 27/03/15.
 */
public class Clock {
    private long realUtcMilliseconds;
    private long floatingUtcMilliseconds;
    private long prevUtcMilliseconds;
    private double timePower;
    private Collection<ClockUpdateReceivable> tickEventDelegates;

    public Clock() {
        this.timePower = 1.0;
        this.realUtcMilliseconds = this.floatingUtcMilliseconds = this.prevUtcMilliseconds = DateTime.now().getMillis();
	this.tickEventDelegates = new HashSet<>();
    }

    public void addTickEventDelegate(ClockUpdateReceivable tickEventDelegate) {
        this.tickEventDelegates.add(tickEventDelegate);
    }

    public void start() {
        this.realUtcMilliseconds = DateTime.now().getMillis();
        this.floatingUtcMilliseconds = this.prevUtcMilliseconds;
        this.handleTimeUpdatedEvent();
    }

    public void setUtcMilliseconds(long utcMilliseconds) {
        this.floatingUtcMilliseconds = utcMilliseconds;
        this.realUtcMilliseconds = DateTime.now().getMillis();
    }

    public long getUtcMilliseconds() {
        long realDelta = DateTime.now().getMillis() - this.realUtcMilliseconds;
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

    public void handleTimeUpdatedEvent(){
	for(ClockUpdateReceivable l : this.tickEventDelegates){
            l.onClockUpdate(this.getUtcMilliseconds());
        }
    }

    public interface ClockUpdateReceivable {
        void onClockUpdate(long utcMilliseconds);
    }
}
