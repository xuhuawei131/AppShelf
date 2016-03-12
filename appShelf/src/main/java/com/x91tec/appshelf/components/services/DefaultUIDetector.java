package com.x91tec.appshelf.components.services;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.x91tec.appshelf.R;

/**
 * Created by oeager on 16-3-7.
 */
public class DefaultUIDetector implements UIDetector {




    @Override
    public void onUnnecessaryUp(Activity context, VersionResponse response) {

    }

    @Override
    public void requestGrantUp(Activity activity, final VersionResponse response, final UpInteractive interactive) {
        if(activity==null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String extra = response.forceUpgrade?activity.getString(R.string.must_upgrade):activity.getString(R.string.suggest_upgrade);
        String message = activity.getString(R.string.app_upgrade_tips,response.newVersionName,response.releaseNote,extra);
        builder.setMessage(message);
        builder.setCancelable(!response.forceUpgrade);

        builder.setPositiveButton(R.string.upgrade, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interactive.onAllowUp(response);
            }
        });
        builder.setNegativeButton(R.string.after, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interactive.onForbidUp(response);
            }
        });
        builder.create().show();
    }
}
