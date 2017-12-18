package com.ogiqvo.clock;

import java.util.ArrayList;
import java.util.List;

/**
 *
 The MIT License (MIT)

 Copyright (c) 2017 Izumi Kawashima

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
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
        this.handleClockUpdateEvent();
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
        boolean isTimePowerUpdated = this.timePower != timePower;
        this.timePower = timePower;
        if (isTimePowerUpdated) {
            handlePowerUpdateEvent();
        }
    }

    public void handleClockUpdateEvent() {
        int length = this.tickEventDelegates.size();
        long utcMilliseconds = this.getUtcMilliseconds();
        for (int i = 0; i < length; i++) {
            ClockUpdateReceivable l = this.tickEventDelegates.get(i);
            l.onClockUpdate(utcMilliseconds);
        }
    }

    public void handlePowerUpdateEvent() {
        int length = this.tickEventDelegates.size();
        for (int i = 0; i < length; i++) {
            ClockUpdateReceivable l = this.tickEventDelegates.get(i);
            l.onPowerUpdate(this.timePower);
        }
    }

    public interface ClockUpdateReceivable {
        void onClockUpdate(long utcMilliseconds);

        void onPowerUpdate(double power);
    }
}
