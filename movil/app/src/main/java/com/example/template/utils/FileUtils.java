package com.example.template.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUtils {

    public static MultipartBody.Part getMultipartBody(Context context, Uri uri, String partName) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String mimeType = contentResolver.getType(uri);
            if (mimeType == null) {
                mimeType = "image/jpeg";
            }
            String fileName = getFileName(context, uri);
            File tempFile = new File(context.getCacheDir(), fileName);
            try (InputStream inputStream = contentResolver.openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), tempFile);
            return MultipartBody.Part.createFormData(partName, tempFile.getName(), requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileName(Context context, Uri uri) {
        String name = "temp_upload_file.jpg";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
}
