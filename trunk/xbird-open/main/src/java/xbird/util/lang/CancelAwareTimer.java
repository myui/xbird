/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.util.lang;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CancelAwareTimer extends Timer {

    private int numSched = 0;
    private boolean canceled = false;

    public CancelAwareTimer() {
        super();
    }

    public CancelAwareTimer(boolean isDaemon) {
        super(isDaemon);
    }

    public CancelAwareTimer(String name) {
        super(name);
    }

    public CancelAwareTimer(String name, boolean isDaemon) {
        super(name, isDaemon);
    }

    public int getNumberOfScheduled() {
        return numSched;
    }

    @Override
    public void cancel() {
        boolean cancel = canceled;
        this.canceled = true;
        if(!cancel) {
            super.cancel();
        }
    }

    @Override
    public int purge() {
        if(canceled) {
            return 0;
        }
        numSched++;
        return super.purge();
    }

    @Override
    public void schedule(TimerTask task, Date firstTime, long period) {
        if(canceled) {
            return;
        }
        numSched++;
        super.schedule(task, firstTime, period);
    }

    @Override
    public void schedule(TimerTask task, Date time) {
        if(canceled) {
            return;
        }
        numSched++;
        super.schedule(task, time);
    }

    @Override
    public void schedule(TimerTask task, long delay, long period) {
        if(canceled) {
            return;
        }
        numSched++;
        super.schedule(task, delay, period);
    }

    @Override
    public void schedule(TimerTask task, long delay) {
        if(canceled) {
            return;
        }
        numSched++;
        super.schedule(task, delay);
    }

    @Override
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        if(canceled) {
            return;
        }
        numSched++;
        super.scheduleAtFixedRate(task, firstTime, period);
    }

    @Override
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        if(canceled) {
            return;
        }
        numSched++;
        super.scheduleAtFixedRate(task, delay, period);
    }

}
