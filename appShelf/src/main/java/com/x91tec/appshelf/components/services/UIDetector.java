package com.x91tec.appshelf.components.services;

import android.app.Activity;

/**
 * Created by oeager on 16-3-4.
 */
public interface UIDetector {

    void onUnnecessaryUp(Activity context,VersionResponse response);

    void requestGrantUp(Activity context,VersionResponse response,UpInteractive interactive);
}
