package com.x91tec.appshelf.process;

import android.os.Parcel;

import java.io.IOException;

public final class Status extends ProcFile {

    /**
     * Read /proc/[pid]/status.
     *
     * @param pid the process id.
     * @return the {@link Status}
     * @throws IOException if the file does not exist or we don't have read permissions.
     */
    public static Status get(int pid) throws IOException {
        return new Status(String.format("/proc/%d/status", pid));
    }

    private Status(String path) throws IOException {
        super(path);
    }

    private Status(Parcel in) {
        super(in);
    }

    /**
     * Get the value of one of the fields.
     *
     * @param fieldName the field name. E.g "PPid", "Uid", "Groups".
     * @return The value of the field or {@code null}.
     */
    public String getValue(String fieldName) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.startsWith(fieldName + ":")) {
                return line.split(fieldName + ":")[1].trim();
            }
        }
        return null;
    }

    /**
     * @return The process' UID or -1 if parsing the UID failed.
     */
    public int getUid() {
        try {
            return Integer.parseInt(getValue("Uid").split("\\s+")[0]);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * @return The process' GID or -1 if parsing the GID failed.
     */
    public int getGid() {
        try {
            return Integer.parseInt(getValue("Gid").split("\\s+")[0]);
        } catch (Exception e) {
            return -1;
        }
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {

        @Override
        public Status createFromParcel(Parcel source) {
            return new Status(source);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };

}
