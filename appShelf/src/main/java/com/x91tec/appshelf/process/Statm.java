package com.x91tec.appshelf.process;

import android.os.Parcel;

import java.io.IOException;

public final class Statm extends ProcFile {

    /**
     * Read /proc/[pid]/statm.
     *
     * @param pid the process id.
     * @return the {@link Statm}
     * @throws IOException if the file does not exist or we don't have read permissions.
     */
    public static Statm get(int pid) throws IOException {
        return new Statm(String.format("/proc/%d/statm", pid));
    }

    public final String[] fields;

    private Statm(String path) throws IOException {
        super(path);
        fields = content.split("\\s+");
    }

    private Statm(Parcel in) {
        super(in);
        this.fields = in.createStringArray();
    }

    /**
     * @return the total program size in bytes
     */
    public long getSize() {
        return Long.parseLong(fields[0]) * 1024;
    }

    /**
     * @return the resident set size in bytes
     */
    public long getResidentSetSize() {
        return Long.parseLong(fields[1]) * 1024;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringArray(this.fields);
    }

    public static final Creator<Statm> CREATOR = new Creator<Statm>() {

        @Override
        public Statm createFromParcel(Parcel source) {
            return new Statm(source);
        }

        @Override
        public Statm[] newArray(int size) {
            return new Statm[size];
        }
    };

}
