package org.ou.common.utils;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.ou.common.constants.ISystemProperties;
import org.ou.main.Main;
import org.ou.process.MainProcess;

public class MetricsUtils {

    public static Map<String, Object> createOsMetricsMap(List<String> stunServersList) throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        String username = UnixUtils.getCurrentUser();
        int uid = UnixUtils.getUid();
        Map<String, Integer> gidMap = UnixUtils.getGroups();
        Collection<String> groups = UnixUtils.getUserGroups(username);
        Collection<Integer> groupsIds = UnixUtils.getGroupIds();
        String stdoutRedirect = UnixUtils.getRedirectedStdoutTarget();

        map.put("uid", uid);
        map.put("gid", groupsIds);
        map.put("groups", groups);
        map.put("group", gidMap);
        map.put("username", username);
        map.put("hostname", UnixUtils.getHostname());

        map.put("stdout_redirect", stdoutRedirect);

        // OS Metrics
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        map.put("os_name", osMxBean.getName());
        map.put("os_version", osMxBean.getVersion());
        map.put("os_arch", osMxBean.getArch());

        map.put("available_processors", Runtime.getRuntime().availableProcessors());
        map.put("interactive_user", ISystemProperties.USER_NAME);
        map.put("user_home_dir", ISystemProperties.USER_HOME);
        map.put("working_dir", ISystemProperties.USER_DIR);

        map.put("system_load_average", osMxBean.getSystemLoadAverage());

        // CPU Load (if available)
        if (osMxBean instanceof com.sun.management.OperatingSystemMXBean osBean) {
            map.put("cpu_process_load", osBean.getProcessCpuLoad());
            map.put("cpu_system_load", osBean.getCpuLoad());
        }

        map.put("interactive_user", ISystemProperties.USER_NAME);
        map.put("user_home_dir", ISystemProperties.USER_HOME);
        map.put("working_dir", ISystemProperties.USER_DIR);

        Collection<String> localAddresses = StunUtils.getLocalAddresses();
        if (!localAddresses.isEmpty()) {
            map.put("local_addresses", localAddresses);
        }

        if (stunServersList != null) {
            for (String stunHostPort : stunServersList) {
                try {
                    InetSocketAddress inetSocketAddress = CommonUtils.parseHostPort(stunHostPort);
                    Collection<String> globalAddresses = StunUtils.getGlobalAddresses(inetSocketAddress);
                    if (!globalAddresses.isEmpty()) {
                        map.put("global_addresses", globalAddresses);
                        map.put("stun_server", stunHostPort);
                    }
                    break;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static Map<String, Object> createJvmMetricsMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        // JVM Runtime Metrics
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        map.put("jvm_start_time_ms", runtime.getStartTime());
        map.put("jvm_uptime_ms", runtime.getUptime());
        map.put("jvm_name", runtime.getName());
        map.put("java_version", System.getProperty(ISystemProperties.JAVA_VERSION));
        map.put("java_vendor", System.getProperty(ISystemProperties.JAVA_VENDOR));

        // Memory Metrics
        MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMxBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMxBean.getNonHeapMemoryUsage();

        map.put("memory_heap_used_mb", heap.getUsed() / (1024 * 1024));
        map.put("memory_heap_committed_mb", heap.getCommitted() / (1024 * 1024));
        map.put("memory_heap_max_mb", heap.getMax() / (1024 * 1024));
        map.put("memory_nonheap_used_mb", nonHeap.getUsed() / (1024 * 1024));

        // Memory Pools
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            String keyPrefix = "memory_pool_" + pool.getName().toLowerCase().replace(" ", "_");
            map.put(keyPrefix + "_used_mb", pool.getUsage().getUsed() / (1024 * 1024));
            map.put(keyPrefix + "_committed_mb", pool.getUsage().getCommitted() / (1024 * 1024));
            map.put(keyPrefix + "_max_mb", pool.getUsage().getMax() / (1024 * 1024));
        }

        // Garbage Collector Metrics
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            String keyPrefix = "gc_" + gcBean.getName().toLowerCase(Locale.ENGLISH).replace(" ", "_");
            map.put(keyPrefix + "_collections", gcBean.getCollectionCount());
            map.put(keyPrefix + "_collection_time_ms", gcBean.getCollectionTime());
        }

        // Thread Metrics
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        map.put("threads_count", threadMxBean.getThreadCount());
        map.put("threads_peak_count", threadMxBean.getPeakThreadCount());
        map.put("threads_daemon_count", threadMxBean.getDaemonThreadCount());
        map.put("threads_total_started", threadMxBean.getTotalStartedThreadCount());

        long[] deadlockedThreads = threadMxBean.findDeadlockedThreads();
        map.put("threads_deadlocked_count", (deadlockedThreads == null) ? 0 : deadlockedThreads.length);

        // Class Loading Metrics
        ClassLoadingMXBean classLoadingMxBean = ManagementFactory.getClassLoadingMXBean();
        map.put("classes_total_loaded", classLoadingMxBean.getTotalLoadedClassCount());
        map.put("classes_currently_loaded", classLoadingMxBean.getLoadedClassCount());
        map.put("classes_total_unloaded", classLoadingMxBean.getUnloadedClassCount());

        return map;
    }

    public static Map<String, Object> createOuMetricsMap() {
        ProcessHandle.Info info = MainProcess.mainProcessHandle.info();
        Optional<Duration> optionalDuration = info.totalCpuDuration();
        Optional<String> optionalCommandLine = info.commandLine();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("implementation_title", Main.MANIFEST_IMPLEMENTATION_TITLE);
        map.put("implementation_version", Main.MANIFEST_IMPLEMENTATION_VERSION);
        map.put("implementation_vendor", Main.MANIFEST_IMPLEMENTATION_VENDOR);
        map.put("implementation_build_date_time", Main.MANIFEST_BUILD_TIME);
        map.put("ou_exec_wrapper_type_standalone", "true".equals(ISystemProperties.OU_STANDALONE));

        map.put("process_pid", MainProcess.mainProcessHandle.pid());
        map.put("process_total_cpu_duration", optionalDuration.get().toString());
        map.put("process_total_command_line", optionalCommandLine.get());

        map.put("logger_queue_size", MainProcess.loggerQueue.size());
        map.put("triggers_queue_size", MainProcess.triggersQueue.size());
        map.put("undelivered_records_count", MainProcess.healthUndeliveredRecordsCount);
        map.put("undelivered_records_count_dmq", MainProcess.healthUndeliveredRecordsCountDMQ);

        return map;
    }
}
