package com.x91tec.appshelf.components.services;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.developer.bsince.log.GOL;
import com.x91tec.appshelf.components.AppHook;
import com.x91tec.appshelf.storage.IOUtils;

import java.io.File;

/**
 * Created by oeager on 16-3-5.
 */
public class SystemDownloader implements Downloader {

    private DownloadManager downloadManager;

    public SystemDownloader() {
        Application app = AppHook.getApp();
        if (app == null) {
            throw new IllegalArgumentException("AppHook's application is null,were you ensureApplication?");
        }
        downloadManager = (DownloadManager) app.getSystemService(Context.DOWNLOAD_SERVICE);


    }

    @Override
    public void download(final VersionResponse response, final UpCallback callback) {

        DownloadDatabase database = DownloadDatabase.get(AppHook.getApp());
        long taskId = database.matchTaskId(response.linkUri);
        GOL.e("matched taskId is :"+taskId);
        if (taskId >= 0) {
            response.tag = String.valueOf(taskId);
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(taskId);
            Cursor cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                IOUtils.closeQuietly(cursor);
                GOL.e("find record in system downloads :status:"+status);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    File downloadFile = new File(response.localUri+response.fileName);
                    if (downloadFile.exists()) {
                        GOL.e("file exists :complete");
                        callback.onCompletedUp(response);
                        return;
                    }
                }
                if (status == DownloadManager.STATUS_RUNNING) {
                    GOL.e("task running");
                    return;
                }
                if (status == DownloadManager.STATUS_FAILED || status == DownloadManager.STATUS_PAUSED) {
                    GOL.e("task pause-fail");
                    downloadManager.remove(taskId);
                    DownloadDatabase.get(AppHook.getApp()).deleteByTaskId(taskId);
                }
            }
        }
        File directory = new File(response.localUri);
        if (!directory.exists() || !directory.isDirectory()) {
            GOL.d("directory is not existed or  not a directory ,mk!");
            boolean flag = directory.mkdirs();
            if (flag) {
                throw new IllegalArgumentException("can not create directory:" + response.localUri);
            }
        }
        DownloadManager.Request request = versionResponse2Request(response);
        taskId = downloadManager.enqueue(request);
        response.tag = String.valueOf(taskId);
        DownloadDatabase.get(AppHook.getApp()).insertRecords(taskId, response.linkUri);
        Handler mHandler = new ProgressHandler(response, callback);
        mHandler.sendEmptyMessage(MSG_PROGRESS);
    }

    protected DownloadManager.Request versionResponse2Request(VersionResponse response){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(response.linkUri));
        request.setDestinationUri(Uri.fromFile(new File(response.localUri + response.fileName)));
        request.setTitle(response.title);
        request.setDescription(response.description);
        return request;
    }

    @Override
    public void cancel(VersionResponse response, UpCallback callback) {

        Application application = AppHook.getApp();
        assert application != null;
        long taskId = matchRealTaskId(application,response);
        downloadManager.remove(taskId);
        DownloadDatabase.get(application).deleteByTaskId(taskId);
        callback.onUpCancel(response);

    }

    long matchRealTaskId(Context context,VersionResponse response){
        try {
            if (TextUtils.isEmpty(response.tag)) {
                return DownloadDatabase.get(context).matchTaskId(response.linkUri);
            } else {
                return  Integer.parseInt(response.tag);
            }
        }catch (Exception e){
            return -1;
        }
    }

    static final int MSG_PROGRESS = 0;

    public class ProgressHandler extends Handler {
        private UpCallback callback;

        private VersionResponse response;

        private final long taskId;


        public ProgressHandler(VersionResponse response, UpCallback callback) {
            this.response = response;
            this.callback = callback;
            Application application = AppHook.getApp();
            assert application != null;
            taskId = matchRealTaskId(application,response);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_PROGRESS) {
                progressUp(response, callback);
            }
        }

        public int[] getBytesAndStatus(long downloadId) {
            int[] bytesAndStatus = new int[]{-1, -1, 0};
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
            Cursor c = null;
            try {
                c = downloadManager.query(query);
                if (c != null && c.moveToFirst()) {
                    bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            return bytesAndStatus;
        }

        void progressUp(VersionResponse response, UpCallback callback) {
            if(taskId<0){
                callback.onUpError(response);
                removeCallbacksAndMessages(null);
                return;
            }
            int[] datas = getBytesAndStatus(taskId);
            int status = datas[2];
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                callback.onCompletedUp(response);
                removeCallbacksAndMessages(null);
            } else if (status == DownloadManager.STATUS_FAILED) {
                callback.onUpError(response);
                removeCallbacksAndMessages(null);
            } else {
                response.fileSize = datas[1];
                callback.onProgressUp(response, datas[0]);
                sendEmptyMessageDelayed(MSG_PROGRESS, 1000);
            }
        }
    }


}
