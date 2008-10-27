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
package xbird.server.services;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.server.ServiceException;
import xbird.util.string.StringUtils;
import xbird.util.system.SystemUtils;
import xbird.util.system.SystemUtils.CPUInfo;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PerfmonService extends ServiceBase {
    private static final Log LOG = LogFactory.getLog("xbird.PerfMon");

    public static final String SRV_NAME = "PerfMon";
    private final int perfmonIntervalnMills;

    private Timer _timer = null;

    public PerfmonService() {
        this(Integer.parseInt(Settings.get("xbird.perfmon.interval", "5000")));
    }

    public PerfmonService(int interval) {
        super(SRV_NAME);
        this.perfmonIntervalnMills = interval;
    }

    public void start() throws ServiceException {
        if(perfmonIntervalnMills == -1 || !LOG.isInfoEnabled()) {
            return;
        }
        PerfmonTask task = new PerfmonTask();
        Timer timer = new Timer(SRV_NAME, true);
        timer.scheduleAtFixedRate(task, 1000, perfmonIntervalnMills);
        this._timer = timer;
        this._status = Status.started;
    }

    public void stop() throws ServiceException {
        if(_timer != null) {
            _timer.cancel();
            this._timer = null;
        }
        this._status = Status.stopped;
    }

    private static final class PerfmonTask extends TimerTask {

        private CPUInfo prevCpuInfo;

        public PerfmonTask() {
            super();
            this.prevCpuInfo = new CPUInfo();
        }

        @Override
        public void run() {
            CPUInfo newCpuInfo = SystemUtils.getCPUInfo(prevCpuInfo);
            float cpuUsage = newCpuInfo.getCpuUsage();
            long heapUsed = SystemUtils.getHeapUsedMemory();
            if(LOG.isDebugEnabled()) {
                String line = "cpuUsage: " + String.format("%.2f", cpuUsage) + " %, usedHeap: "
                        + StringUtils.displayBytesSize(heapUsed);
                LOG.debug(line);
            }
            this.prevCpuInfo = newCpuInfo;
        }

        @Override
        public boolean cancel() {
            this.prevCpuInfo = null;
            return super.cancel();
        }

    }

}
