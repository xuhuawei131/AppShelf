package com.x91tec.appshelf.components.services;

/**
 * Created by oeager on 16-3-8.
 */
public class EmptyUpCallback implements UpCallback {
    @Override
    public void onStartUp(VersionResponse response) {

    }

    @Override
    public void onProgressUp(VersionResponse response, long currentSize) {

    }

    @Override
    public void onUpError(VersionResponse response) {

    }

    @Override
    public void onUpCancel(VersionResponse response) {

    }

    @Override
    public void onCompletedUp(VersionResponse response) {

    }
}
