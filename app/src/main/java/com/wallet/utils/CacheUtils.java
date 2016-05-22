package com.wallet.utils;


import android.content.Context;
import android.os.Environment;

import java.io.File;

public class CacheUtils {

    public static File getCheckImageFolder(Context context) {
        String imageFolderName = ".nomedia";
        File rootFolder = context.getExternalFilesDir(null);
        if (rootFolder == null) {
            return Environment.getExternalStorageDirectory();
        }
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

        File cacheFolder = new File(rootFolder, imageFolderName);
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        return cacheFolder;
    }
}
