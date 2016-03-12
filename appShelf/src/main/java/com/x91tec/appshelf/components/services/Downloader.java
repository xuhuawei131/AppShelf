package com.x91tec.appshelf.components.services;

/**
 * Created by oeager on 16-3-5.
 */
public interface Downloader {

    void download(VersionResponse response,UpCallback callback);

    void cancel(VersionResponse response,UpCallback callback);
}
