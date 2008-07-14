/*
 * @(#)$Id: SystemUtils.java 3870 2008-05-21 02:46:25Z yui $
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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SystemUtils {

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final boolean IS_OS_SUN_OS = OS_NAME.startsWith("SunOS")
            || OS_NAME.startsWith("Solaris");
    public static final boolean IS_OS_LINUX = OS_NAME.startsWith("Linux")
            || OS_NAME.startsWith("LINUX");
    public static final boolean IS_OS_WINDOWS = OS_NAME.startsWith("Windows");
    public static final float JAVA_VERSION_FLOAT = parseJavaVersion(getJavaVersion(JAVA_VERSION));

    private static final int NPROCS = Runtime.getRuntime().availableProcessors();
    public static final boolean IS_SUN_VM = System.getProperty("java.vm.vendor").indexOf("Sun") != -1;
    private static final MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    private static final boolean USE_SUN_MXBEAN;
    static {
        boolean useSunMx = false;
        OperatingSystemMXBean mx = ManagementFactory.getOperatingSystemMXBean();
        if(IS_SUN_VM) {
            com.sun.management.OperatingSystemMXBean sunmx = (com.sun.management.OperatingSystemMXBean) mx;
            long testval = sunmx.getProcessCpuTime();
            if(testval != -1L) {
                useSunMx = true;
            }
        }
        if(!useSunMx) {
            if(System.getProperty("xbird.use_jni") != null) {
                throw new IllegalStateException("Please set `xbird.use_jni' system property");
            }
            System.loadLibrary("xbird_util_lang_SystemUtils");
        }
        USE_SUN_MXBEAN = useSunMx;
    }

    private SystemUtils() {}

    public static float getJavaVersion() {
        return JAVA_VERSION_FLOAT;
    }

    private static float parseJavaVersion(String versionStr) {
        if(versionStr == null) {
            throw new IllegalArgumentException();
        }
        // e.g., 1.3.12, 1.6
        String str = versionStr.substring(0, 3);
        if(versionStr.length() >= 5) {
            str = str + versionStr.substring(4, 5);
        }
        return Float.parseFloat(str);
    }

    private static String getJavaVersion(String versionStr) {
        for(int i = 0; i < versionStr.length(); i++) {
            char c = versionStr.charAt(i);
            if(Character.isDigit(c)) {
                return versionStr.substring(i);
            }
        }
        return null;
    }

    public static boolean isSunVM() {
        return IS_SUN_VM;
    }

    public static boolean is64BitVM() {
        try {
            int bits = Integer.getInteger("sun.arch.data.model", 0).intValue();
            if(bits != 0) {
                return bits == 64;
            }
            return System.getProperty("java.vm.name").indexOf("64") >= 0;
        } catch (Throwable t) {
            return false;
        }
    }

    public static int availableProcessors() {
        return NPROCS;
    }

    public static boolean isSendfileSupported() {
        return IS_OS_SUN_OS || (IS_OS_LINUX && getJavaVersion() >= 1.7f);
    }

    public static boolean isMunmapAvailable() {
        return !IS_OS_WINDOWS || getJavaVersion() >= 1.7f;
    }

    public static long getFreeMemory() {
        final Runtime runtime = Runtime.getRuntime();
        return runtime.freeMemory();
    }

    public static long getHeapFreeMemory() {
        MemoryUsage usage = mbean.getHeapMemoryUsage();
        final long max = usage.getMax();
        final long used = usage.getUsed();
        final long free = max - used;
        return free;
    }

    public static long getHeapUsedMemory() {
        MemoryUsage usage = mbean.getHeapMemoryUsage();
        final long used = usage.getUsed();
        return used;
    }

    public static float getHeapFreeRatio() {
        MemoryUsage usage = mbean.getHeapMemoryUsage();
        final long max = usage.getMax();
        final long used = usage.getUsed();
        final long free = max - used;
        return (float) free / (float) max;
    }

    public static int getHeapFreePercentage() {
        return (int) (getHeapFreeRatio() * 100f);
    }

    public static int countGC() {
        int count = 0;
        final List<GarbageCollectorMXBean> gclist = ManagementFactory.getGarbageCollectorMXBeans();
        for(GarbageCollectorMXBean gcmx : gclist) {
            count += gcmx.getCollectionCount();
        }
        return count;
    }

    /** return in nano-seconds */
    public static long getProcessCpuTime() {
        if(USE_SUN_MXBEAN) {
            OperatingSystemMXBean mx = ManagementFactory.getOperatingSystemMXBean();
            com.sun.management.OperatingSystemMXBean sunmx = (com.sun.management.OperatingSystemMXBean) mx;
            return sunmx.getProcessCpuTime();
        } else {
            return _getProcessCpuTime() * 1000000L; /* milli to nano (10^6) */
        }
    }

    /** return in msec */
    public static long getUpTimeInMillis() {
        final RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
        return mx.getUptime();
    }

    public static CPUInfo getCPUInfo(CPUInfo prev) {
        final long cpuTime = getProcessCpuTime();
        final long upTime = getUpTimeInMillis();
        final long elapsedCpu = cpuTime - prev.cpuTime;
        long elapsedUp = upTime - prev.upTime;
        if(elapsedUp == 0L) {
            elapsedUp = 1L;
        }
        // nano (10^6) * percent (100) = 10000F
        final float cpuUsage = Math.min(99F, elapsedCpu / (elapsedUp * 10000F * NPROCS));
        return new CPUInfo(cpuTime, upTime, cpuUsage);
    }

    public static final class CPUInfo {

        final long cpuTime;
        final long upTime;
        final float cpuUsage;

        public CPUInfo() {
            this.cpuTime = getProcessCpuTime();
            this.upTime = getUpTimeInMillis();
            this.cpuUsage = 1L;
        }

        public CPUInfo(long procCpuTime, long upTime, float cpuUsage) {
            this.cpuTime = procCpuTime;
            this.upTime = upTime;
            this.cpuUsage = cpuUsage;
        }

        public float getCpuUsage() {
            return cpuUsage;
        }
    }

    /** gets Process CPU time in milli-seconds */
    private native static long _getProcessCpuTime();

}
