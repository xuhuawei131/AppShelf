package com.x91tec.appshelf.process;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ProcessManager {

    private ProcessManager() {
        throw new AssertionError("no instances");
    }

    /**
     * @return a list of <i>all</i> processes running on the device.
     */
    public static List<AndroidProcess> getRunningProcesses() {
        List<AndroidProcess> processes = new ArrayList<>();
        File[] files = new File("/proc").listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                int pid;
                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException e) {
                    continue;
                }
                try {
                    processes.add(new AndroidProcess(pid));
                } catch (IOException e) {
                    // If you are running this from a third-party app, then system apps will not be
                    // readable on Android 5.0+ if SELinux is enforcing. You will need root access or an
                    // elevated SELinux context to read all files under /proc.
                    // See: https://su.chainfire.eu/#selinux
                }
            }
        }
        return processes;
    }

    /**
     * @return a list of all running app processes on the device.
     */
    public static List<AndroidAppProcess> getRunningAppProcesses() {
        List<AndroidAppProcess> processes = new ArrayList<>();
        File[] files = new File("/proc").listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                int pid;
                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException e) {
                    continue;
                }
                try {
                    processes.add(new AndroidAppProcess(pid));
                } catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
                } catch (IOException e) {
                    // If you are running this from a third-party app, then system apps will not be
                    // readable on Android 5.0+ if SELinux is enforcing. You will need root access or an
                    // elevated SELinux context to read all files under /proc.
                    // See: https://su.chainfire.eu/#selinux
                }
            }
        }
        return processes;
    }


    public static List<AndroidAppProcess> getRunningForegroundApps(Context ctx) {
        List<AndroidAppProcess> processes = new ArrayList<>();
        File[] files = new File("/proc").listFiles();
        PackageManager pm = ctx.getPackageManager();
        for (File file : files) {
            if (file.isDirectory()) {
                int pid;
                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException e) {
                    continue;
                }
                try {
                    AndroidAppProcess process = new AndroidAppProcess(pid);
                    if (!process.foreground) {
                        // Ignore processes not in the foreground
                        continue;
                    } else if (process.uid >= 1000 && process.uid <= 9999) {
                        // First app user starts at 10000. Ignore system processes.
                        continue;
                    } else if (process.name.contains(":")) {
                        // Ignore processes that are not running in the default app process.
                        continue;
                    } else if (pm.getLaunchIntentForPackage(process.getPackageName()) == null) {
                        // Ignore processes that the user cannot launch.
                        // TODO: remove this block?
                        continue;
                    }
                    processes.add(process);
                } catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
                } catch (IOException e) {
                    // If you are running this from a third-party app, then system apps will not be
                    // readable on Android 5.0+ if SELinux is enforcing. You will need root access or an
                    // elevated SELinux context to read all files under /proc.
                    // See: https://su.chainfire.eu/#selinux
                }
            }
        }
        return processes;
    }


    public static boolean isMyProcessInTheForeground() {
        List<AndroidAppProcess> processes = getRunningAppProcesses();
        int myPid = android.os.Process.myPid();
        for (AndroidAppProcess process : processes) {
            if (process.pid == myPid && process.foreground) {
                return true;
            }
        }
        return false;
    }

    public static List<RunningAppProcessInfo> getRunningAppProcessInfo(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
            List<RunningAppProcessInfo> appProcessInfos = new ArrayList<>();
            for (AndroidAppProcess process : runningAppProcesses) {
                RunningAppProcessInfo info = new RunningAppProcessInfo(
                        process.name, process.pid, null
                );
                info.uid = process.uid;
                // TODO: Get more information about the process. pkgList, importance, lru, etc.
                appProcessInfos.add(info);
            }
            return appProcessInfos;
        }
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses();
    }


    @Deprecated
    public static class Filter {

        private String name;
        private int pid = -1;
        private int ppid = -1;
        private boolean apps;


        @Deprecated
        public Filter setName(String name) {
            this.name = name;
            return this;
        }


        @Deprecated
        public Filter setPid(int pid) {
            this.pid = pid;
            return this;
        }

        @Deprecated
        public Filter setPpid(int ppid) {
            this.ppid = ppid;
            return this;
        }


        @Deprecated
        public Filter setApps(boolean apps) {
            this.apps = apps;
            return this;
        }


        @Deprecated
        public List<AndroidProcess> run() {
            List<AndroidProcess> processes = new ArrayList<>();
            File[] files = new File("/proc").listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    int pid;
                    try {
                        pid = Integer.parseInt(file.getName());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    if (this.pid != -1 && pid != this.pid) {
                        continue;
                    }
                    try {
                        AndroidProcess process;
                        if (this.apps) {
                            process = new AndroidAppProcess(pid);
                        } else {
                            process = new AndroidProcess(pid);
                        }
                        if (this.name != null && !process.name.contains(this.name)) {
                            continue;
                        }
                        if (this.ppid != -1 && process.stat().ppid() != this.ppid) {
                            continue;
                        }
                        processes.add(process);
                    } catch (IOException e) {
                        // If you are running this from a third-party app, then system apps will not be
                        // readable on Android 5.0+ if SELinux is enforcing. You will need root access or an
                        // elevated SELinux context to read all files under /proc.
                        // See: https://su.chainfire.eu/#selinux
                    } catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
                    }
                }
            }
            return processes;
        }

    }

    /**
     * Comparator to list processes by name
     */
    public static final class ProcessComparator implements Comparator<AndroidProcess> {

        @Override
        public int compare(AndroidProcess p1, AndroidProcess p2) {
            return p1.name.compareToIgnoreCase(p2.name);
        }

    }

}
