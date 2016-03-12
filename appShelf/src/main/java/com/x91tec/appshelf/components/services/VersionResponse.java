package com.x91tec.appshelf.components.services;

import android.content.Context;

import com.developer.bsince.log.GOL;
import com.x91tec.appshelf.R;
import com.x91tec.appshelf.components.AppEnvironment;
import com.x91tec.appshelf.converter.TextUtils;
import com.x91tec.appshelf.storage.FileUtil;
import com.x91tec.appshelf.storage.SDCardUtils;

/**
 * Created by oeager on 16-3-4.
 */
public class VersionResponse {

    public final String linkUri;

    public final String localUri;

    public final String fileName;

    public final String tempName;

    public final String newVersionName;

    public final int newVersionCode;

    public final String versionName;

    public final int versionCode;

    public final boolean forceUpgrade;

    public final String title;

    public final String description;

    public final String releaseNote;

    public long fileSize;

    public String tag;

    public VersionResponse(Builder builder){
        linkUri = builder.linkUri;
        localUri = builder.localUri;
        fileName = builder.fileName;
        tempName = builder.tempName;
        newVersionName = builder.newVersionName;
        newVersionCode = builder.newVersionCode;
        forceUpgrade = builder.forceUpgrade;
        title = builder.title;
        description = builder.description;
        releaseNote = builder.releaseNote;
        versionName = AppEnvironment.VERSION_NAME;
        versionCode = AppEnvironment.VERSION_CODE;

    }
    public static class Builder {

        private String linkUri;

        private String localUri;

        private String fileName;

        private String tempName;

        private String title;

        private String description;

        private String newVersionName;

        private int newVersionCode;

        private String releaseNote;

        private boolean forceUpgrade = false;

        public Builder linkUri(String linkUri) {
            this.linkUri = linkUri;
            return this;
        }

        public Builder localUri(String localUri) {
            this.localUri = localUri;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder forceUpgrade(boolean forceUpgrade){
            this.forceUpgrade = forceUpgrade;
            return this;
        }

        public Builder newVersionName(String newVersionName){
            this.newVersionName = newVersionName;
            return this;
        }

        public Builder newVersionCode(int newVersionCode){
            this.newVersionCode = newVersionCode;
            return this;
        }

        public Builder releaseNote(String releaseNote){
            this.releaseNote = releaseNote;
            return this;
        }

        public VersionResponse build(Context mContext){
            if(TextUtils.isEmpty(linkUri)){
                throw new IllegalArgumentException("linkUri can not be null");
            }

            if (TextUtils.isEmpty(localUri)){
                if(!SDCardUtils.hasExternalStoragePermission(mContext)){
                    throw new IllegalArgumentException("localUri auto set should must have external storage permission");
                }
                localUri = SDCardUtils.getSDCardPath();
            }

            if(TextUtils.isEmpty(newVersionName)){
                if(AppEnvironment.VERSION_NAME.indexOf('.')!=-1){
                    int index = AppEnvironment.VERSION_NAME.lastIndexOf('.');
                    newVersionName = AppEnvironment.VERSION_NAME.substring(0,index+1)+newVersionCode;
                }
            }

            if(TextUtils.isEmpty(fileName)){
                fileName = FileUtil.getFileName(linkUri);
            }
            StringBuilder buffer = new StringBuilder();
            String fn = FileUtil.getFileNameNoFormat(linkUri);
            String format = FileUtil.getFileFormat(fileName);

            buffer.append(fn).append("_").append(newVersionName).append(".").append(format);
            fileName = buffer.toString();

            tempName = getTempFileName(fileName);

            if(TextUtils.isEmpty(title)){
                title = AppEnvironment.NAME;
            }

            if(TextUtils.isEmpty(description)){
                description = "app download";
            }
            if(TextUtils.isEmpty(releaseNote)){
                releaseNote = mContext.getResources().getString(R.string.empty_releaseNote);
            }

            VersionResponse ui= new VersionResponse(this);
            GOL.d(ui.toString());
            return ui;

        }


    }
    private static String getTempFileName(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex != -1) {
            String tempName = fileName.substring(0, lastIndex);
            return tempName + "_temp";
        } else {
            return fileName + "_temp";
        }
    }

    @Override
    public String toString() {
        return String.format("linkUrl[%1$s]\n" +
                "localUri[%2$s]\n" +
                "fileName[%3$s]\n" +
                "tempFileName[%4$s]\n" +
                "newVersionName[%5$s]\n" +
                "newVersionCode[%6$s]\n" +
                "currentVersionName[%7$s]\n" +
                "currentVersionCode[%8$d]\n" +
                "forceUpdate[%9$d]\n" +
                "title[%10$s]\n" +
                "description[%11$s]\n" +
                "releaseNote[%12$s]\n",linkUri,localUri,fileName,tempName,newVersionName,newVersionCode,versionName,versionCode,forceUpgrade?1:0,title,description,releaseNote);
    }
    public boolean hasNewVersion(){
        return versionCode<newVersionCode;
    }
}
