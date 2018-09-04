package com.example.feicui.testcamera;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataSaveImpl {

    public static String parentPath;

    public static void saveImage(byte[] bytes) {
        if (TextUtils.isEmpty(parentPath))
            return;

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
        String fileName = dateFormat.format(date);
        String filepath = (new File(parentPath, fileName + ".jpg")).getAbsolutePath();
        Log.d("DataSaveImpl","save image file = " + filepath);
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(filepath);
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
