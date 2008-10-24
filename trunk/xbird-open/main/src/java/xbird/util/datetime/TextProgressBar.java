/*
 * @(#)$Id$
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.util.datetime;

import java.util.Random;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://0xcc.net/ruby-progressbar/
 */
public class TextProgressBar {

    protected final String title;

    protected final int totalSteps;
    protected int stepCount = 0;

    protected final long startTime;
    protected long endTime = -1L;

    protected long refreshTime = 5000;
    protected long lastShown;
    protected int lastProgressPercentage = 0;
    protected int refreshFluctuations = 5;

    /** 
     * example: 
     *  title   96% |oooo _ _ _ | Elapsed: 1m 2sec
     */
    private String format;
    private int barWidth = 30;
    private char barMark = 'o';

    public TextProgressBar(String title, int totalSteps, long startTime) {
        this.title = title;
        this.totalSteps = totalSteps;
        this.startTime = startTime;
        this.lastShown = startTime;
        final int titleLength = title.length();
        if(titleLength >= 10) {
            format = "%-" + (titleLength + 2) + "s %3d%% |%s| Elapsed: %s";
        } else {
            format = "%-10s %3d%% |%s| Elapsed: %s";
        }
    }

    public TextProgressBar(String title, int totalSteps) {
        this(title, totalSteps, System.currentTimeMillis());
    }

    public final void setFormat(String frmt) {
        this.format = frmt;
    }

    public final void setBarWidth(int width) {
        this.barWidth = width;
    }

    public final void setBarMark(char mark) {
        this.barMark = mark;
    }

    public final void setRefreshTime(long msec) {
        this.refreshTime = msec;
    }

    /**
     * @param n between 0 and 100
     */
    public final void setRefreshFluctations(int n) {
        if(n > 100 || n < 0) {
            throw new IllegalArgumentException("Fluctation must be between 0 and 100: " + n);
        }
        this.refreshFluctuations = n;
    }

    public final void inc(int step) {
        stepCount += step;
        if(stepCount > totalSteps) {
            this.stepCount = totalSteps;
        }
        showIfNeeded();
    }

    public final void inc() {
        stepCount += 1;
        showIfNeeded();
    }

    public final void set(int count) {
        if(count < 0 || count > totalSteps) {
            throw new IllegalArgumentException();
        }
        this.stepCount = count;
        showIfNeeded();
    }

    public final void finish() {
        if(endTime != -1L) {
            return; // already finished
        }
        this.endTime = System.currentTimeMillis();
        this.stepCount = totalSteps;
        showIfNeeded();
    }

    public final float getProgress() {
        if(endTime != -1L) {
            return 1f;
        }
        return stepCount / totalSteps;
    }

    public final int getProgressPercentage() {
        if(endTime != -1L) {
            return 100;
        }
        return (int) ((stepCount * 100.f) / totalSteps);
    }

    public final long elapsedTime() {
        if(endTime != -1L) {
            return endTime - startTime;
        } else {
            return System.currentTimeMillis() - startTime;
        }
    }

    public final String getInfo() {
        String bar = generateBar();
        String elapsed = formatTime(elapsedTime());
        return String.format(format, title, getProgressPercentage(), bar, elapsed);
    }

    private final String generateBar() {
        final StringBuilder buf = new StringBuilder(barWidth);
        final int numDots = getProgressPercentage() * barWidth / 100;
        for(int i = 0; i < numDots; i++) {
            buf.append(barMark);
        }
        final int numSpaces = barWidth - numDots;
        for(int j = 0; j < numSpaces; j++) {
            buf.append(' ');
        }
        return buf.toString();
    }

    private void showIfNeeded() {
        if(isShowNeeded()) {
            this.lastShown = System.currentTimeMillis();
            this.lastProgressPercentage = getProgressPercentage();
            show();
        }
    }

    // ------------------------------
    // user customizable points

    protected String formatTime(long t) {
        final int sec = (int) (t % 60);
        final int min = (int) ((t / 60) % 60);
        final int hour = (int) (t / 3600);
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    protected boolean isShowNeeded() {
        final long elapsed = System.currentTimeMillis() - lastShown;
        if(elapsed >= refreshTime) {
            return true;
        }
        final int diff = getProgressPercentage() - lastProgressPercentage;
        if(diff >= refreshFluctuations) {
            return true;
        }
        return false;
    }

    protected void show() {}

    // ------------------------------

    public static void main(String[] args) throws InterruptedException {
        final TextProgressBar bar = new TextProgressBar("test", 100) {
            protected void show() {
                System.out.println(getInfo());
            }
        };
        final Random rand = new Random(2390814071209731L);
        for(int i = 0; i < 100; i++) {
            Thread.sleep(rand.nextInt(3000));
            bar.inc();
        }
        bar.finish();
        System.err.println(bar.getInfo());
    }
}
