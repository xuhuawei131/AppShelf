package com.x91tec.appshelf.components.services;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by oeager on 16-3-8.
 */
public final class AppVersionController<T extends Activity>  {

    public static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    private WeakReference<T> mContext;

    private VersionResponse response;

    private UpCallback callback;

    private UpInteractive interactive;

    private Downloader downloader;

    private UIDetector uiDetector;

    private AppVersionController(T t){
        mContext = new WeakReference<T>(t);
    }

    public static <T extends Activity> AppVersionController<T> target(T t){
        return new AppVersionController<>(t);
    }

    public T getActivity(){
        return mContext.get();
    }

    public AppVersionController<T> response(VersionResponse response){
        this.response = response;
        return this;
    }

    public void startUpVersion(UpCallback callback){
        this.callback = callback;
        ensureParams();
        if(response.hasNewVersion()){
            uiDetector.requestGrantUp(getActivity(),response,interactive);
        }else {
            uiDetector.onUnnecessaryUp(getActivity(),response);
        }
    }

    public AppVersionController<T> upInteractive(UpInteractive interactive){
        this.interactive = interactive;
        return this;
    }

    public AppVersionController<T> downloader(Downloader downloader){
        this.downloader = downloader;
        return this;
    }

    public AppVersionController<T> ui(UIDetector uiDetector){
        this.uiDetector = uiDetector;
        return this;
    }

    void ensureParams(){
        if(this.response==null){
            throw new IllegalArgumentException("VersionResponse can not be null");
        }
        if(this.uiDetector==null){
            throw new IllegalArgumentException("UIDetector can not be null");
        }
        if(downloader==null){
            downloader = new SystemDownloader();
        }
        if(callback==null){
            callback = new EmptyUpCallback();
        }
        if(interactive==null){
            interactive = new UpInteractive() {
                @Override
                public void onAllowUp(VersionResponse response) {
                    callback.onStartUp(response);
                    downloader.download(response,callback);

                }

                @Override
                public void onForbidUp(VersionResponse response) {
                    callback.onUpCancel(response);
                }
            };
        }

    }

    public void cancel(VersionResponse response,UpCallback callback){
        if(downloader!=null){
            downloader.cancel(response,callback);
        }
    }

    public void cancel(){

        if(downloader!=null){
            if(callback==null){
                callback = new EmptyUpCallback();
            }
            downloader.cancel(response,callback);
        }
    }
}
