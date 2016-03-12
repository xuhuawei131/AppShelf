package com.x91tec.appshelf.components.services;

/**
 * Created by oeager on 16-3-5.
 */
public interface UpCallback {

    void onStartUp(VersionResponse response);

    void onProgressUp(VersionResponse response,long currentSize);

    void onUpError(VersionResponse response);

    void onUpCancel(VersionResponse response);

    void onCompletedUp(VersionResponse response);
}
